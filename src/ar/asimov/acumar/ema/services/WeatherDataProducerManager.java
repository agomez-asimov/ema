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

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.ProcessLogDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherDataProducerManager implements Runnable{
	

	private ExecutorService executorService = Executors.newFixedThreadPool(15);
	private Queue<WeatherReport> reports;
	private Queue<WeatherSummary> summaries;
	private Map<Station,ProcessInformation> produced;
	private List<Station> stations;
	private boolean running;
	
	public WeatherDataProducerManager(Queue<WeatherReport> reports,Queue<WeatherSummary> summaries,List<Station> stations){
		this.reports = reports;
		this.summaries = summaries;
		this.stations = stations;
		this.produced = new HashMap<>();
	}
	
	@Override
	public void run() {
		this.running = true;
		final ProcessLogDAO logSet = new ProcessLogDAO(EntityManagerHelper.getEntityManager());
		final List<Future<ProcessInformation>> producersPromesses = new ArrayList<>();
		for(Station station : this.stations){
			ProcessLog lastLog =  logSet.fetchLast(station);
			WeatherDataProducer producer = new WeatherDataProducer(station,this.reports,(null == lastLog)?null:lastLog.getLastDateProcessed(),(null == lastLog)?null:lastLog.getLastProcessedRecords());
			producersPromesses.add(executorService.submit(producer));
		}
		for(Future<ProcessInformation> promess : producersPromesses){
			try{
				ProcessInformation information = promess.get(); 
				this.produced.put(information.getStation(), information);
			}catch(InterruptedException | ExecutionException e){
				this.running = false;
				throw new RuntimeException(e);
			}
		}
	}
	
	public ProcessInformation getInformation(Station station){
		return this.produced.get(station);
	}
	

	public void stop(){
		this.executorService.shutdown();
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
}
