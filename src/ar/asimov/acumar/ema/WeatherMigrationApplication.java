package ar.asimov.acumar.ema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import ar.asimov.acumar.ema.model.ProcessInformation;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.dao.DAOManager;
import ar.asimov.acumar.ema.services.Message;
import ar.asimov.acumar.ema.services.WeatherFileConsumer;
import ar.asimov.acumar.ema.services.WeatherFileProducer;

public class WeatherMigrationApplication implements Runnable {

	private static final int REPORTS_QUEUE_CAPACITY = 50;
	private static final Log LOGGER = LogFactory.getLog(WeatherMigrationApplication.class);

	private static Configuration CONFIGURATION;
	
	public static Log getLogger() {
		return LOGGER;
	}

	public static void main(String[] args) {
		final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		Long delay = getConfiguration().getLong("execution.period.value");
		TimeUnit unit = TimeUnit.valueOf(getConfiguration().getString("execution.period.unit"));
		if(null != unit && null != delay){
			final ScheduledFuture<?> handler = service.scheduleWithFixedDelay(new WeatherMigrationApplication(), 0, 1,
					TimeUnit.HOURS);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				while (!service.awaitTermination(500, TimeUnit.MILLISECONDS)) {
					String command = null;
					try {
						command = reader.readLine();
					} catch (IOException e) {
						Thread.sleep(100);
					}
					switch (command) {
					case "quit":
						handler.cancel(false);
						service.shutdownNow();
						break;
					default:
						Thread.sleep(100);
						break;
					}
				}
			} catch (InterruptedException e) {
				getLogger().error(e);
			}
		}
	}

	@Override
	public void run() {
		final int threadPoolSize = Runtime.getRuntime().availableProcessors();
		final ArrayBlockingQueue<WeatherFile> files = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY);
		final List<Station> stations = DAOManager.getStationDAO().fetchAll(true);
		final List<Message> messages = new ArrayList<>();
		if (getLogger().isInfoEnabled()) {
			getLogger().info("WeatherMigrationApplication started");
		}
		CountDownLatch startSignal;
		CountDownLatch doneSignal;
		final ThreadPoolExecutor executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
		for (Station station : stations) {
			startSignal = new CountDownLatch(1);
			doneSignal = new CountDownLatch(threadPoolSize);
			ProcessInformation processInformation = new ProcessInformation();
			processInformation.setStation(station);
			processInformation.setStart(LocalDateTime.now());
			processInformation.setAbnormalTermination(false);
			Integer totalConsumed = 0;
			Integer totalProduced = 0;
			try {
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Thread pool maximum size is " + executors.getMaximumPoolSize());
				}
				WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
				WeatherFileProducer producer = new WeatherFileProducer(station, files, lastFile, startSignal,
						doneSignal);
				final List<Future<Integer>> consumersResults = new ArrayList<>();
				Future<Integer> producerResult = executors.submit(producer);
				while ((executors.getActiveCount() < executors.getMaximumPoolSize() || consumersResults.isEmpty())) {
					WeatherFileConsumer consumer = new WeatherFileConsumer(files, startSignal, doneSignal);
					consumersResults.add(executors.submit(consumer));
				}
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("Excutors.size()=" + executors.getActiveCount());
					getLogger().debug("CountDownLatch.size()=" + doneSignal.getCount());
				}
				while (!doneSignal.await(5, TimeUnit.SECONDS)) {
					if (getLogger().isDebugEnabled()) {
						getLogger().debug("CountDownLatch.size()=" + doneSignal.getCount());
					}
				}
				totalProduced = producerResult.get();
				for (Future<Integer> result : consumersResults) {
					totalConsumed += result.get();
				}
			} catch (ExecutionException | InterruptedException | CancellationException e) {
				getLogger().error("An exception has been thrown", e);
				processInformation.setAbnormalTermination(true);
				processInformation.setAbnormalTemrminationCause(e.getLocalizedMessage());
				continue;
			} finally {
				processInformation.setEnd(LocalDateTime.now());
				processInformation.setProcessedRecords(totalProduced);
				if (processInformation.getProcessedRecords() == 0) {
					Message message = new Message();
					message.setTitle(station.getName() + " no ha registrado datos");
					message.setMessage("El proceso de migracion de datos iniciado a las "
							+ processInformation.getStart().format(DateTimeFormatter.ofPattern("dd/MM/Y hh:mm:ss a"))
							+ " y finalizado a las "
							+ processInformation.getEnd().format(DateTimeFormatter.ofPattern("dd/MM//Y hh:mm:ss a"))
							+ " para la estacion " + station.getName() + " no ha producido ningun registro");
					messages.add(message);
				}
				/*
				*/
				try {
					DAOManager.beginTransaction();
					DAOManager.getProcessInformationDAO().create(processInformation);
					DAOManager.commitTransaction();
				} catch (Exception e) {
					getLogger().error(e);
				}
			}
		}
		executors.shutdown();
		sendMail(messages);
		if (getLogger().isInfoEnabled()) {
			getLogger().info("WeatherMigrationApplication finished");
		}
	}

	public static void sendMail(List<Message> messages) {
		if (messages.size() > 0) {
			Email email = new SimpleEmail();
			StringBuffer sb = new StringBuffer();
			for (Message message : messages) {
				sb.append(message.getTitle()).append(" - ").append(message.getMessage()).append("\n");
			}
			try {
				String[] emails = getConfiguration().getStringArray("alert.email");
				if(emails.length > 0){
					email.setFrom("agomez@acumar");
					email.setHostName("smtp.gmail.com");
					email.setSmtpPort(587);
					email.setAuthenticator(new DefaultAuthenticator("alejandro.gomez.auad@gmail.com", "Gmail+1806$"));
					email.setStartTLSRequired(true);
					email.setStartTLSEnabled(true);
					email.getMailSession().getProperties().put("mail.smtps.auth", "true");
					email.getMailSession().getProperties().put("mail.debug", "true");
					email.getMailSession().getProperties().put("mail.smtps.port", "587");
					email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587");
					email.getMailSession().getProperties().put("mail.smtps.socketFactory.class",
							"javax.net.ssl.SSLSocketFactory");
					email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false");
					email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
					email.setFrom("agomez@acumar.gov.ar");
					
					for(int i = 0;i<emails.length;i++){
						email.addTo(emails[i]);
					}
					email.setSubject("Notificacion de Estaciones Meteorologicas");
					email.setMsg(sb.toString());
					email.send();
				}else{
					if(getLogger().isInfoEnabled()){
						getLogger().info("No email has been specified for alerts");
					}
				}
			} catch (EmailException e) {
				getLogger().error(e);
				// e.printStackTrace();
			}
		}

	}

	private static Configuration getConfiguration() {
		final Path executionPath = Paths.get(System.getProperty("user.dir"));
		if (null == CONFIGURATION) {
			Configurations configs = new Configurations();
			try {
				Path filePath = executionPath.resolve("ema-config.properties");
				if(!Files.exists(filePath)){
					Files.createFile(filePath);
				}
				FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(filePath.toFile());
				PropertiesConfiguration config = builder.getConfiguration();
				if (!config.containsKey("execution.period.value")) {
					config.setProperty("execution.period.value", 1);
					config.setProperty("execution.period.unit", TimeUnit.HOURS.toString());
				}
				if (!config.containsKey("alert.email")) {
					config.setProperty("alert.email", "agomez@acumar.gov.ar");
				}
				builder.save();
				return config;
			} catch (ConfigurationException | IOException e) {
				getLogger().error(e);
				e.printStackTrace();
			}
		}
		return CONFIGURATION;
	}

}
