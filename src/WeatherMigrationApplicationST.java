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
import ar.asimov.acumar.ema.wlk.test.TestWholeProcess;

public class WeatherMigrationApplicationST {

	public static final Log LOGGER = LogFactory.getLog(TestWholeProcess.class);
	public static final int REPORTS_QUEUE_CAPACITY = 50;

	public static Log getLogger() {
		return LOGGER;
	}

	public static void main(String[] args) {
		final List<Station> stations = DAOManager.getStationDAO().fetchAll();
		for (Station station : stations) {
			ProcessInformation processInformation = new ProcessInformation();
			processInformation.setStation(station);
			processInformation.setStart(LocalDateTime.now());
			processInformation.setAbnormalTermination(false);
			int localTotalProcessed = 0;
			try {
				WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
				List<WeatherFile> files = produce(station,lastFile);
				localTotalProcessed = consume(files);
			} catch (Exception e) {
				processInformation.setAbnormalTermination(true);
				processInformation.setAbnormalTemrminationCause(e.getMessage());
				e.printStackTrace();
			} finally {
				processInformation.setEnd(LocalDateTime.now());
				processInformation.setProcessedRecords(localTotalProcessed);
				DAOManager.beginTransaction();
				DAOManager.getProcessInformationDAO().create(processInformation);
				DAOManager.commitTransaction();
			}
		}
		DAOManager.close();
	}

	private static Integer consume(List<WeatherFile> files) {
		int processedRecords = 0;
		while (!files.isEmpty()) {
			WeatherFile file = files.remove(0);
			if (getLogger().isDebugEnabled()) {
				if (null == file.getStation()) {
					getLogger().debug("A null station has been provided for the file.");
				} else {
					getLogger().debug(
							"Processing file " + file.getPeriod() + ".wlk for Station " + file.getStation().getId());
				}
			}
			DAOManager.beginTransaction();
			for (WeatherData data : file.getWeatherData()) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Creating data [" + data.getStation().getId() + ", " + data.getDate().toString()
							+ ", " + data.getStartTime().toString() + "]");
				}
				DAOManager.getDataDAO().create(data);
				processedRecords++;
				if(processedRecords >= 1000 && processedRecords % 1000 == 0){
					DAOManager.commitTransaction();
					DAOManager.beginTransaction();
					try{
						Thread.sleep(500);
					}catch(InterruptedException e){
						//DO NOTHING
					}
				}
			}
			for (WeatherSummary summary : file.getWeatherSummaries()) {
				if (DAOManager.getSummaryDAO().fetch(summary.getStation(), summary.getDate()) != null) {
					if (getLogger().isDebugEnabled()) {
						getLogger().debug("Updating summary [" + summary.getStation().getId() + ", "
								+ summary.getDate().toString() + "]");
					}
					DAOManager.getSummaryDAO().update(summary);
				} else {
					if (getLogger().isDebugEnabled()) {
						getLogger().debug("Creating summary [" + summary.getStation().getId() + ", "
								+ summary.getDate().toString() + "]");
					}
					DAOManager.getSummaryDAO().create(summary);
				}
			}
			if (null != DAOManager.getFileDAO().fetch(file.getStation(), file.getPeriod())) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Updated file [" + file.getStation().getId() + ", " + file.getPeriod().toString()
							+ ".wlk]");
				}
				file.setDateUpdated(LocalDateTime.now());
				;
				DAOManager.getFileDAO().update(file);
			} else {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Created file [" + file.getStation().getId() + ", " + file.getPeriod().toString()
							+ ".wlk]");
				}
				file.setDateCreated(LocalDateTime.now());
				file.setDateUpdated(LocalDateTime.now());
				DAOManager.getFileDAO().create(file);
			}
			DAOManager.commitTransaction();
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Pushing changes to DB");
			}
		}
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("File consumer finished");
		}
		return processedRecords;
	}

	private static List<WeatherFile> produce(Station station, WeatherFile lastFileProcessed) throws IOException {
		List<WeatherFile> files = new ArrayList<>();
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("Starting producer for Station " + station.getId());
		}
		Path path = Paths.get(station.getDirectoryPath());
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
		for (Path wlkFile : wlkFiles) {
			WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Processing file " + wlkFile.toString());
			}
			if (null == lastFileProcessed || !lastFileProcessed.getPeriod().isAfter(reader.getFilePeriod())) {
				WeatherFile currentFile;
				currentFile = new WeatherFile();
				currentFile.setStation(station);
				currentFile.setPeriod(reader.getFilePeriod());
				currentFile.setTotalRecords(reader.getTotalRecords());
				int startDayIndex = (null != lastFileProcessed
						&& lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))
								? lastFileProcessed.getLastDayIndex() : 1;
				for (int i = startDayIndex; i <= currentFile.getPeriod().atEndOfMonth().getDayOfMonth(); i++) {
					int lastDayRecords = (null != lastFileProcessed
							&& lastFileProcessed.getPeriod().equals(reader.getFilePeriod()))
									? lastFileProcessed.getLastDayRecords() : 0;
					if (reader.getRecordsInDay(i) > lastDayRecords) {
						DailySummaryData dailySummary = reader.readDay(i);
						WeatherSummary summary = WeatherSummaryMapper.map(dailySummary, station);
						currentFile.addWetaherSummary(summary);
						if (getLogger().isDebugEnabled()) {
							getLogger().debug(
									"Added summary [" + summary.getStation().getId() + ", " + summary.getDate() + "]");
						}
						currentFile.setLastDayIndex(i);
						currentFile.setLastDayRecords(reader.getRecordsInDay(i));
					}
					for (int j = lastDayRecords; j < reader.getRecordsInDay(i); j++) {
						DailyWeatherData record = reader.read(i, j);
						WeatherData data = WeatherDataMapper.map(record, station);
						if (!currentFile.getWeatherData().contains(data)) {
							currentFile.addWeatherData(data);
						}
						if (getLogger().isDebugEnabled()) {
							getLogger().debug("Added data [" + data.getStation().getId() + ", " + data.getDate() + ", "
									+ data.getStartTime().toString() + "]");
						}
					}
				}
				files.add(currentFile);
			}
			reader.close();
		}
		return files;
	}

}
