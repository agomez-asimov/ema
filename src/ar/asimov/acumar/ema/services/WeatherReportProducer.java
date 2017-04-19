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
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.ExtraHumidity;
import ar.asimov.acumar.ema.model.ExtraTemperature;
import ar.asimov.acumar.ema.model.LeafTemperature;
import ar.asimov.acumar.ema.model.LeafWetness;
import ar.asimov.acumar.ema.model.NewSensor;
import ar.asimov.acumar.ema.model.SoilMoisture;
import ar.asimov.acumar.ema.model.SoilTemperature;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.reader.WLinkFileReader;

public class WeatherReportProducer implements Callable<ProcessInformation> {
	private static final Log LOGGER = LogFactory.getLog(WeatherReportProducer.class);
	private WeakReference<Station> station;
	private BlockingQueue<WeatherReport> reports;
	
	private Short exitCode;
	private LocalDate startDate;
	private Integer startRecord;
	private boolean running;
	private boolean stop;

	public WeatherReportProducer(final Station station, BlockingQueue<WeatherReport> reports,LocalDate startDate,Integer  startRecord) {
		this.reports = reports;
		this.station = new WeakReference<Station>(station);
		this.running = false;
		this.startDate = startDate; 
		this.startRecord = startRecord;
	}

	protected Log getLogger() {
		return WeatherReportProducer.LOGGER;
	}

	public short getExitCode() {
		return (this.running) ? null : this.exitCode;
	}


