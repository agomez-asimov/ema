package ar.asimov.acumar.ema.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;


public class WeatherDataConsumerManager implements Runnable{
	


	private static final Integer DEFAULT_LIMIT = 10000;
	
	private Map<WeatherDataConsumer,Thread> consumers;
	private Queue<WeatherMeasure> measures;
	private Map<Station,ProcessInformation> consumed;
	private int limit;
	private boolean stop;
	private boolean running;
	
	public WeatherDataConsumerManager(Queue<WeatherMeasure> measures,Integer limit){
		this.measures = measures;
		this.consumers = new HashMap<>();
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
			synchronized(this.measures){
				try{
					this.measures.wait();
					
				}catch(InterruptedException e){
					throw new RuntimeException(e);
				}
			}
		}
		while(!this.stop && !this.measures.isEmpty()){
			Integer currentSize = this.measures.size();
			if(Math.round(currentSize / this.limit) > this.consumers.size()){
				WeatherDataConsumer consumer = new WeatherDataConsumer(this.measures, this.limit);
				Thread thread = new Thread(consumer);
				this.consumers.put(consumer, thread);
				thread.start();
			}
			if(this.stop){
				for(WeatherDataConsumer consumer : this.consumers.keySet()){
					consumer.stop();
				}
			}
			for(Thread t : this.consumers.values()){
				try{
					t.join();			
				}catch(InterruptedException e){
					this.running = false;
					throw new RuntimeException(e);
				}
			}
		}
		for(WeatherDataConsumer consumer : this.consumers.keySet()){
			Map<Station,ProcessInformation> information = consumer.getProcessInformation();
			for(Station station : information.keySet()){
				if(this.consumed.containsKey(station)){
					this.consumed.put(station, information.get(station));
				}else{
					ProcessInformation localInfo = this.consumed.get(station);
					ProcessInformation currentInfo = information.get(station);
					if(localInfo.getLastProcessedDate().isBefore(currentInfo.getLastProcessedDate())){
						localInfo.setLastProcessedDate(currentInfo.getLastProcessedDate());
						localInfo.setLastProcessedRecords(currentInfo.getLastProcessedRecords());
					}else if(localInfo.getLastProcessedDate().isEqual(currentInfo.getLastProcessedDate())){
						localInfo.setLastProcessedRecords(localInfo.getLastProcessedRecords()+currentInfo.getLastProcessedRecords());
					}
					localInfo.setTotalProcessed(localInfo.getTotalProcessed()+currentInfo.getTotalProcessed());
				}
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
	

}
