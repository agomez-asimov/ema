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

public class WeatherDataProducer implements Callable<ProcessInformation> {
	private static final Log LOGGER = LogFactory.getLog(WeatherDataProducer.class);
	private WeakReference<Station> station;
	private Queue<WeatherReport> measures;
	
	private Short exitCode;
	private LocalDate startDate;
	private Integer startRecord;
	private boolean running;
	private boolean stop;

	public WeatherDataProducer(final Station station, Queue<WeatherReport> measures,LocalDate startDate,Integer  startRecord) {
		this.measures = measures;
		this.station = new WeakReference<Station>(station);
		this.running = false;
		this.startDate = startDate; 
		this.startRecord = startRecord;
	}

	protected Log getLogger() {
		return WeatherDataProducer.LOGGER;
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
					WeatherReport measure = this.fromFileRecord(record);
					this.produce(measure);
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

	private void produce(WeatherReport measure) {
		synchronized (this.measures) {
			this.measures.add(measure);
			this.measures.notifyAll();
		}
	}

	private WeatherReport fromFileRecord(DailyWeatherData record) {
		WeatherReport measure = new WeatherReport();
		measure.setDate(record.getDate());
		measure.setStation(this.getStation());
		measure.setStartTime(record.getStartTime());
		measure.setEndTime(record.getEndTime());
		measure.setOutsideTemperature(record.getOutsideTemperature());
		measure.setMaxOutsideTemperature(record.getMaxOutsideTemperature());
		measure.setMinOutsideTemperature(record.getMinOutsideTemperature());
		measure.setInsideTemperature(record.getInsideTemperature());
		measure.setPressure(record.getPressure());
		measure.setOutsideHumidity(record.getOutsideHumidity());
		measure.setInsideHumidity(record.getInsideHumidity());
		measure.setPrecipitation(record.getPrecipitation());
		measure.setMaxPrecipitationRate(record.getMaxPrecipitationRate());
		measure.setWindSpeed(record.getWindSpeed());
		measure.setMaxWindSpeed(record.getMaxWindSpeed());
		measure.setWindSamplesNumber(record.getWindSamplesNumber());
		measure.setSolarRadiation(record.getSolarRadiation());
		measure.setMaxSolarRadiation(record.getMaxSolarRadiation());
		measure.setUVIndex(record.getUVIndex());
		measure.setMaxUVIndex(record.getMaxUVIndex());
		measure.setExtraRadiation(record.getExtraRadiation());
		measure.setForecast(record.getForecast());
		measure.setET(record.getET());
		measure.setIconFlags(record.getIconFlags());
		measure.setRainCollectorType(record.getRainCollectorType());
		measure.setWindDirection(record.getWindDirection());
		measure.setMaxWindDirection(record.getMaxWindDirection());
		measure.setMoreFlags(record.getMoreFlags());
		List<LeafTemperature> localLeafTemperature = new ArrayList<>();
		for (int i = 0; i < record.getLeafTemperature().size(); i++) {
			LeafTemperature lt = new LeafTemperature();
			lt.setStation(this.getStation());
			lt.setDate(measure.getDate());
			lt.setStartTime(measure.getStartTime());
			lt.setOrder(i);
			lt.setValue((int) record.getLeafTemperature(i));
			localLeafTemperature.add(lt);
		}
		measure.setLeafTemperature(localLeafTemperature);
		List<NewSensor> localNewSensors = new ArrayList<>();
		for (int i = 0; i < record.getNewSensors().size(); i++) {
			NewSensor ns = new NewSensor();
			ns.setStation(this.getStation());
			ns.setDate(measure.getDate());
			ns.setStartTime(measure.getStartTime());
			ns.setOrder(i);
			ns.setValue((int) record.getNewSensor(i));
			localNewSensors.add(ns);
		}
		measure.setNewSensors(localNewSensors);
		List<SoilTemperature> localSoilTemperature = new ArrayList<>();
		for (int i = 0; i < record.getSoilTemperature().size(); i++) {
			SoilTemperature st = new SoilTemperature();
			st.setStation(this.getStation());
			st.setDate(measure.getDate());
			st.setStartTime(measure.getStartTime());
			st.setOrder(i);
			st.setValue((int) record.getSoilTemperature(i));
			localSoilTemperature.add(st);
		}
		measure.setSoilTemperature(localSoilTemperature);
		List<SoilMoisture> localSoilMoisture = new ArrayList<>();
		for (int i = 0; i < record.getSoilMoisture().size(); i++) {
			SoilMoisture sm = new SoilMoisture();
			sm.setStation(this.getStation());
			sm.setDate(measure.getDate());
			sm.setStartTime(measure.getStartTime());
			sm.setOrder(i);
			sm.setValue((int) record.getSoilMoisture(i));
			localSoilMoisture.add(sm);
		}
		measure.setSoilMoisture(localSoilMoisture);
		List<LeafWetness> localLeafWetness = new ArrayList<>();
		for (int i = 0; i < record.getLeafWetness().size(); i++) {
			LeafWetness lw = new LeafWetness();
			lw.setStation(this.getStation());
			lw.setDate(measure.getDate());
			lw.setStartTime(measure.getStartTime());
			lw.setOrder(i);
			lw.setValue((int) record.getLeafWetness(i));
			localLeafWetness.add(lw);
		}
		measure.setLeafWetness(localLeafWetness);
		List<ExtraTemperature> localExtraTemperature = new ArrayList<>();
		for (int i = 0; i < record.getExtraTemperature().size(); i++) {
			ExtraTemperature et = new ExtraTemperature();
			et.setStation(this.getStation());
			et.setDate(measure.getDate());
			et.setStartTime(measure.getStartTime());
			et.setOrder(i);
			et.setValue((int) record.getExtraTemperature(i));
			localExtraTemperature.add(et);
		}
		measure.setExtraTemperature(localExtraTemperature);
		List<ExtraHumidity> localExtraHumidity = new ArrayList<>();
		for (int i = 0; i < record.getExtraHumidity().size(); i++) {
			ExtraHumidity eh = new ExtraHumidity();
			eh.setStation(this.getStation());
			eh.setDate(measure.getDate());
			eh.setStartTime(measure.getStartTime());
			eh.setOrder(i);
			eh.setValue((int) record.getExtraHumidity(i));
			localExtraHumidity.add(eh);
		}
		measure.setExtraHumidity(localExtraHumidity);
		return measure;

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
