package ar.asimov.acumar.ema;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.ConfigurationDAO;
import ar.asimov.acumar.ema.model.dao.StationDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;
import ar.asimov.acumar.ema.services.WeatherDataConsumer;
import ar.asimov.acumar.ema.services.WeatherDataProducer;

public class WeatherController implements Runnable{
	
	private ConfigurationDAO configurations;
	private StationDAO stations;
	private Queue<WeatherMeasure> measures;
	private Queue<WeatherSummary> summaries;
	private Queue<String> messages;
	
	public WeatherController() {
		this.configurations =new ConfigurationDAO(EntityManagerHelper.getEntityManager());
		this.stations = new StationDAO(EntityManagerHelper.getEntityManager());
	}
	
	public static void main(String[] args) {
		WeatherController controller = new WeatherController();
		Thread t = new Thread(controller);
		t.start();
		try{
			t.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		final List<Station> stations = this.stations.fetchAll();
		final List<WeatherDataProducer> producers = new ArrayList<>();
		final List<WeatherDataConsumer> consumers = new ArrayList<>();
		final List<Thread> threads = new ArrayList<>();
		for(Station station : stations){
			WeatherDataProducer producer = new WeatherDataProducer(station, measures);
			WeatherDataConsumer consumer = new WeatherDataConsumer(measures,1000);
			producers.add(producer);
			threads.add(new Thread(producer));
			threads.add(new Thread(consumer));
		}
		for(Thread thread : threads){
			thread.start();
		}
		while(this.measures.isEmpty()){
			
		}
		int iThread = 0;
		
		while(!this.measures.isEmpty() || !this.summaries.isEmpty()){
			if(iThread<threads.size() && threads.get(iThread).isAlive()){
				try{
					threads.get(iThread).join(200);	
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
			}else{
				iThread++;
			}
		}
		EntityManagerHelper.beginTransaction();
		for(Station station : stations){
			this.stations.update(station);
		}
		EntityManagerHelper.commitTransaction();
		
	}

}
