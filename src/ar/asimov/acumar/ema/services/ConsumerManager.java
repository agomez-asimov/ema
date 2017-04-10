package ar.asimov.acumar.ema.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;

public class ConsumerManager implements Runnable{
	
	private static final Integer DEFAULT_LIMIT = 10000;
	
	private List<Thread> threads;
	private List<WeatherDataConsumer> consumers;
	private Queue<WeatherMeasure> measures;
	private Map<Station,Integer> processed;
	private int limit;
	private boolean stop;
	
	public ConsumerManager(Queue<WeatherMeasure> measures,Integer limit){
		this.measures = measures;
		this.consumers = new ArrayList<>();
		this.threads = new ArrayList<>();
		if(null == limit){
			this.limit = DEFAULT_LIMIT;
		}else{
			this.limit = limit;
		}
	}

	@Override
	public void run() {		
		while(this.measures.isEmpty()){
			synchronized(this.measures){
				try{
					this.measures.wait();
					
				}catch(InterruptedException e){
					throw new RuntimeException(e);
				}
			}
		}
		while(!this.stop && !this.measures.isEmpty()){
			Integer size = this.measures.size();
			
		}
	}
	
	
	

}
