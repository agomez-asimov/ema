package ar.asimov.acumar.ema.wlk.test;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.dao.StationDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;
import ar.asimov.acumar.ema.services.WeatherDataConsumer;
import ar.asimov.acumar.ema.services.WeatherDataProducer;

public class ProducerConsumerTest {
	
	private Queue<WeatherMeasure> measures;

	@Before
	public void setUp() throws Exception {
		this.measures = new LinkedBlockingQueue<>();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test() {
		StationDAO stationDAO = new StationDAO(EntityManagerHelper.getEntityManager());
		List<Station> stations = stationDAO.fetchAll();
		final ArrayList<WeatherDataProducer> producers = new ArrayList<>();
		final ArrayList<WeatherDataConsumer> consumers = new ArrayList<>();
		final ArrayList<Thread> threads = new ArrayList<>();
		for(Station station : stations){
			WeatherDataProducer producer =new WeatherDataProducer(station,this.measures);
			producers.add(producer);
			threads.add(new Thread(producer));
			WeatherDataConsumer consumer = new WeatherDataConsumer(this.measures);
			consumers.add(consumer);
			threads.add(new Thread(consumer));
		}
		for(Thread thread : threads){
			thread.start();	
		}
		for(Thread thread : threads){
			try{
				thread.join();
				if(this.measures.isEmpty()){
					for(WeatherDataConsumer consumer : consumers){
						consumer.stop();
					}
				}
			}catch(InterruptedException e){
				e.printStackTrace();
				fail("Interrupted exception");
			}
		}
		
	}

}
