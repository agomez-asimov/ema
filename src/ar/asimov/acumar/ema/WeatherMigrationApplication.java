package ar.asimov.acumar.ema;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherDailyReport;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.dao.DAOManager;
import ar.asimov.acumar.ema.services.WeatherReportConsumer;
import ar.asimov.acumar.ema.services.WeatherReportProducer;

public class WeatherMigrationApplication {
	
	private static final int REPORTS_QUEUE_CAPACITY = 10000;

	public static void main(String[] args) {
		final ArrayBlockingQueue<WeatherReport> weatherReports = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY);
		final ArrayBlockingQueue<WeatherDailyReport> weatherDailyReports = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY); 
		final List<Station> stations = DAOManager.getStationDAO().fetchAll();
		final ScheduledExecutorService executors = Executors.newScheduledThreadPool(stations.size()*2); 
		for(Station station : stations){
			WeatherDailyReport lastDailyReport = DAOManager.getWeatherDailyReportDAO().fetchLast(station);
			WeatherReportProducer producer = new WeatherReportProducer(station, weatherReports, weatherDailyReports, lastDailyReport);
			WeatherReportConsumer consumer = new WeatherReportConsumer(station, weatherReports, weatherDailyReports, lastDailyReport);
			executors.schedule(producer, 0, TimeUnit.NANOSECONDS);
			executors.schedule(consumer,50,TimeUnit.MILLISECONDS);
		}
	}

}
