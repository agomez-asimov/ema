package ar.asimov.acumar.ema.services;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.DAOManager;


public class WeatherFileConsumer implements Callable<Integer> {

	private static final Log LOGGER = LogFactory.getLog(WeatherFileConsumer.class); 
	private static final int COMMIT_LIMIT = 1000;
	private static final int SLEEP_LIMIT = 500;
	private final BlockingQueue<WeatherFile> files;
	private boolean stop;

	public WeatherFileConsumer(BlockingQueue<WeatherFile> files) {
		this.stop = false;
		this.files = files;
	}


	protected WeatherFile consume() throws InterruptedException {
		while (this.files.isEmpty()){
			synchronized (this.files) {
				this.files.wait();
			}
		}
		synchronized (this.files) {
			this.files.notifyAll();
			return this.files.poll();
		}
	}
	
	private Log getLogger(){
		return WeatherFileConsumer.LOGGER;
	}

	@Override
	public Integer call() throws Exception {
		Integer localTotalProcessedRecords = 0;
		try {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("New consumer started");
			}
			Thread.sleep(1000);
			this.stop = (this.files.isEmpty());
			while(!this.stop) {
				WeatherFile file = this.consume();
				if(this.getLogger().isDebugEnabled()){
					if(null == file.getStation()){
						this.getLogger().debug("A null station has been provided for the file.");
					}else{
						this.getLogger().debug("Processing file "+file.getPeriod()+".wlk for Station "+file.getStation().getId());
					}
				}
				DAOManager.beginTransaction();
				int processedEntities = 0;
				for(WeatherData data : file.getWeatherData()){
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("Creating data ["+data.getStation().getId()+", "+data.getDate().toString()+", "+data.getStartTime().toString()+"]");
					}
					DAOManager.getDataDAO().create(data);
					processedEntities++;
					if(processedEntities == SLEEP_LIMIT && Thread.activeCount()>=Runtime.getRuntime().availableProcessors()){
						Thread.sleep(1000);
					}
					if(processedEntities == COMMIT_LIMIT){
						DAOManager.commitTransaction();
						DAOManager.beginTransaction();
						processedEntities = 0;
					}
					localTotalProcessedRecords++;
				}
				for(WeatherSummary summary : file.getWeatherSummaries()){
					if(DAOManager.getSummaryDAO().fetch(summary.getStation(),summary.getDate())!=null){
						if(this.getLogger().isDebugEnabled()){
							this.getLogger().debug("Updating summary ["+summary.getStation().getId()+", "+summary.getDate().toString()+"]");
						}
						DAOManager.getSummaryDAO().update(summary);
						processedEntities++;
					}else{
						if(this.getLogger().isDebugEnabled()){
							this.getLogger().debug("Creating summary ["+summary.getStation().getId()+", "+summary.getDate().toString()+"]");
						}
						DAOManager.getSummaryDAO().create(summary);
						processedEntities++;
					}
					if(processedEntities == SLEEP_LIMIT  && Thread.activeCount() >= Runtime.getRuntime().availableProcessors()){
						Thread.sleep(500);
					}
					if(processedEntities == COMMIT_LIMIT){
						DAOManager.commitTransaction();
						DAOManager.beginTransaction();
						processedEntities=0;
					}
				}
				if(null != DAOManager.getFileDAO().fetch(file.getStation(), file.getPeriod())){
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("Updated file ["+file.getStation().getId()+", "+file.getPeriod().toString()+".wlk]");
					}
					file.setDateUpdated(LocalDateTime.now());;
					DAOManager.getFileDAO().update(file);
				}else{
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("Created file ["+file.getStation().getId()+", "+file.getPeriod().toString()+".wlk]");
					}
					file.setDateCreated(LocalDateTime.now());
					file.setDateUpdated(LocalDateTime.now());
					DAOManager.getFileDAO().create(file);
				}
				if(DAOManager.isTransactionActive()){
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("Pushing changes to DB");
					}
					DAOManager.commitTransaction();
				}
				this.stop = (this.files.isEmpty());
			}
			return localTotalProcessedRecords;
		} catch (Exception e) {
			if(DAOManager.isTransactionActive()){
				DAOManager.rollBackTransaction();
			}
			this.getLogger().error("An error has been thrown",e);
			throw e;
			//HANDLE FREFRE
		} finally {
			DAOManager.close();
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("File consumer finished");
			}
		}		
	}
	

}
