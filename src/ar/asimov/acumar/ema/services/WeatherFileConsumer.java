package ar.asimov.acumar.ema.services;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.DAOManager;


public class WeatherFileConsumer implements Runnable {

	private static final Log LOGGER = LogFactory.getLog(WeatherFileConsumer.class); 
	
	private final BlockingQueue<WeatherFile> files;
	private boolean stop;
	private boolean running;

	public WeatherFileConsumer(BlockingQueue<WeatherFile> files) {
		this.stop = false;
		this.running = false;
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
	
	public void stop() {
		this.stop = true;
	}

	public boolean isRunning() {
		return running;
	}
	
	private Log getLogger(){
		return WeatherFileConsumer.LOGGER;
	}

	@Override
	public void run() {
		this.running = true;
		try {
			while(!this.stop) {
				WeatherFile file = this.consume();
				DAOManager.beginTransaction();
				for(WeatherData data : file.getWeatherData()){
					DAOManager.getDataDAO().create(data);
				}
				for(WeatherSummary summary : file.getWeatherSummaries()){
					if(DAOManager.getSummaryDAO().fetch(summary.getStation(),summary.getDate())!=null){
						DAOManager.getSummaryDAO().update(summary);
					}else{
						DAOManager.getSummaryDAO().create(summary);
					}
				}
				if(null != DAOManager.getFileDAO().fetch(file.getStation(), file.getPeriod())){
					DAOManager.getFileDAO().update(file);
				}else{
					DAOManager.getFileDAO().create(file);
				}
				this.stop = (this.files.isEmpty());
				DAOManager.commitTransaction();
			}
		} catch (InterruptedException e) {
			DAOManager.rollBackTransaction();
			//HANDLE FREFRE
		} finally {
			DAOManager.close();
			this.running = false;
		}		
	}
	

}
