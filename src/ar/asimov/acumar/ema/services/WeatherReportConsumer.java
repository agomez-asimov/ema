package ar.asimov.acumar.ema.services;

import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherDailyReport;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.dao.DAOManager;

public class WeatherReportConsumer implements Runnable {

	private static final Log LOGGER = LogFactory.getLog(WeatherReportConsumer.class); 
	
	private final BlockingQueue<WeatherReport> reports;
	private final BlockingQueue<WeatherDailyReport> dailyReports;
	private final WeakReference<Station> station;
	private final WeatherDailyReport lastDailyReport;
	private boolean stop;
	private boolean running;

	public WeatherReportConsumer(Station station,BlockingQueue<WeatherReport> weatherReports,BlockingQueue<WeatherDailyReport> dailyReports, WeatherDailyReport lastDailyReport) {
		this.reports = weatherReports;
		this.dailyReports = dailyReports;
		this.stop = false;
		this.running = false;
		this.station = new WeakReference<>(station);
		this.lastDailyReport = lastDailyReport;
	}


	protected WeatherReport consumeWeatherReport() throws InterruptedException {
		while (this.reports.isEmpty()){
			synchronized (this.reports) {
				this.reports.wait();
			}
		}
		while(!this.reports.peek().getStation().equals(this.getStation())) {
			synchronized(this.reports){
				this.reports.notifyAll();
				Thread.sleep(100);
			}
		}
		synchronized (this.reports) {
			this.reports.notifyAll();
			return this.reports.poll();
		}
	}
	
	protected WeatherDailyReport consumeWeatherDailyReport() throws InterruptedException{
		while(this.dailyReports.isEmpty()){
			synchronized(this.dailyReports){
				this.dailyReports.wait();
			}
		}
		while(!this.dailyReports.peek().getStation().equals(this.getStation())){
			this.dailyReports.notifyAll();
			Thread.sleep(100);
		}
		synchronized(this.dailyReports){
			this.dailyReports.notifyAll();
			return this.dailyReports.poll();
		}
	}

	public void stop() {
		this.stop = true;
	}

	public boolean isRunning() {
		return running;
	}
	
	private Log getLogger(){
		return WeatherReportConsumer.LOGGER;
	}

	@Override
	public void run() {
		if(this.getLogger().isInfoEnabled()){
			this.getLogger().info("WeatherReportConsumer started");
		}
		final ProcessLog processLog = new ProcessLog();
		processLog.setStation(this.station.get());
		processLog.setStart(Instant.now());
		this.running = true;
		try {
			DAOManager.startTransaction();
			while (!this.stop) {
				WeatherDailyReport dailyReport = this.consumeWeatherDailyReport();
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Consumed DailyReport "+dailyReport.getStation().getId()+" "+dailyReport.getDate().toString());
				}
				if(dailyReport.equals(lastDailyReport)){
					DAOManager.getWeatherDailyReportDAO().update(dailyReport);
				}else{
					DAOManager.getWeatherDailyReportDAO().create(dailyReport);
				}
				WeatherReport report = this.consumeWeatherReport();
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Consumed Report "+report.getStation().getId()+" "+report.getDate().toString()+" "+report.getStartTime().toString());
				}
				DAOManager.getWeatherReportDAO().create(report);
				this.stop = (this.dailyReports.isEmpty() && this.reports.isEmpty());
			}
			DAOManager.commitTransaction();
		} catch (InterruptedException e) {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("An IOException occurred in Consumer",e);
			}
			DAOManager.rollBackTransaction();
			throw new RuntimeException(e);
		} finally {
			DAOManager.close();
			this.running = false;
		}		
	}
	
	private Station getStation(){
		return this.station.get();
	}

}