	private ProcessInformation updateProcess(Path path, LocalDate paramLastProcessedDate,Integer paramLastProcessedRecords) throws IOException {
		final ProcessInformation information = new ProcessInformation();
		information.setStation(this.station.get());
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("Update process for file "+path.toAbsolutePath().toString()+" lastProcessedDate: "+paramLastProcessedDate.toString()+" lastProcessedRecords: "+paramLastProcessedRecords);
		}
		LocalDate localLastProcessedDate = paramLastProcessedDate;
		boolean fileNotFound = false;
		int localTotalProcessedRecords = 0;
		while (YearMonth.from(localLastProcessedDate).isBefore(YearMonth.now()) && !fileNotFound && !this.stop) {
			YearMonth currentPeriod = YearMonth.from(localLastProcessedDate);
			String fileName = currentPeriod.format(DateTimeFormatter.ofPattern("y-M")) + ".wlk";
			Path filePath = path.resolve(fileName);
			if(!(fileNotFound = filePath.toFile().exists())){
				WLinkFileReader reader = new WLinkFileReader(filePath);
				while (information.getLastProcessedDate().isBefore(currentPeriod.atEndOfMonth())) {
					int localLastProcessedRecords = (information.getLastProcessedDate().isEqual(localLastProcessedDate)) ? paramLastProcessedRecords : 0; 
					int maxRecords = reader.getRecordsInDay(information.getLastProcessedDate().getDayOfMonth());
					while(localLastProcessedRecords < maxRecords){
						this.produce(this.fromFileRecord(reader.read(localLastProcessedDate.getDayOfMonth(), localLastProcessedRecords)));
						localLastProcessedRecords++;
						localTotalProcessedRecords++;
						information.setTotalProcessed(localTotalProcessedRecords);
						information.setLastProcessedRecords(localLastProcessedRecords);
					}
					localLastProcessedDate = localLastProcessedDate.plus(1,ChronoUnit.DAYS);
					information.setLastProcessedDate(localLastProcessedDate);
				}
			}else if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("The specified file "+path.getFileName() +" could not be found at "+path.getParent().toAbsolutePath());
			}
		}
		return information;
	}

	private ProcessInformation firstProces(Path path) throws IOException{
		final ProcessInformation information = new ProcessInformation();
		information.setStation(this.station.get());
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("First run called at for "+path.toAbsolutePath());
		}
		information.setLastProcessedDate(null);
		DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
		int localTotalProcessedRecords = 0;
		for (Path wlkFile : stream) {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Processing file "+wlkFile.toAbsolutePath());
			}
			WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
			for (int i = 1; i < reader.getFilePeriod().atEndOfMonth().getDayOfMonth(); i++) {
				int maxRecords = reader.getRecordsInDay(i);
				information.setLastProcessedRecords(0);
				int localLastProcessedRecords = 0;
				while(localLastProcessedRecords < maxRecords) {
					DailyWeatherData record = reader.read(i, localLastProcessedRecords);
					WeatherReport report = this.fromFileRecord(record);
					this.produce(report);
					localTotalProcessedRecords++;
					localLastProcessedRecords++;
					information.setTotalProcessed(localTotalProcessedRecords);
					information.setLastProcessedRecords(localLastProcessedRecords);
				}
				information.setLastProcessedDate(reader.getFilePeriod().atDay(i));
			}
			reader.close();
		}
		return information;
	}

	public Station getStation() {
		return this.station.get();
	}

	private void produce(WeatherReport report) {
		if(this.reports.remainingCapacity()==0){
			synchronized(this.reports){
				try{
					this.reports.wait();
				}catch(InterruptedException e){
					throw new RuntimeException(e);
				}
			}
		}
		synchronized (this.reports) {
			this.reports.add(report);
			this.reports.notifyAll();
		}
	}

	private WeatherReport fromFileRecord(DailyWeatherData record) {
		WeatherReport report = new WeatherReport();
		report.setDate(record.getDate());
		report.setStation(this.getStation());
		report.setStartTime(record.getStartTime());
		report.setEndTime(record.getEndTime());
		report.setOutsideTemperature(record.getOutsideTemperature());
		report.setMaxOutsideTemperature(record.getMaxOutsideTemperature());
		report.setMinOutsideTemperature(record.getMinOutsideTemperature());
		report.setInsideTemperature(record.getInsideTemperature());
		report.setPressure(record.getPressure());
		report.setOutsideHumidity(record.getOutsideHumidity());
		report.setInsideHumidity(record.getInsideHumidity());
		report.setPrecipitation(record.getPrecipitation());
		report.setMaxPrecipitationRate(record.getMaxPrecipitationRate());
		report.setWindSpeed(record.getWindSpeed());
		report.setMaxWindSpeed(record.getMaxWindSpeed());
		report.setWindSamplesNumber(record.getWindSamplesNumber());
		report.setSolarRadiation(record.getSolarRadiation());
		report.setMaxSolarRadiation(record.getMaxSolarRadiation());
		report.setUVIndex(record.getUVIndex());
		report.setMaxUVIndex(record.getMaxUVIndex());
		report.setExtraRadiation(record.getExtraRadiation());
		report.setForecast(record.getForecast());
		report.setET(record.getET());
		report.setIconFlags(record.getIconFlags());
		report.setRainCollectorType(record.getRainCollectorType());
		report.setWindDirection(record.getWindDirection());
		report.setMaxWindDirection(record.getMaxWindDirection());
		report.setMoreFlags(record.getMoreFlags());
		List<LeafTemperature> localLeafTemperature = new ArrayList<>();
		for (int i = 0; i < record.getLeafTemperature().size(); i++) {
			LeafTemperature lt = new LeafTemperature();
			lt.setStation(this.getStation());
			lt.setDate(report.getDate());
			lt.setStartTime(report.getStartTime());
			lt.setOrder(i);
			lt.setValue((int) record.getLeafTemperature(i));
			localLeafTemperature.add(lt);
		}
		report.setLeafTemperature(localLeafTemperature);
		List<NewSensor> localNewSensors = new ArrayList<>();
		for (int i = 0; i < record.getNewSensors().size(); i++) {
			NewSensor ns = new NewSensor();
			ns.setStation(this.getStation());
			ns.setDate(report.getDate());
			ns.setStartTime(report.getStartTime());
			ns.setOrder(i);
			ns.setValue((int) record.getNewSensor(i));
			localNewSensors.add(ns);
		}
		report.setNewSensors(localNewSensors);
		List<SoilTemperature> localSoilTemperature = new ArrayList<>();
		for (int i = 0; i < record.getSoilTemperature().size(); i++) {
			SoilTemperature st = new SoilTemperature();
			st.setStation(this.getStation());
			st.setDate(report.getDate());
			st.setStartTime(report.getStartTime());
			st.setOrder(i);
			st.setValue((int) record.getSoilTemperature(i));
			localSoilTemperature.add(st);
		}
		report.setSoilTemperature(localSoilTemperature);
		List<SoilMoisture> localSoilMoisture = new ArrayList<>();
		for (int i = 0; i < record.getSoilMoisture().size(); i++) {
			SoilMoisture sm = new SoilMoisture();
			sm.setStation(this.getStation());
			sm.setDate(report.getDate());
			sm.setStartTime(report.getStartTime());
			sm.setOrder(i);
			sm.setValue((int) record.getSoilMoisture(i));
			localSoilMoisture.add(sm);
		}
		report.setSoilMoisture(localSoilMoisture);
		List<LeafWetness> localLeafWetness = new ArrayList<>();
		for (int i = 0; i < record.getLeafWetness().size(); i++) {
			LeafWetness lw = new LeafWetness();
			lw.setStation(this.getStation());
			lw.setDate(report.getDate());
			lw.setStartTime(report.getStartTime());
			lw.setOrder(i);
			lw.setValue((int) record.getLeafWetness(i));
			localLeafWetness.add(lw);
		}
		report.setLeafWetness(localLeafWetness);
		List<ExtraTemperature> localExtraTemperature = new ArrayList<>();
		for (int i = 0; i < record.getExtraTemperature().size(); i++) {
			ExtraTemperature et = new ExtraTemperature();
			et.setStation(this.getStation());
			et.setDate(report.getDate());
			et.setStartTime(report.getStartTime());
			et.setOrder(i);
			et.setValue((int) record.getExtraTemperature(i));
			localExtraTemperature.add(et);
		}
		report.setExtraTemperature(localExtraTemperature);
		List<ExtraHumidity> localExtraHumidity = new ArrayList<>();
		for (int i = 0; i < record.getExtraHumidity().size(); i++) {
			ExtraHumidity eh = new ExtraHumidity();
			eh.setStation(this.getStation());
			eh.setDate(report.getDate());
			eh.setStartTime(report.getStartTime());
			eh.setOrder(i);
			eh.setValue((int) record.getExtraHumidity(i));
			localExtraHumidity.add(eh);
		}
		report.setExtraHumidity(localExtraHumidity);
		return report;

	}
	
	

	public LocalDate getStartDate() {
		return startDate;
	}

	public Integer getStartRecord() {
		return startRecord;
	}

    public boolean isRunning() {
		return running;
	}

	public void stop() {
		this.stop = true;
	}

	@Override
	public ProcessInformation call() throws Exception {
		if(this.getLogger().isDebugEnabled()){
			this.getLogger().debug("Weather producer started for station "+this.getStation().getName()+ "at " + Instant.now().toString());
		}
		this.running = true;
		try {
			final ProcessInformation information;
			Path path = Paths.get(this.getStation().getDbPath());
			if (null == this.startDate) {
				information = this.firstProces(path);
			} else {
				information = this.updateProcess(path, this.startDate, this.startRecord);
			}
			return information;
		} catch (IOException e) {
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("IOException in producer for station "+this.getStation().getName(),e);
			}
			this.exitCode = 1;
			final ProcessInformation information = new ProcessInformation();
			information.setLastProcessedDate(null);
			information.setLastProcessedRecords(0);
			information.setTotalProcessed(0);
			return information;
		} finally {
			this.running = false;
		}
	}

}
