	package ar.asimov.acumar.ema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.ProcessInformation;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.dao.DAOManager;
import ar.asimov.acumar.ema.services.WeatherFileConsumer;
import ar.asimov.acumar.ema.services.WeatherFileProducer;

public class WeatherMigrationApplication {
	
	private static final int REPORTS_QUEUE_CAPACITY = 50;
	private static final Log LOGGER = LogFactory.getLog(WeatherMigrationApplication.class);
	
	public static Log getLogger(){
		return LOGGER;
	}

	public static void main(String[] args) {
		final ArrayBlockingQueue<WeatherFile> files = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY);
		final List<Station> stations = DAOManager.getStationDAO().fetchAll();
		for(Station station : stations){
			ProcessInformation processInformation = new ProcessInformation();
			processInformation.setStation(station);
			processInformation.setStart(LocalDateTime.now());
			processInformation.setAbnormalTermination(false);
			Integer totalConsumed = 0;
			Integer totalProduced = 0;
			try{
				final ThreadPoolExecutor executors = (ThreadPoolExecutor)Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
				WeatherFileProducer producer = new WeatherFileProducer(station,files,lastFile);
				Thread.sleep(10000);
				final List<Future<Integer>> consumersResults = new ArrayList<>();
				Future<Integer> producerResult = executors.submit(producer);
				while((executors.getActiveCount() < executors.getMaximumPoolSize()|| consumersResults.isEmpty()) && !files.isEmpty()){
					WeatherFileConsumer consumer = new WeatherFileConsumer(files);
					consumersResults.add(executors.submit(consumer));
				}
				executors.shutdown();
				totalProduced = producerResult.get();
				if(totalProduced == 0){
					//TODO: SEND MAIL 
				}
				for(Future<Integer> result : consumersResults){
					totalConsumed+=result.get();
				}
			}catch(ExecutionException | InterruptedException | CancellationException e){
				getLogger().error("An exception has been thrown",e);
				processInformation.setAbnormalTermination(true);
				processInformation.setAbnormalTemrminationCause(e.getMessage());
			}finally{
				processInformation.setEnd(LocalDateTime.now());
				processInformation.setProcessedRecords(totalConsumed);
				DAOManager.beginTransaction();
				DAOManager.getProcessInformationDAO().create(processInformation);
				DAOManager.commitTransaction();
			}

		}

	}
	
}
