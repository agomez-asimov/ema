package ar.asimov.acumar.ema.wlk.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Before;
import org.junit.Test;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.dao.StationDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;
import ar.asimov.acumar.ema.services.WeatherDataConsumer;
import ar.asimov.acumar.ema.services.WeatherDataProducer;

public class ProducerTest {

	private ConcurrentLinkedQueue<WeatherMeasure> queue;
	private boolean stillProducing;
	
	@Before
	public void initialize(){
		this.queue = new ConcurrentLinkedQueue<>();
		this.stillProducing = true;
	}

	@Test
	public void testRun() {
		StationDAO stationDAO = new StationDAO(EntityManagerHelper.getEntityManager());
		List<Station> stations = stationDAO.fetchAll();
		List<Thread> threads = new ArrayList<>();
		for(Station station : stations){
			threads.add(new Thread(new WeatherDataProducer(station, this.queue),"La Boca Producer"));
		}
		Thread c1 = new Thread(new WeatherDataConsumer(this.queue,1000),"Consumer 1");
		Thread c2 = new Thread(new WeatherDataConsumer(this.queue,1000),"Consumer 2");
		threads.add(c1);
		threads.add(c2);
		for(Thread thread : threads){
			thread.start();
		}
		for(Thread thread : threads){
			try{
				thread.join();
			}catch(InterruptedException e){
				e.printStackTrace();
			}	
		}		
	}

}
