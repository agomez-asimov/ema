package ar.asimov.acumar.ema.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.ProcessInformation;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.dao.DAOManager;
import ar.asimov.acumar.ema.model.helper.WeatherDataMapper;
import ar.asimov.acumar.ema.model.helper.WeatherSummaryMapper;
import ar.asimov.acumar.ema.wlk.data.DailySummaryData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.reader.WLinkFileReader;

public class WeatherFileProducer implements Runnable {

	private static final Log LOGGER = LogFactory.getLog(WeatherFileProducer.class);
	private static final int SLEEP_LIMIT = 500;
	private Station station;
	private final BlockingQueue<WeatherFile> files;
	
	private WeatherFile lastFileProcessed;

	public WeatherFileProducer(final Station station, BlockingQueue<WeatherFile> files, WeatherFile lastFile) {
		this.station = station;
		this.files = files;
		this.lastFileProcessed = lastFile;
	}

	protected Log getLogger() {
		return WeatherFileProducer.LOGGER	;
	}

	private Station getStation() {
		return this.station;
	}

	@Override
	public void run() {
		ProcessInformation processInformation = new ProcessInformation();
		processInformation.setStation(this.getStation());
		int localTotalProcessedRecords = 0;
		try{
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Starting producer for Station "+this.getStation().getId());
			}
			processInformation.setStart(LocalDateTime.now());
			processInformation.setAbnormalTermination(false);
			Path path = Paths.get(this.getStation().getDirectoryPath());
			DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
			List<Path> wlkFiles = new ArrayList<>();
			for(Path wlkFile : stream){
				wlkFiles.add(wlkFile);
			}
			Collections.sort(wlkFiles, new Comparator<Path>() {

				@Override
				public int compare(Path o1, Path o2) {
					YearMonth p1 = YearMonth.parse(FilenameUtils.removeExtension(o1.toFile().getName()),DateTimeFormatter.ofPattern("y-M"));
					YearMonth p2 = YearMonth.parse(FilenameUtils.removeExtension(o2.toFile().getName()),DateTimeFormatter.ofPattern("y-M"));
					return p1.compareTo(p2);
				}
				
			});
			int processedRecords = 0;
			for (Path wlkFile : wlkFiles) {
				WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Processing file "+wlkFile.toString());
				}
				if (null == this.lastFileProcessed || !this.lastFileProcessed.getPeriod().isAfter(reader.getFilePeriod())) {
					WeatherFile currentFile;
						currentFile = new WeatherFile();
						currentFile.setStation(this.getStation());
						currentFile.setPeriod(reader.getFilePeriod());
						currentFile.setTotalRecords(reader.getTotalRecords());
					int startDayIndex = (null != this.lastFileProcessed && this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))?this.lastFileProcessed.getLastDayIndex():1;
					for (int i = startDayIndex; i <= currentFile.getPeriod().atEndOfMonth().getDayOfMonth(); i++) {
						int lastDayRecords = (null != this.lastFileProcessed && this.lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))?this.lastFileProcessed.getLastDayIndex():0;
						if (reader.getRecordsInDay(i) > lastDayRecords) {
							DailySummaryData dailySummary = reader.readDay(i);
							WeatherSummary summary = WeatherSummaryMapper.map(dailySummary,this.getStation());
							currentFile.addWetaherSummary(summary);
							processedRecords++;
							if(processedRecords == SLEEP_LIMIT && Thread.activeCount() >= 2){
								Thread.sleep(500);
							}
		
							if(this.getLogger().isDebugEnabled()){
								this.getLogger().debug("Added summary ["+summary.getStation().getId()+", "+summary.getDate()+"]");
							}
							currentFile.setLastDayIndex(i);
							currentFile.setLastDayRecords(reader.getRecordsInDay(i));
						}
						for (int j = lastDayRecords; j < reader.getRecordsInDay(i); j++) {
							DailyWeatherData record = reader.read(i, j);
							WeatherData data = WeatherDataMapper.map(record,this.getStation());
							currentFile.addWeatherData(data);
							if(this.getLogger().isDebugEnabled()){
								this.getLogger().debug("Added data ["+data.getStation().getId()+", "+data.getDate()+", "+data.getStartTime().toString()+"]");
							}
		
							processedRecords++;
							if(processedRecords == SLEEP_LIMIT && Thread.activeCount() >= 2){
								Thread.sleep(500);
							}
		
							localTotalProcessedRecords++;
						}
					}
					try{
						this.produce(currentFile);
						if(this.getLogger().isDebugEnabled()){
							this.getLogger().debug("Produced file "+currentFile.getPeriod().toString()+".wlk"+" for Station "+this.getStation().getId());
						}
					}catch(InterruptedException e){
						this.getLogger().error("An interrupted exception happened",e);;
					}
				}
				reader.close();
			}
			if(localTotalProcessedRecords == 0){
				
				//SEND MAIL NO RECORDS HAS BEEN PROCESSED
			}
			if(this.getLogger().isDebugEnabled()){
				this.getLogger().debug("Ending process for Station "+this.getStation().getId()+". Readed "+localTotalProcessedRecords+" records.");
			}
		}catch(IOException e){
			this.getLogger().error("An exception has been thrown",e);
			processInformation.setAbnormalTermination(true);
			processInformation.setAbnormalTemrminationCause(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			processInformation.setEnd(LocalDateTime.now());
			processInformation.setProcessedRecords(localTotalProcessedRecords);
			DAOManager.beginTransaction();
			DAOManager.getProcessInformationDAO().create(processInformation);
			DAOManager.commitTransaction();
		}
	}

	private void produce(WeatherFile report) throws InterruptedException{
		while(this.files.remainingCapacity()==0){
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
