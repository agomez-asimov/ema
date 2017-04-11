package ar.asimov.acumar.ema.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.dao.ProcessLogDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherDataProducerManager implements Runnable{
	
	
	private Map<WeatherDataProducer,Thread> producers;
	private Queue<WeatherMeasure> measures;
	private Map<Station,ProcessInformation> produced;
	private List<Station> stations;
	private boolean running;
	
	public WeatherDataProducerManager(Queue<WeatherMeasure> measures,List<Station> stations){
		this.measures = measures;
		this.stations = stations;
		this.producers = new HashMap<>();
		this.produced = new HashMap<>();
	}
	
	@Override
	public void run() {
		this.running = true;
		final ProcessLogDAO logSet = new ProcessLogDAO(EntityManagerHelper.getEntityManager());
		for(Station station : this.stations){
			ProcessLog lastLog =  logSet.fetchLast(station);
			WeatherDataProducer producer = new WeatherDataProducer(station,this.measures,(null == lastLog)?null:lastLog.getLastDateProcessed(),(null == lastLog)?null:lastLog.getLastProcessedRecords());
			Thread t = new Thread(producer);
			this.producers.put(producer, t);
			t.start();
		}
		for(Thread thread : producers.values()){
			try{
				thread.join();
			}catch(InterruptedException e){
				throw new RuntimeException(e);
			}
		}
		for(WeatherDataProducer producer : this.producers.keySet()){
			ProcessInformation processInfo = new ProcessInformation();
			processInfo.setLastProcessedDate(producer.getLastProcessedDate());
			processInfo.setLastProcessedRecords(producer.getlastProcessedRecords());
			processInfo.setTotalProcessed(producer.getProduced());
			this.produced.put(producer.getStation(),processInfo);
		}
		this.running = false;
	}
	
	public ProcessInformation getInformation(Station station){
		return this.produced.get(station);
	}
	

	public void stop(){
		for(WeatherDataProducer producer : this.producers.keySet()){
			producer.stop();
		}
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
}
