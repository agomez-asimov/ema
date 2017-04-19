package ar.asimov.acumar.ema.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.WeatherMeasureDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherDataConsumer implements Callable<List<ProcessInformation>> {

	private static final Log LOGGER = LogFactory.getLog(WeatherDataConsumer.class); 
	
	private Queue<WeatherReport> measures;
	private boolean stop;
	private boolean running;
	private int processLimit;
	private int consumed;

	public WeatherDataConsumer(Queue<WeatherReport> sharedQueue,int processLimit) {
		this.measures = sharedQueue;
		this.stop = false;
		this.running = false;
		this.processLimit = processLimit;
		this.consumed = 0;
	}


	protected WeatherReport consume() throws InterruptedException {
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

	public void stop() {
		this.stop = true;
	}

	public boolean isRunning() {
		return running;
	}
	
	private Log getLogger(){
		return WeatherDataConsumer.LOGGER;
	}

	@Override
	public List<ProcessInformation> call() throws Exception {
		final Map<Station,ProcessInformation> information = new HashMap<Station,ProcessInformation>();
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("Weather consumer started");
		}
		this.running = true;
		boolean commit = false;
		final WeatherMeasureDAO measures = new WeatherMeasureDAO(EntityManagerHelper.getEntityManager());
		try {
			EntityManagerHelper.beginTransaction();
			final List<WeatherReport> localConsumed = new ArrayList<>();
			while (!this.stop && !this.measures.isEmpty() && !commit) {
				WeatherReport consumed = this.consume();
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Consumed weather measure for "+consumed.getStation().getId()+" on "+consumed.getDate() + " from "+consumed.getStartTime().toString() + " to "+consumed.getEndTime().toString());
				}
				measures.create(consumed);
				localConsumed.add(consumed);
				commit = (localConsumed.size() == this.processLimit);
				Thread.sleep(50);
			}
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Consume loop exited for Consumer status [stop]="+String.valueOf(this.stop)+" [measuresEmpty]="+this.measures.isEmpty()+" [commit]="+commit);
			}
			EntityManagerHelper.commitTransaction();
			this.consumed = localConsumed.size();
			for(WeatherReport m : localConsumed){
				if(!information.containsKey(m.getStation())){
					ProcessInformation info = new ProcessInformation();
					info.setStation(m.getStation());
					information.put(m.getStation(), info);
				}
				ProcessInformation info = information.get(m.getStation());
				if(info.getLastProcessedDate() == null || info.getLastProcessedDate().isBefore(m.getDate())){
					info.setLastProcessedDate(m.getDate());
					info.setLastProcessedRecords(1);
				}else if(info.getLastProcessedDate().equals(m.getDate())){
					info.setLastProcessedRecords(info.getLastProcessedRecords()+1);
				}
				info.setTotalProcessed(info.getTotalProcessed()+1);
			}
			return new ArrayList<>(information.values());
		} catch (InterruptedException e) {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("An IOException occurred in Consumer",e);
			}
			EntityManagerHelper.rollbackTransaction();
			throw new RuntimeException(e);
		} finally {
			EntityManagerHelper.closeEntityManager();
			this.running = false;
		}
	}

}
