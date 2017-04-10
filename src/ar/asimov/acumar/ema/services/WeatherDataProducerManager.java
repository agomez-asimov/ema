package ar.asimov.acumar.ema.services;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.sun.media.jfxmedia.logging.Logger;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.StationDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherDataProducerManager implements Runnable{
	
	private List<WeatherDataProducer> producers;
	private Queue<WeatherMeasure> measures;
	private List<Thread> threads;
	
	public WeatherDataProducerManager(Queue<WeatherMeasure> measures){
		this.measures = measures;
	}
	
	@Override
	public void run() {
		StationDAO stationDAO = new StationDAO(EntityManagerHelper.getEntityManager());
		List<Station> stations = stationDAO.fetchAll();
		for(Station station : stations){
			WeatherDataProducer producer = new WeatherDataProducer(station,this.measures);
			this.producers.add(producer);
			Thread t = new Thread(producer);
			this.threads.add(t);
			t.start();
		}
		for(Thread thread : threads){
			try{
				thread.join();
			}catch(InterruptedException e){
				throw new RuntimeException(e);
			}
		}
		for(WeatherDataProducer producer : this.producers){
			if(producer.getProduced() == 0){
				//TODO: NOTIFY THERE IS NO DATA AVAILABLE FOR THIS PRODUCER
			}
		}
	}
	
	
	
	
	

}
