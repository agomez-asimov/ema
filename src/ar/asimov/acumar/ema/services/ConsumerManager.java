package ar.asimov.acumar.ema.services;

import java.util.List;
import java.util.Queue;

import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.WeatherSummary;

public class ConsumerManager implements Runnable{
	
	private List<Thread> threads;
	private List<WeatherDataConsumer> consumers;
	
	private Queue<WeatherMeasure> measures;
	private Queue<WeatherSummary> summaries; 

	@Override
	public void run() {	
		
	}
	
	
	

}
