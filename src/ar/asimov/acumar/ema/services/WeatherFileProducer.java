package ar.asimov.acumar.ema.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.helper.WeatherDataMapper;
import ar.asimov.acumar.ema.model.helper.WeatherSummaryMapper;
import ar.asimov.acumar.ema.wlk.data.DailySummaryData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.reader.WLinkFileReader;

public class WeatherFileProducer implements Callable<Integer> {

	private static final Log LOGGER = LogFactory.getLog(WeatherFileProducer.class);
	private static final int SLEEP_LIMIT = 500;
	private Station station;
	private final BlockingQueue<WeatherFile> files;
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;

	private WeatherFile lastFileProcessed;

	public WeatherFileProducer(final Station station, BlockingQueue<WeatherFile> files, WeatherFile lastFile,
			CountDownLatch startSignal, CountDownLatch doneSignal) {
		this.station = station;
		this.files = files;
		this.lastFileProcessed = lastFile;
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
	}

	protected Log getLogger() {
		return WeatherFileProducer.LOGGER;
	}

	private Station getStation() {
		return this.station;
	}

	private void produce(WeatherFile report) throws InterruptedException {
		while (this.files.remainingCapacity() == 0) {
			synchronized (this.files) {
				this.files.wait();
			}
		}
		synchronized (this.files) {
			while (!this.files.offer(report)) {
				this.files.wait();
			}
			this.files.notifyAll();
		}

	}

	@Override
	public Integer call() throws Exception {
		int totalProcessedData = 0;
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("Starting producer for Station " + this.getStation().getId());
		}
		Collection<Path> wlkFiles = this.fetchStationFiles();
		if (wlkFiles.size() > 0) {
			try {
				for (Path wlkFile : wlkFiles) {
					int sleepCounter = 0;
					totalProcessedData += this.doProduceFile(wlkFile, sleepCounter);
					this.startSignal.countDown();
					if (this.getLogger().isInfoEnabled()) {
						this.getLogger().debug("Ending process for Station " + this.getStation().getId() + ". Readed "
								+ totalProcessedData + " records.");
					}
				}
				return totalProcessedData;
			} catch (Exception e) {
				this.getLogger().error(e);
				this.getLogger().error(e);
				throw e;
			} finally {
				doneSignal.countDown();
			}
		} else {
			startSignal.countDown();
			doneSignal.countDown();
			return 0;
		}

	}

	protected boolean goToSleep(int sleepCounter) {
		return sleepCounter >= SLEEP_LIMIT && sleepCounter % SLEEP_LIMIT == 0
				&& Thread.activeCount() >= Runtime.getRuntime().availableProcessors();
	}

	protected Collection<Path> fetchStationFiles() throws IOException {
		Path path = Paths.get(this.getStation().getDirectoryPath());
		DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
		List<Path> wlkFiles = new ArrayList<>();
		for (Path wlkFile : stream) {
			wlkFiles.add(wlkFile);
		}
		Collections.sort(wlkFiles, new Comparator<Path>() {
			@Override
			public int compare(Path o1, Path o2) {
				YearMonth p1 = YearMonth.parse(FilenameUtils.removeExtension(o1.toFile().getName()),
						DateTimeFormatter.ofPattern("y-M"));
				YearMonth p2 = YearMonth.parse(FilenameUtils.removeExtension(o2.toFile().getName()),
						DateTimeFormatter.ofPattern("y-M"));
				return p1.compareTo(p2);
			}

		});
		return wlkFiles;
	}

	protected int doProduceFile(Path wlkFile, int sleepCounter) throws IOException, InterruptedException {
		int processedData = 0;
		WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
		int dataCounter = 0;
		int summaryCounter = 0;
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("Processing file " + wlkFile.toString());
		}
		if ((null != this.lastFileProcessed
				&& ((this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod()) && this.lastFileProcessed
						.getLastDayRecords() < reader.getRecordsInDay(this.lastFileProcessed.getLastDayIndex()))
						|| this.lastFileProcessed.getPeriod().isBefore(reader.getFilePeriod())))
				|| null == this.lastFileProcessed) {
			WeatherFile currentFile;
			currentFile = new WeatherFile();
			currentFile.setStation(this.getStation());
			currentFile.setPeriod(reader.getFilePeriod());
			currentFile.setTotalRecords(reader.getTotalRecords());
			int startDayIndex = (null != this.lastFileProcessed
					&& this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))
							? this.lastFileProcessed.getLastDayIndex() : 1;
			for (int i = startDayIndex; i <= currentFile.getPeriod().atEndOfMonth().getDayOfMonth(); i++) {
				int lastDayRecords = (null != this.lastFileProcessed
						&& this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))
								? this.lastFileProcessed.getLastDayRecords() : 0;
				if (reader.getRecordsInDay(i) > lastDayRecords) {
					DailySummaryData dailySummary = reader.readDay(i);
					WeatherSummary summary = WeatherSummaryMapper.map(dailySummary, this.getStation());
					currentFile.addWetaherSummary(summary);
					sleepCounter++;
					summaryCounter++;
					/**
					if (this.goToSleep(sleepCounter)) {
						Thread.sleep(100);
					}
					// */
					if (this.getLogger().isDebugEnabled()) {
						this.getLogger().debug("Processed summary record " + i + " [" + summary.getStation().getId()
								+ ", " + summary.getDate() + "]");
					}
					currentFile.setLastDayIndex(i);
					currentFile.setLastDayRecords(reader.getRecordsInDay(i));
				}
				for (int j = lastDayRecords; j < reader.getRecordsInDay(i); j++) {
					DailyWeatherData record = reader.read(i, j);
					if (null == record) {
						this.getLogger()
								.debug("Record " + j + " of day " + i + " in file " + currentFile.getPeriod().toString()
										+ " is null." + " Total records for day " + reader.getRecordsInDay(i));
					} else {
						WeatherData data = WeatherDataMapper.map(record, this.getStation());
						if (!currentFile.getWeatherData().contains(data)) {
							currentFile.addWeatherData(data);
						}
						dataCounter++;
						sleepCounter++;
						/**
						if (this.goToSleep(sleepCounter)) {
							Thread.sleep(100);
						}
						//*/
						if (this.getLogger().isDebugEnabled()) {
							this.getLogger()
									.debug("Processed data record " + j + " of day " + i + "["
											+ data.getStation().getId() + ", " + data.getDate() + ", "
											+ data.getStartTime().toString() + "]");
						}
						processedData++;
					}
				}
			}
			if (null != this.lastFileProcessed && this.lastFileProcessed.getPeriod().equals(currentFile.getPeriod())) {
				if (null == currentFile.getLastDayIndex() && null == currentFile.getLastDayRecords()) {
					currentFile.setLastDayIndex(this.lastFileProcessed.getLastDayIndex());
					currentFile.setLastDayRecords(this.lastFileProcessed.getLastDayIndex());
				}
			}
			this.produce(currentFile);
			if (this.getLogger().isInfoEnabled()) {
				this.getLogger()
						.info("Produced file " + currentFile.getPeriod().toString() + ".wlk" + " for Station "
								+ this.getStation().getId() + ". With a total of " + summaryCounter
								+ " summary records and " + dataCounter + " data records");
			}
		}
		reader.close();
		return processedData;
	}
}
