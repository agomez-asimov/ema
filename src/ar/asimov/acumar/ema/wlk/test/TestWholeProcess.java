package ar.asimov.acumar.ema.wlk.test;

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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

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

public class TestWholeProcess {

	public static final Log LOGGER = LogFactory.getLog(TestWholeProcess.class);
	public static final int REPORTS_QUEUE_CAPACITY = 50;

	public Log getLogger() {
		return LOGGER;
	}

	@Test
	public void test() {
		final ArrayBlockingQueue<WeatherFile> files = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY);
		final List<Station> stations = DAOManager.getStationDAO().fetchAll();
		final ExecutorService executors = Executors.newFixedThreadPool(stations.size()*2);
		for(Station station : stations){
			ProcessInformation processInformation = new ProcessInformation();
			processInformation.setStation(station);
			WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
			int localTotalProcessedRecords = 0;
			try{
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Starting producer for Station "+station.getId());
				}
				processInformation.setStart(LocalDateTime.now());
				processInformation.setAbnormalTermination(false);
				Path path = Paths.get(station.getDirectoryPath());
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
				///*
				int processedRecords = 0;
				//*/
				for (Path wlkFile : wlkFiles) {
					WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
					if(this.getLogger().isDebugEnabled()){
						this.getLogger().debug("Processing file "+wlkFile.toString());
					}
					if (null == lastFile || !lastFile.getPeriod().isAfter(reader.getFilePeriod())) {
						WeatherFile currentFile;
							currentFile = new WeatherFile();
							currentFile.setStation(station);
							currentFile.setPeriod(reader.getFilePeriod());
							currentFile.setTotalRecords(reader.getTotalRecords());
						int startDayIndex = (null != lastFile && lastFile.getPeriod().equals(reader.getFilePeriod()))?lastFile.getLastDayIndex():1;
						for (int i = startDayIndex; i <= currentFile.getPeriod().atEndOfMonth().getDayOfMonth(); i++) {
							int lastDayRecords = (null != lastFile && lastFile.getPeriod().equals(reader.getFilePeriod()))?lastFile.getLastDayRecords():0;
							if (reader.getRecordsInDay(i) > lastDayRecords) {
								DailySummaryData dailySummary = reader.readDay(i);
								WeatherSummary summary = WeatherSummaryMapper.map(dailySummary,station);
								currentFile.addWetaherSummary(summary);		
								if(this.getLogger().isDebugEnabled()){
									this.getLogger().debug("Added summary ["+summary.getStation().getId()+", "+summary.getDate()+"]");
								}
								currentFile.setLastDayIndex(i);
								currentFile.setLastDayRecords(reader.getRecordsInDay(i));
							}
							for (int j = lastDayRecords; j < reader.getRecordsInDay(i); j++) {
								DailyWeatherData record = reader.read(i, j);
								WeatherData data = WeatherDataMapper.map(record,station);
								if(!currentFile.getWeatherData().contains(data)){
									currentFile.addWeatherData(data);
								}
								if(this.getLogger().isDebugEnabled()){
									this.getLogger().debug("Added data ["+data.getStation().getId()+", "+data.getDate()+", "+data.getStartTime().toString()+"]");
								}
								localTotalProcessedRecords++;
							}
						}
						//this.produce(currentFile);
						//CONSUMER START
						DAOManager.beginTransaction();
						for(WeatherData data : currentFile.getWeatherData()){
							if(this.getLogger().isDebugEnabled()){
								this.getLogger().debug("Creating data ["+data.getStation().getId()+", "+data.getDate().toString()+", "+data.getStartTime().toString()+"]");
							}
							DAOManager.getDataDAO().create(data);
						}
						for(WeatherSummary summary : currentFile.getWeatherSummaries()){
							if(DAOManager.getSummaryDAO().fetch(summary.getStation(),summary.getDate())!=null){
								if(this.getLogger().isDebugEnabled()){
									this.getLogger().debug("Updating summary ["+summary.getStation().getId()+", "+summary.getDate().toString()+"]");
								}
								DAOManager.getSummaryDAO().update(summary);
							}else{
								if(this.getLogger().isDebugEnabled()){
									this.getLogger().debug("Creating summary ["+summary.getStation().getId()+", "+summary.getDate().toString()+"]");
								}
								DAOManager.getSummaryDAO().create(summary);
							}
						}
						if(null != DAOManager.getFileDAO().fetch(currentFile.getStation(), currentFile.getPeriod())){
							if(this.getLogger().isDebugEnabled()){
								this.getLogger().debug("Updated file ["+currentFile.getStation().getId()+", "+currentFile.getPeriod().toString()+".wlk]");
							}
							currentFile.setDateUpdated(LocalDateTime.now());;
							DAOManager.getFileDAO().update(currentFile);
						}else{
							if(this.getLogger().isDebugEnabled()){
								this.getLogger().debug("Created file ["+currentFile.getStation().getId()+", "+currentFile.getPeriod().toString()+".wlk]");
							}
							currentFile.setDateCreated(LocalDateTime.now());
							currentFile.setDateUpdated(LocalDateTime.now());
							DAOManager.getFileDAO().create(currentFile);
						}
						DAOManager.commitTransaction();
						if(this.getLogger().isDebugEnabled()){
							this.getLogger().debug("Pushing changes to DB");
						}
						//CONSUMER STOP
						if(this.getLogger().isDebugEnabled()){
							this.getLogger().debug("Produced file "+currentFile.getPeriod().toString()+".wlk"+" for Station "+station.getId());
						}
					}
					reader.close();
				}
				if(localTotalProcessedRecords == 0){
					//SEND MAIL NO RECORDS HAS BEEN PROCESSED
				}
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug("Ending process for Station "+station.getId()+". Readed "+localTotalProcessedRecords+" records.");
				}
			}catch(Exception e){
				this.getLogger().error("An exception has been thrown",e);
				processInformation.setAbnormalTermination(true);
				processInformation.setAbnormalTemrminationCause(e.getMessage());
			}finally{
				processInformation.setEnd(LocalDateTime.now());
				processInformation.setProcessedRecords(localTotalProcessedRecords);
				DAOManager.beginTransaction();
				DAOManager.getProcessInformationDAO().create(processInformation);
				DAOManager.commitTransaction();
			}
		}
	}

}
