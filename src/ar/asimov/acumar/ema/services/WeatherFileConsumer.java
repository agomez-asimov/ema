package ar.asimov.acumar.ema.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.exception.DAOException;
import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.DAOManager;

public class WeatherFileConsumer implements Callable<Integer> {

	private static final Log LOGGER = LogFactory.getLog(WeatherFileConsumer.class);
	private static final int COMMIT_LIMIT = 1000;
	private static final int SLEEP_LIMIT = 500;
	private final BlockingQueue<WeatherFile> files;
	private boolean stop;
	
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;

	public WeatherFileConsumer(BlockingQueue<WeatherFile> files,CountDownLatch startSignal,CountDownLatch doneSignal) {
		this.stop = false;
		this.files = files;
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
	}

	protected WeatherFile consume() throws InterruptedException {
		while (this.files.isEmpty()) {
			synchronized (this.files) {
				this.files.wait();
			}
		}
		synchronized (this.files) {
			this.files.notifyAll();
			return this.files.poll();
		}
	}

	private Log getLogger() {
		return WeatherFileConsumer.LOGGER;
	}

	@Override
	public Integer call() throws Exception {
		int totalCounter = 0;
		try {
			this.startSignal.await();
			this.stop = (this.files.isEmpty());
			if (this.getLogger().isInfoEnabled()) {
				this.getLogger().debug("New WeatherFileConsumer started");
			}
			while (!this.stop) {
				int dataCounter = 0;
				int summariesCounter = 0;
				WeatherFile file = this.consume();
				DAOManager.beginTransaction();
				int commitCounter = 0;
				if (null != file) {
					if (this.getLogger().isDebugEnabled()) {
						this.getLogger().debug("Processing file " + file.getPeriod().format(DateTimeFormatter.ofPattern("y-MM")) + ".wlk for Station "
								+ file.getStation().getId());
					}
					dataCounter+=doProcessWeatherData(file,commitCounter);
					totalCounter+=dataCounter;
					summariesCounter+=doProcessWeatherSummaries(file,commitCounter);
					doProcessFile(file);
					if (DAOManager.isTransactionActive()) {
						DAOManager.commitTransaction();
						if (this.getLogger().isInfoEnabled()) {
							this.getLogger().info("Persisted file "+file.getPeriod().format(DateTimeFormatter.ofPattern("y-MM"))+".wlk for station "+file.getStation().getId()+" with a total of "+summariesCounter+" summaries and "+dataCounter+" records");
						}
					}
				}
				this.stop = (this.files.isEmpty());
			}
			return totalCounter;
		} catch (Exception e) {
			if (DAOManager.isTransactionActive()) {
				DAOManager.rollBackTransaction();
			}
			this.getLogger().error(e);
			throw e;
		} finally {
			DAOManager.close();
			if (this.getLogger().isDebugEnabled()) {
				this.getLogger().debug("File consumer finished");
			}
			this.doneSignal.countDown();
		}
	}

	protected boolean goToSleep(int counter){
		return counter >= SLEEP_LIMIT  && counter % SLEEP_LIMIT == 0 && Thread.activeCount() >= Runtime.getRuntime().availableProcessors();
	}

	protected boolean requiresCommit(int counter) {
		return counter >= COMMIT_LIMIT && counter % COMMIT_LIMIT == 0;
	}
	
	
	protected int doProcessWeatherData(WeatherFile file,int commitCounter) throws DAOException, InterruptedException{
		int dataCounter = 0;
		for (WeatherData data : file.getWeatherData()) {
			if (this.getLogger().isDebugEnabled()) {
				this.getLogger().debug("Creating data [" + data.getStation().getId() + ", "
						+ data.getDate().toString() + ", " + data.getStartTime().toString() + "]");
			}
			DAOManager.getDataDAO().create(data);
			dataCounter++;
			commitCounter++;
			/*;
			if (this.goToSleep(commitCounter)) {
				Thread.sleep(100);
			}
			//*/
			if (this.requiresCommit(commitCounter)) {
				DAOManager.commitTransaction();
				DAOManager.beginTransaction();
			}

		}
		return dataCounter;
	}
	
	protected int doProcessWeatherSummaries(WeatherFile file,int commitCounter) throws DAOException, InterruptedException{
		int processedSummaries = 0;
		for (WeatherSummary summary : file.getWeatherSummaries()) {
			if (DAOManager.getSummaryDAO().fetch(summary.getStation().getId(), summary.getDate()) != null) {
				if (this.getLogger().isDebugEnabled()) {
					this.getLogger().debug("Updating summary [" + summary.getStation().getId() + ", "
							+ summary.getDate().toString() + "]");
				}
				DAOManager.getSummaryDAO().update(summary);
			} else {
				if (this.getLogger().isDebugEnabled()) {
					this.getLogger().debug("Creating summary [" + summary.getStation().getId() + ", "
							+ summary.getDate().toString() + "]");
				}
				DAOManager.getSummaryDAO().create(summary);
			}
			processedSummaries++;
			commitCounter++;
			/*
			if (this.goToSleep(commitCounter)) {
				Thread.sleep(100);
			}
			//*/
			if (this.requiresCommit(commitCounter)) {
				DAOManager.commitTransaction();
				DAOManager.beginTransaction();
			}
		}
		return processedSummaries;
	}
	
	private void doProcessFile(WeatherFile file) throws DAOException{
		if (null != DAOManager.getFileDAO().fetch(file.getStation().getId(), file.getPeriod())) {
			if (this.getLogger().isDebugEnabled()) {
				this.getLogger().debug("Updated file [" + file.getStation().getId() + ", "
						+ file.getPeriod().toString() + ".wlk]");
			}
			file.setDateUpdated(LocalDateTime.now());
			DAOManager.getFileDAO().update(file);
		} else {
			if (this.getLogger().isDebugEnabled()) {
				this.getLogger().debug("Created file [" + file.getStation().getId() + ", "
						+ file.getPeriod().toString() + ".wlk]");
			}
			file.setDateCreated(LocalDateTime.now());
			file.setDateUpdated(LocalDateTime.now());
			DAOManager.getFileDAO().create(file);
		}
	}

}
