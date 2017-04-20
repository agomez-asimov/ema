package ar.asimov.acumar.ema.services;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherDailyReport;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.model.dao.DAOManager;
import ar.asimov.acumar.ema.model.helper.WeatherDailyReportMapper;
import ar.asimov.acumar.ema.model.helper.WeatherReportMapper;
import ar.asimov.acumar.ema.wlk.data.DailySummaryData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.reader.WLinkFileReader;

public class WeatherReportProducer implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(WeatherReportProducer.class);
	private WeakReference<Station> station;
	private BlockingQueue<WeatherReport> reports;
	private BlockingQueue<WeatherDailyReport> dailyReports;

	
	private Short exitCode;
	private WeatherDailyReport lastDailyReport; 
	private boolean running;
	private boolean stop;

	public WeatherReportProducer(final Station station, BlockingQueue<WeatherReport> reports,BlockingQueue<WeatherDailyReport> dailyReports,WeatherDailyReport lastDailyReport) {
		this.reports = reports;
		this.station = new WeakReference<Station>(station);
		this.running = false;
		this.lastDailyReport = lastDailyReport;
		this.dailyReports = dailyReports;
	}

	protected Log getLogger() {
		return WeatherReportProducer.LOGGER;
	}

	public short getExitCode() {
		return (this.running) ? null : this.exitCode;
	}


	private ProcessLog updateProcess(Path path, WeatherDailyReport lastDailyReport, ProcessLog processLog) throws IOException,InterruptedException {
		boolean fileNotFound = false;
		WeatherDailyReport localDailyReport = lastDailyReport;
		while (YearMonth.from(localDailyReport.getDate()).isBefore(YearMonth.now()) && !fileNotFound && !this.stop) {
			YearMonth currentPeriod = YearMonth.from(localDailyReport.getDate());
			String fileName = currentPeriod.format(DateTimeFormatter.ofPattern("y-M")) + ".wlk";
			Path filePath = path.resolve(fileName);
			if(!(fileNotFound = filePath.toFile().exists())){
				WLinkFileReader reader = new WLinkFileReader(filePath);
					while (null != localDailyReport && localDailyReport.getDate().isBefore(currentPeriod.atEndOfMonth())) {
						int localProcessedRecords = localDailyReport.getDate().isEqual(lastDailyReport.getDate())?localDailyReport.getRecordsInDay():0; 
						int localRecordsInDay = reader.getRecordsInDay(processLog.getLastProcessedDate().getDayOfMonth());
						if(localDailyReport.getDate().isEqual(lastDailyReport.getDate()) && localDailyReport.getRecordsInDay() > lastDailyReport.getRecordsInDay()){
							localDailyReport.setRecordsInDay(localRecordsInDay);
						}
						this.produceWeatherDailyReport(localDailyReport);
						while(localProcessedRecords < localRecordsInDay){
							DailyWeatherData data = reader.read(localDailyReport.getDate().getDayOfMonth(), localProcessedRecords);
							WeatherReport report = WeatherReportMapper.map(data, this.getStation());
							this.produceWeatherReport(report);
							localProcessedRecords++;
						}
						LocalDate nextDate = localDailyReport.getDate().plus(1, ChronoUnit.DAYS);
						if(reader.getRecordsInDay(nextDate.getDayOfMonth())>0){
							DailySummaryData summaryData = reader.readDay(localDailyReport.getDate().plus(1,ChronoUnit.DAYS).getDayOfMonth());
							localDailyReport = WeatherDailyReportMapper.map(summaryData, this.getStation(), reader.getRecordsInDay(nextDate.getDayOfMonth()));
						}else{
							localDailyReport = null;
						}
					}
			}else if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("The specified file "+path.getFileName() +" could not be found at "+path.getParent().toAbsolutePath());
			}
		}
		return processLog;
	}

	private ProcessLog firstProces(Path path,ProcessLog processLog) throws IOException,InterruptedException{
		DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
		int localTotalProcessedRecords = 0;
		for (Path wlkFile : stream) {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Processing file "+wlkFile.toAbsolutePath());
			}
			WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
			for (int i = 1; i < reader.getFilePeriod().atEndOfMonth().getDayOfMonth(); i++) {
				int maxRecords = reader.getRecordsInDay(i);
				processLog.setLastProcessedDateRecords(0);
				int localLastProcessedRecords = 0;
				int recordsInDay = reader.getRecordsInDay(i);
				if(recordsInDay > 0){
					DailySummaryData dailySummary = reader.readDay(i);
					WeatherDailyReport dailyReport = WeatherDailyReportMapper.map(dailySummary, this.getStation(), recordsInDay);
					this.produceWeatherDailyReport(dailyReport);
					while(localLastProcessedRecords < maxRecords) {
						DailyWeatherData record = reader.read(i, localLastProcessedRecords);
						WeatherReport report = WeatherReportMapper.map(record,this.getStation());
						this.produceWeatherReport(report);
						localTotalProcessedRecords++;
						localLastProcessedRecords++;
						processLog.setTotalProcessedRecords(localTotalProcessedRecords);
						processLog.setLastProcessedDateRecords(localLastProcessedRecords);
					}
					processLog.setLastProcessedDate(reader.getFilePeriod().atDay(i));
				}
			}
			reader.close();
		}
		return processLog;
	}

	public Station getStation() {
		return this.station.get();
	}

	private void produceWeatherReport(WeatherReport report) throws InterruptedException {
		if(this.reports.remainingCapacity()==0){
			synchronized(this.reports){
				this.reports.wait();
			}
		}
		synchronized (this.reports) {
			this.reports.add(report);
			this.reports.notifyAll();
		}
	}
	
	private void produceWeatherDailyReport(WeatherDailyReport report) throws InterruptedException{
		if(this.dailyReports.remainingCapacity()==0){
			synchronized(this.dailyReports){
				this.dailyReports.wait();
			}
		}
		synchronized(this.dailyReports){
			this.dailyReports.add(report);
			this.dailyReports.notifyAll();
		}
	}

    public boolean isRunning() {
		return running;
	}

	public void stop() {
		this.stop = true;
	}


	@Override
	public void run() {
		ProcessLog processLog = new ProcessLog();
		processLog.setStart(Instant.now());
		processLog.setStation(this.station.get());
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("Weather producer started for station "+this.getStation().getName()+ "at " + Instant.now().toString());
		}
		this.running = true;
		try {
			Path path = Paths.get(this.getStation().getDbPath());
			if (null == this.lastDailyReport) {
				processLog = this.firstProces(path,processLog);
			} else {
				processLog = this.updateProcess(path, this.lastDailyReport,processLog);
			}
			processLog.setEnd(Instant.now());
		} catch (IOException | InterruptedException e) {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("An Exception has been thrown in producer for station "+this.getStation().getName(),e);
			}
			processLog.setEnd(Instant.now());
			processLog.setAbnormalCompletion(true);
			processLog.setAbnormalCompletionCause(e.getMessage()+"For more information see log file");
			this.exitCode = 1;
		} finally {
			DAOManager.startTransaction();
			DAOManager.getProcessLogDAO().create(processLog);
			DAOManager.commitTransaction();
			this.running = false;
		}	
	}

}
