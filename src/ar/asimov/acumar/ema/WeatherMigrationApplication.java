	package ar.asimov.acumar.ema;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.dao.DAOManager;
import ar.asimov.acumar.ema.services.WeatherFileConsumer;
import ar.asimov.acumar.ema.services.WeatherFileProducer;

public class WeatherMigrationApplication {
	
	private static final int REPORTS_QUEUE_CAPACITY = 50;

	public static void main(String[] args) {
		final ArrayBlockingQueue<WeatherFile> files = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY);
		final List<Station> stations = DAOManager.getStationDAO().fetchAll();
		final ExecutorService executors = Executors.newFixedThreadPool(stations.size()*2);
		for(Station station : stations){
			WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
			WeatherFileProducer producer = new WeatherFileProducer(station,files,lastFile);
			executors.execute(producer);
			WeatherFileConsumer consumer = new WeatherFileConsumer(files);
			executors.execute(consumer);
		}
		executors.shutdown();
	}
	
}
