package ar.asimov.acumar.ema;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.ConfigurationDAO;
import ar.asimov.acumar.ema.model.dao.ProcessLogDAO;
import ar.asimov.acumar.ema.model.dao.StationDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;
import ar.asimov.acumar.ema.services.ProcessInformation;
import ar.asimov.acumar.ema.services.WeatherDataConsumerManager;
import ar.asimov.acumar.ema.services.WeatherDataProducerManager;

public class WeatherController implements Runnable{
	private static final Log LOGGER = LogFactory.getLog(WeatherController.class);	 
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	public WeatherController() {
		new ConfigurationDAO(EntityManagerHelper.getEntityManager());
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
	
	protected Log getLogger(){
		return LOGGER;
	}

	@Override
	public void run() {
		final Queue<WeatherSummary> summaries = new LinkedBlockingQueue<>();
		final Queue<WeatherReport> measures = new LinkedBlockingQueue<>();
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
		final WeatherDataProducerManager producerManager = new WeatherDataProducerManager(measures,summaries, stations);
		final WeatherDataConsumerManager consumerManager = new WeatherDataConsumerManager(measures, 1000);
		this.executorService.schedule(producerManager,2,TimeUnit.SECONDS);
		this.executorService.schedule(consumerManager,5,TimeUnit.SECONDS);
		try{
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Waiting for producer thread to end");
			}
			while(!this.executorService.isTerminated()){
				this.executorService.awaitTermination(300, TimeUnit.SECONDS);
			}
	
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("Process finished about to update process log");
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
