package ar.asimov.acumar.ema;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.dao.ConfigurationDAO;
import ar.asimov.acumar.ema.model.dao.ProcessLogDAO;
import ar.asimov.acumar.ema.model.dao.StationDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;
import ar.asimov.acumar.ema.services.ProcessInformation;
import ar.asimov.acumar.ema.services.WeatherDataConsumerManager;
import ar.asimov.acumar.ema.services.WeatherDataProducerManager;

public class WeatherController implements Runnable{
	
	private ConfigurationDAO configurations;
	private Queue<WeatherMeasure> measures;
	private Queue<String> messages;
	
	public WeatherController() {
		this.configurations =new ConfigurationDAO(EntityManagerHelper.getEntityManager());
		this.measures = new LinkedBlockingQueue<>();
	}
	
	public static void main(String[] args) {
		WeatherController controller = new WeatherController();
		Thread t = new Thread(controller);
		t.start();
		try{
			t.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		final StationDAO stationSet = new StationDAO(EntityManagerHelper.getEntityManager());
		final ProcessLogDAO logSet = new ProcessLogDAO(EntityManagerHelper.getEntityManager());
		List<Station> stations = stationSet.fetchAll();
		final Map<Station,ProcessLog> logs = new HashMap<>();
		for(Station station : stations){
			ProcessLog log = new ProcessLog();
			log.setStation(station);
			log.setStart(Instant.now());
			logs.put(station,log);
		}
		final WeatherDataProducerManager producerManager = new WeatherDataProducerManager(this.measures, stations);
		final WeatherDataConsumerManager consumerManager = new WeatherDataConsumerManager(this.measures, 1000);
		Thread producerManagerThread = new Thread(producerManager,"Producer Manager");
		Thread consumerManagerThread = new Thread(consumerManager,"Consumer Manager");
		producerManagerThread.start();
		consumerManagerThread.start();
		try{
			producerManagerThread.join();
			if(this.measures.isEmpty()){
				System.out.println("IM HERE");
				consumerManager.stop();
			}else{
				System.out.println("IM HERE NOW");
				consumerManagerThread.join();
			}
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
		for(Station station : stations){
			ProcessLog lastLog = logSet.fetchLast(station);
			ProcessLog currentLog = logs.get(station);
			ProcessInformation producerInformation  = producerManager.getInformation(station);
			ProcessInformation consumerInformation = consumerManager.getInformation(station);
			if(null != producerInformation && null != consumerInformation){
				if(producerInformation.getTotalProcessed() != consumerInformation.getTotalProcessed()){
					currentLog.setAbnormalCompletion(true);
					currentLog.setAbnormalCompletionCause("Los registros leidos "+producerInformation.getTotalProcessed()+" no coincide con los registros procesados "+consumerInformation.getTotalProcessed());
				}
				currentLog.setEnd(Instant.now());
				currentLog.setLastProcessedRecords(consumerInformation.getLastProcessedRecords());
				currentLog.setLastDateProcessed(consumerInformation.getLastProcessedDate());
				EntityManagerHelper.beginTransaction();
				logSet.create(currentLog);
				EntityManagerHelper.commitTransaction();
				if(lastLog.getLastProcessedRecords() == 0 && currentLog.getLastProcessedRecords() == 0){
					//TODO: SEND FUCKING MAIL
				}
			}
		}
	}

}
