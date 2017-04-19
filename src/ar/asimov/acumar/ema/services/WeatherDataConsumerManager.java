package ar.asimov.acumar.ema.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.WeatherSummary;


public class WeatherDataConsumerManager implements Runnable{
	
	private static final Log LOGGER = LogFactory.getLog(WeatherDataConsumerManager.class);

	private static final Integer DEFAULT_LIMIT = 10000;
	private static final Integer THREAD_POOL_LIMIT = 50;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_LIMIT);
	private Queue<WeatherReport> measures;
	private Queue<WeatherSummary> summaries;
	private Map<Station,ProcessInformation> consumed;
	private int limit;
	private boolean stop;
	private boolean running;
	
	public WeatherDataConsumerManager(Queue<WeatherReport> measures,Integer limit){
		this.measures = measures;
		if(null == limit){
			this.limit = DEFAULT_LIMIT;
		}else{
			this.limit = limit;
		}
		this.consumed = new HashMap<>();
	}

   @Override
	public void run() {		
	   this.running =true;
		while(!this.stop && this.measures.isEmpty()){
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Waiting for measures to read");
			}
			synchronized(this.measures){
				try{
					this.measures.wait();
					
				}catch(InterruptedException e){
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("An InterruptedException happened",e);
					}
					throw new RuntimeException(e);
				}
			}
		}
		final List<Future<List<ProcessInformation>>> consumerPromesses = new ArrayList<>();
		while(!this.stop && !this.measures.isEmpty()){
			Integer currentSize = this.measures.size();
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Current measures size is "+currentSize+ " and I have "+consumerPromesses.size()+" consumers working at the moment");
			}
			if(Math.round(currentSize / this.limit) > consumerPromesses.size() || consumerPromesses.size() == 0){
				WeatherDataConsumer consumer = new WeatherDataConsumer(this.measures, this.limit);
				consumerPromesses.add(executorService.submit(consumer));
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("New consumer thread created ("+")"); 
				}
			}
			if(this.stop){
				this.executorService.shutdown();
			}
		}
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("Main loop exited [stop]="+String.valueOf(this.stop)+" [measures.size]="+this.measures.size());
		}
		for(Future<List<ProcessInformation>> promess : consumerPromesses ){
			try{
				final List<ProcessInformation> informationList = promess.get();
				for(ProcessInformation information : informationList){
					if(this.consumed.containsKey(information.getStation())){
						this.consumed.put(information.getStation(),information);
					}else{
						ProcessInformation previousInformation = this.consumed.get(information.getStation());
						if(!previousInformation.getLastProcessedDate().isAfter(information.getLastProcessedDate())){
							if(previousInformation.getLastProcessedDate().isEqual(information.getLastProcessedDate())){
								int curentLastProceessedRecords = information.getLastProcessedRecords();
								int currentTotalProcessedRecords = information.getTotalProcessed();
								information.setLastProcessedRecords(previousInformation.getLastProcessedRecords()+curentLastProceessedRecords);
								information.setTotalProcessed(previousInformation.getTotalProcessed()+currentTotalProcessedRecords);
							}
							this.consumed.put(information.getStation(),information);
						}
					}
				}
			}catch(InterruptedException | ExecutionException e){
				this.running = false;
				throw new RuntimeException(e);
			}
		}
		this.running = false;
	}
	
	public ProcessInformation getInformation(Station station){
		return this.consumed.get(station);
	}
	
	public void stop(){
		this.stop = true;
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
	private Log getLogger(){
		return WeatherDataConsumerManager.LOGGER;
	}

}
