package ar.asimov.acumar.ema.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.dao.WeatherMeasureDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherDataConsumer implements Runnable {

	private Queue<WeatherMeasure> measures;
	private Map<Station,ProcessInformation> processInfo;
	private boolean stop;
	private boolean running;
	private int processLimit;
	private int consumed;

	public WeatherDataConsumer(Queue<WeatherMeasure> sharedQueue, int processLimit) {
		this.measures = sharedQueue;
		this.stop = false;
		this.running = false;
		this.processLimit = processLimit;
		this.consumed = 0;
		this.processInfo = new HashMap<>();
	}

	@Override
	public void run() {
		this.running = true;
		boolean commit = false;
		// EntityManagerHelper.beginTransaction();
		final WeatherMeasureDAO measures = new WeatherMeasureDAO(EntityManagerHelper.getEntityManager());
		try {
			EntityManagerHelper.beginTransaction();
			final List<WeatherMeasure> localConsumed = new ArrayList<>();
			while (!this.stop && !this.measures.isEmpty() && !commit) {
				WeatherMeasure consumed = this.consume();
				measures.create(consumed);
				localConsumed.add(consumed);
				commit = (this.consumed == this.processLimit);
				Thread.sleep(50);
			}
			EntityManagerHelper.commitTransaction();
			for(WeatherMeasure m : localConsumed){
				if(!this.processInfo.containsKey(m.getStation())){
					this.processInfo.put(m.getStation(), new ProcessInformation());
				}
				ProcessInformation info = this.processInfo.get(m.getStation());
				if(info.getLastProcessedDate() == null || info.getLastProcessedDate().isBefore(m.getDate())){
					info.setLastProcessedDate(m.getDate());
					info.setLastProcessedRecords(1);
				}else if(info.getLastProcessedDate().equals(m.getDate())){
					info.setLastProcessedRecords(info.getLastProcessedRecords()+1);
				}
				info.setTotalProcessed(info.getTotalProcessed()+1);
			}
		} catch (InterruptedException e) {
			EntityManagerHelper.rollbackTransaction();
			throw new RuntimeException(e);
		} finally {
			EntityManagerHelper.closeEntityManager();
			this.running = false;
		}
	}

	protected WeatherMeasure consume() throws InterruptedException {
		while (this.measures.isEmpty()) {
			synchronized (this.measures) {
				this.measures.wait();
			}
		}
		synchronized (this.measures) {
			this.measures.notifyAll();
			return this.measures.poll();
		}
	}

	public Integer getConsumed(Station station) {
		return this.consumed;
	}

	public Map<Station,ProcessInformation> getProcessInformation(){
		return this.processInfo;
	}

	public void stop() {
		this.stop = true;
	}

	public boolean isRunning() {
		return running;
	}

}
