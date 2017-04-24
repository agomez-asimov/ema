package ar.asimov.acumar.ema.services;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

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

public class WeatherFileProducer implements Runnable {

	private static final Log LOGGER = LogFactory.getLog(WeatherFileProducer.class);
	private WeakReference<Station> station;
	private BlockingQueue<WeatherFile> files;

	private Short exitCode;
	private WeatherFile lastFileProcessed;
	private boolean running;
	private boolean stop;

	public WeatherFileProducer(final Station station, BlockingQueue<WeatherFile> files, WeatherFile lastFile) {
		this.station = new WeakReference<Station>(station);
		this.running = false;
	}

	protected Log getLogger() {
		return WeatherFileProducer.LOGGER;
	}

	public short getExitCode() {
		return (this.running) ? null : this.exitCode;
	}

	protected Station getStation() {
		return this.station.get();
	}

	@Override
	public void run() {
		this.running = true;
		try{
			Path path = Paths.get(this.getStation().getDirectoryPath());
			DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
			int localTotalProcessedRecords = 0;
			for (Path wlkFile : stream) {
				WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
				if (null == this.lastFileProcessed) {
					this.lastFileProcessed = new WeatherFile();
					this.lastFileProcessed.setPeriod(reader.getFilePeriod());
					this.lastFileProcessed.setLastDayIndex(1);
					this.lastFileProcessed.setLastDayRecords(0);
				}
				if (!this.lastFileProcessed.getPeriod().isAfter(reader.getFilePeriod())) {
					WeatherFile currentFile;
					if(this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod())){
						currentFile = this.lastFileProcessed;
					}else{
						currentFile = new WeatherFile();
						currentFile.setStation(this.getStation());
						currentFile.setPeriod(reader.getFilePeriod());
						currentFile.setTotalRecords(reader.getTotalRecords());
					}
					int startDayIndex = this.lastFileProcessed.getLastDayIndex();
					for (int i = startDayIndex; i <= reader.getFilePeriod().atEndOfMonth().getDayOfMonth(); i++) {
						int lastDayRecords = (this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))
								? this.lastFileProcessed.getLastDayRecords() : 0;
						if (reader.getRecordsInDay(i) > lastDayRecords) {
							DailySummaryData dailySummary = reader.readDay(i);
							WeatherSummary summary = WeatherSummaryMapper.map(dailySummary,this.getStation());
							currentFile.addWetaherSummary(summary);
							currentFile.setLastDayIndex(i);
							currentFile.setLastDayRecords(reader.getRecordsInDay(i));
						}
						for (int j = lastDayRecords; j < reader.getRecordsInDay(i); j++) {
							DailyWeatherData record = reader.read(i, j);
							WeatherData data = WeatherDataMapper.map(record,this.getStation());
							currentFile.addWeatherData(data);
							localTotalProcessedRecords++;
						}
					}
					try{
						this.produce(currentFile);
					}catch(InterruptedException e){
						//HANDLE INTERRUPTED EXCEPTION
					}
				}
				reader.close();
			}
			if(localTotalProcessedRecords == 0){
				//SEND MAIL NO RECORDS HAS BEEN PROCESSED
			}
			this.running = false;
		}catch(IOException e){
			
		}
	}

	private void produce(WeatherFile report) throws InterruptedException{
		if(this.files.remainingCapacity()==0){
			synchronized(this.files){
				this.files.wait();
			}
		}
		synchronized(this.files){
			this.files.add(report);
			this.files.notifyAll();
		}
	}
	
}
