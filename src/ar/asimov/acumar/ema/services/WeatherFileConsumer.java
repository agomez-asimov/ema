package ar.asimov.acumar.ema.services;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.DAOManager;


public class WeatherFileConsumer implements Runnable {

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
	public void run() {
		try {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("New consumer started");
			}
			while(!this.stop) {
				WeatherFile file = this.consume();
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Processing file "+file.getPeriod().toString()+".wlk for Station "+file.getStation().getId());
				}
				DAOManager.beginTransaction();
				int processedEntities = 0;
				for(WeatherData data : file.getWeatherData()){
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("Creating data ["+data.getStation().getId()+", "+data.getDate().toString()+", "+data.getStartTime().toString()+"]");
					}
					DAOManager.getDataDAO().create(data);
					processedEntities++;
					if(processedEntities == SLEEP_LIMIT && Thread.activeCount()>=2){
						Thread.sleep(500);
					}
					if(processedEntities == COMMIT_LIMIT){
						DAOManager.commitTransaction();
						DAOManager.beginTransaction();
						processedEntities = 0;
					}
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
					if(processedEntities == SLEEP_LIMIT  && Thread.activeCount() >= 2){
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
				DAOManager.commitTransaction();
				this.stop = (this.files.isEmpty());
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Pushing changes to DB");
				}
			}
		} catch (InterruptedException e) {
			DAOManager.rollBackTransaction();
			this.getLogger().error("An error has been thrown",e);
			//HANDLE FREFRE
		} finally {
			DAOManager.close();
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("File consumer finished");
			}
		}		
	}
	

}
