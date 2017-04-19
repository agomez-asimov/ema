package ar.asimov.acumar.ema.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.dao.WeatherMeasureDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherReportConsumer implements Callable<List<ProcessInformation>> {

	private static final Log LOGGER = LogFactory.getLog(WeatherReportConsumer.class); 
	
	private final BlockingQueue<WeatherReport> reports;
	private final Station station;
	private boolean stop;
	private boolean running;
	private int processLimit;
	private int consumed;

	public WeatherReportConsumer(Station station,BlockingQueue<WeatherReport> sharedQueue,int processLimit) {
		this.reports = sharedQueue;
		this.stop = false;
		this.running = false;
		this.processLimit = processLimit;
		this.consumed = 0;
		this.station = station;
	}


	protected WeatherReport consume() throws InterruptedException {
		while (this.reports.isEmpty() || !this.reports.peek().getStation().equals(this.station)) {
			synchronized (this.reports) {
				this.reports.wait();
			}
		}
		synchronized (this.reports) {
			this.reports.notifyAll();
			return this.reports.poll();
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
		return WeatherReportConsumer.LOGGER;
	}

	@Override
	public List<ProcessInformation> call() throws Exception {
		final Map<Station,ProcessInformation> information = new HashMap<Station,ProcessInformation>();
		if(this.getLogger().isInfoEnabled()){
			this.getLogger().info("WeatherReportConsumer started");
		}
		this.running = true;
		boolean commit = false;
		final WeatherMeasureDAO reports = new WeatherMeasureDAO(EntityManagerHelper.getEntityManager());
		try {
			EntityManagerHelper.beginTransaction();
			final List<WeatherReport> localConsumed = new ArrayList<>();
			while (!this.stop && !this.reports.isEmpty() && !commit) {
				WeatherReport consumed = this.consume();
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Consumed weather measure for "+consumed.getStation().getId()+" on "+consumed.getDate() + " from "+consumed.getStartTime().toString() + " to "+consumed.getEndTime().toString());
				}
				reports.create(consumed);
				localConsumed.add(consumed);
				commit = (localConsumed.size() == this.processLimit);
				Thread.sleep(50);
			}
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Consume loop exited for Consumer status [stop]="+String.valueOf(this.stop)+" [reportsEmpty]="+this.reports.isEmpty()+" [commit]="+commit);
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
			if(this.getLogger().isInfoEnabled()){
				this.getLogger().info("WeatherReportConsumer finished.  Total consumed "+localConsumed.size());
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
