	package ar.asimov.acumar.ema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

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

public class WeatherMigrationApplication {
	
	private static final int REPORTS_QUEUE_CAPACITY = 50;
	private static final Log LOGGER = LogFactory.getLog(WeatherMigrationApplication.class);
	
	public static Log getLogger(){
		return LOGGER;
	}

	public static void main(String[] args) {
		final ArrayBlockingQueue<WeatherFile> files = new ArrayBlockingQueue<>(REPORTS_QUEUE_CAPACITY);
		final List<Station> stations = DAOManager.getStationDAO().fetchAll(true);
		for(Station station : stations){
			ProcessInformation processInformation = new ProcessInformation();
			processInformation.setStation(station);
			processInformation.setStart(LocalDateTime.now());
			processInformation.setAbnormalTermination(false);
			Integer totalConsumed = 0;
			Integer totalProduced = 0;
			try{
				final ThreadPoolExecutor executors = (ThreadPoolExecutor)Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				if(getLogger().isDebugEnabled()){
					getLogger().debug("Thread pool maximum size is "+executors.getMaximumPoolSize());
				}
				WeatherFile lastFile = DAOManager.getFileDAO().fetchLast(station);
				WeatherFileProducer producer = new WeatherFileProducer(station,files,lastFile);
				final List<Future<Integer>> consumersResults = new ArrayList<>();
				Future<Integer> producerResult = executors.submit(producer);
				Thread.sleep(200);
				while((executors.getActiveCount() < executors.getMaximumPoolSize()|| consumersResults.isEmpty())){
					WeatherFileConsumer consumer = new WeatherFileConsumer(files);
					consumersResults.add(executors.submit(consumer));
				}
				executors.shutdown();
				totalProduced = producerResult.get();
				for(Future<Integer> result : consumersResults){
					totalConsumed+=result.get();
				}
			}catch(ExecutionException | InterruptedException | CancellationException e){
				getLogger().error("An exception has been thrown",e);
				processInformation.setAbnormalTermination(true);
				processInformation.setAbnormalTemrminationCause(e.getMessage());
			}finally{
				processInformation.setEnd(LocalDateTime.now());
				processInformation.setProcessedRecords(totalConsumed);
				if(totalProduced == 0){
					Message message = new Message();
					message.setTitle(station.getName()+" no ha registrado datos");
					message.setMessage("El proceso de migracion de datos iniciado a las "+ processInformation.getStart().format(DateTimeFormatter.ofPattern("dd/MM/Y hh:mm:ss a"))+ " y finalizado a las "+processInformation.getEnd().format(DateTimeFormatter.ofPattern("dd/MM//Y hh:mm:ss a"))+" para la estacion "+ station.getName() +" no ha producido ningun registro");
					sendMail(message);
				}
				NotificationService.getInstance().terminate();
				DAOManager.beginTransaction();
				DAOManager.getProcessInformationDAO().create(processInformation);
				DAOManager.commitTransaction();
			}

		}

	}
	
	
	public static void sendMail(Message message){
		Email email = new SimpleEmail();
		try {
			email.setFrom("agomez@acumar");
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator("alejandro.gomez.auad@gmail.com", "Gmail+1806$"));
			email.setStartTLSRequired(true);
			email.setStartTLSEnabled(true);
			email.getMailSession().getProperties().put("mail.smtps.auth","true");
			email.getMailSession().getProperties().put("mail.debug", "true");
			email.getMailSession().getProperties().put("mail.smtps.port", "587");
			email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587");
			email.getMailSession().getProperties().put("mail.smtps.socketFactory.class",   "javax.net.ssl.SSLSocketFactory");
			email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false");
			email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
			email.setFrom("agomez@acumar.gov.ar");
			email.addTo("agomez@acumar.gov.ar");
			//email.addTo("plema@acumar.gov.ar");
			email.setSubject(message.getTitle());
			email.setMsg(message.getMessage());
			email.send();
		} catch (EmailException e) {
			getLogger().error(e);
			//e.printStackTrace();
		}
		
	}
	
}
