package ar.asimov.acumar.ema.services;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.reader.WLinkFileReader;

public class WeatherDataProducer implements Runnable {

	private WeakReference<Station> station;
	private Queue<WeatherMeasure> measures;
	private final Log logger;
	
	private Short exitCode;
	private Integer produced;
	private LocalDate lastProcessedDate;
	private Integer lastProcessedRecords;
	private LocalDate startDate;
	private Integer startRecord;
	private boolean running;
	private boolean stop;

	public WeatherDataProducer(final Station station, Queue<WeatherMeasure> measures,LocalDate startDate,Integer  startRecord) {
		this.measures = measures;
		this.station = new WeakReference<Station>(station);
		this.logger = LogFactory.getLog(this.getClass());
		this.running = false;
		this.startDate = startDate; 
		this.startRecord = startRecord;
		this.produced = 0;
	}

	protected Log getLogger() {
		return this.logger;
	}

	public short getExitCode() {
		return (this.running) ? null : this.exitCode;
	}

	public Integer getProduced() {
		return (this.running) ? null : this.produced;
	}

	@Override
	public void run() {
		this.running = true;
		try {
			Path path = Paths.get(this.getStation().getDbPath());
			if (null == this.startDate) {
				this.firstProces(path);
			} else {
				this.updateProcess(path, this.startDate, this.startRecord);
			}
		} catch (IOException e) {
			this.exitCode = 1;
			this.lastProcessedRecords = 0;
			this.lastProcessedDate = null;
			this.produced = 0;
		} finally {
			this.running = false;
		}
	}

	private void updateProcess(Path path, LocalDate localLastProcessedDate,Integer localLastProcessedRecords) throws IOException {
		this.lastProcessedDate = localLastProcessedDate;
		boolean fileNotFound = false;
		while (YearMonth.from(this.lastProcessedDate).isBefore(YearMonth.now()) && !fileNotFound && !this.stop) {
			YearMonth currentPeriod = YearMonth.from(this.lastProcessedDate);
			String fileName = currentPeriod.format(DateTimeFormatter.ofPattern("y-M")) + ".wlk";
			Path filePath = path.resolve(fileName);
			if(!(fileNotFound = filePath.toFile().exists())){
				WLinkFileReader reader = new WLinkFileReader(filePath);
				while (this.lastProcessedDate.isBefore(currentPeriod.atEndOfMonth())) {
					this.lastProcessedRecords = (this.lastProcessedDate.isEqual(lastProcessedDate)) ? localLastProcessedRecords : 0;
					int maxRecords = reader.getRecordsInDay(this.lastProcessedDate.getDayOfMonth());
					while(this.lastProcessedRecords < maxRecords){
						this.produce(this.fromFileRecord(reader.read(this.lastProcessedDate.getDayOfMonth(), this.lastProcessedRecords)));
						this.produced++;
						this.lastProcessedRecords++;
					}
					this.lastProcessedDate = this.lastProcessedDate.plus(1, ChronoUnit.DAYS);
				}
			}
		}
	}

	private void firstProces(Path path) throws IOException{
		this.lastProcessedDate = null;
		DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
		for (Path wlkFile : stream) {
			WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
			for (int i = 1; i < reader.getFilePeriod().atEndOfMonth().getDayOfMonth(); i++) {
				int maxRecords = reader.getRecordsInDay(i);
				this.lastProcessedRecords = 0;
				while(lastProcessedRecords < maxRecords) {
					DailyWeatherData record = reader.read(i, lastProcessedRecords);
					WeatherMeasure measure = this.fromFileRecord(record);
					this.produce(measure);
					this.produced++;
					this.lastProcessedRecords++;
				}
				this.lastProcessedDate = reader.getFilePeriod().atDay(i);
			}
			reader.close();
		}

	}

	public Station getStation() {
		return this.station.get();
	}

	private void produce(WeatherMeasure measure) {
		synchronized (this.measures) {
			this.measures.add(measure);
			this.measures.notifyAll();
		}
	}

	private WeatherMeasure fromFileRecord(DailyWeatherData record) {
		WeatherMeasure measure = new WeatherMeasure();
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

	public LocalDate getLastProcessedDate() {
		return lastProcessedDate;
	}

	public Integer getlastProcessedRecords() {
		return lastProcessedRecords;
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		this.stop = true;
	}

	

}
