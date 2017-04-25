package ar.asimov.acumar.ema.wlk.test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

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
		final ExecutorService executors = Executors.newFixedThreadPool(stations.size() * 2);
		for (Station station : stations) {
			WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
			Path path = Paths.get(station.getDirectoryPath());
			try {
				DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.wlk");
				int localTotalProcessedRecords = 0;
				for (Path wlkFile : stream) {
					WLinkFileReader reader = new WLinkFileReader(wlkFile.toString());
					if (getLogger().isDebugEnabled()) {
						getLogger().debug("Processing file " + wlkFile.toString());
					}
					if (null == lastFile) {
						lastFile = new WeatherFile();
						lastFile.setStation(station);
						lastFile.setPeriod(reader.getFilePeriod());
						lastFile.setLastDayIndex(1);
						lastFile.setLastDayRecords(0);
					}
					if (!lastFile.getPeriod().isAfter(reader.getFilePeriod())) {
						WeatherFile currentFile;
						if (lastFile.getPeriod().equals(reader.getFilePeriod())) {
							currentFile = lastFile;
						} else {
							currentFile = new WeatherFile();
							currentFile.setStation(station);
							currentFile.setPeriod(reader.getFilePeriod());
							currentFile.setTotalRecords(reader.getTotalRecords());
						}
						int startDayIndex = lastFile.getLastDayIndex();
						for (int i = startDayIndex; i <= reader.getFilePeriod().atEndOfMonth().getDayOfMonth(); i++) {
							int lastDayRecords = (lastFile.getPeriod().equals(reader.getFilePeriod()))
									? lastFile.getLastDayRecords() : 0;
							if (reader.getRecordsInDay(i) > lastDayRecords) {
								DailySummaryData dailySummary = reader.readDay(i);

								WeatherSummary summary = WeatherSummaryMapper.map(dailySummary, station);
								currentFile.addWetaherSummary(summary);
								if (getLogger().isDebugEnabled()) {
									getLogger().debug(
											"Added summary [" + summary.getStation() + ", " + summary.getDate() + "]");
								}
								currentFile.setLastDayIndex(i);
								currentFile.setLastDayRecords(reader.getRecordsInDay(i));
							}
							for (int j = lastDayRecords; j < reader.getRecordsInDay(i); j++) {
								DailyWeatherData record = reader.read(i, j);
								WeatherData data = WeatherDataMapper.map(record, station);
								currentFile.addWeatherData(data);
								if (getLogger().isDebugEnabled()) {
									getLogger().debug("Added data [" + data.getStation() + ", " + data.getDate() + ", "
											+ data.getStartTime().toString() + "]");
								}
								localTotalProcessedRecords++;
							}
						}
						persistFile(currentFile);
					}
					reader.close();
				}
			} catch (IOException e) {
				getLogger().error(null, e);
			}

		}
	}

	private void persistFile(WeatherFile file) {
		DAOManager.beginTransaction();
		int recordsToCommit = 0;
		for (WeatherData data : file.getWeatherData()) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Creating data [" + data.getStation().getId() + ", " + data.getDate().toString()
						+ ", " + data.getStartTime().toString() + "]");
			}
			DAOManager.getDataDAO().create(data);
			recordsToCommit++;
			if (recordsToCommit == 100) {
				DAOManager.commitTransaction();
				DAOManager.beginTransaction();
				recordsToCommit = 0;
			}
		}
		for (WeatherSummary summary : file.getWeatherSummaries()) {
			if (DAOManager.getSummaryDAO().fetch(summary.getStation(), summary.getDate()) != null) {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Updating summary [" + summary.getStation().getId() + ", "
							+ summary.getDate().toString() + "]");
				}
				DAOManager.getSummaryDAO().update(summary);
				recordsToCommit++;
			} else {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Creating summary [" + summary.getStation().getId() + ", "
							+ summary.getDate().toString() + "]");
				}
				DAOManager.getSummaryDAO().create(summary);
				recordsToCommit++;
			}
			if (recordsToCommit == 100) {
				DAOManager.commitTransaction();
				DAOManager.beginTransaction();
				recordsToCommit = 0;
			}
		}
		if (null != DAOManager.getFileDAO().fetch(file.getStation(), file.getPeriod())) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug(
						"Updated file [" + file.getStation().getId() + ", " + file.getPeriod().toString() + ".wlk]");
			}
			file.setDateUpdated(LocalDateTime.now());
			;
			DAOManager.getFileDAO().update(file);
			recordsToCommit++;
		} else {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug(
						"Created file [" + file.getStation().getId() + ", " + file.getPeriod().toString() + ".wlk]");
			}
			file.setDateCreated(LocalDateTime.now());
			file.setDateUpdated(LocalDateTime.now());
			DAOManager.getFileDAO().create(file);
			recordsToCommit++;
		}
		DAOManager.commitTransaction();
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("Pushing changes to DB");
		}
	}

}
