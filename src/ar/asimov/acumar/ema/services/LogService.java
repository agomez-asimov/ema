package ar.asimov.acumar.ema.services;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.dao.ProcessLogDAO;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class LogService {
	
	private static Thread THREAD;
	private static LogService INSTANCE;
	
	public static LogService getInstance(){
		if(INSTANCE == null){
			INSTANCE = new LogService();
			THREAD = new Thread(new Runnable() {
				
				@Override
				public void run() {
					INSTANCE.running = true;
					INSTANCE.logger = new ProcessLogDAO(EntityManagerHelper.getEntityManager());
					while(!INSTANCE.stop){
						try{
							ProcessLog log = INSTANCE.readNextLog();
							EntityManagerHelper.beginTransaction();
							INSTANCE.logger.create(log);
							EntityManagerHelper.commitTransaction();
						}catch(InterruptedException e){
							INSTANCE.running = false;
							throw new RuntimeException(e);
						}
					}
					INSTANCE.running = false;
				}
			});
			THREAD.start();
			
		}
		return INSTANCE;
	}
	
	private Queue<ProcessLog> logs;
	private ProcessLogDAO logger;
	private boolean stop;
	private boolean running;
	
	protected LogService() {
		this.logs = new LinkedBlockingQueue<>();
	}
	
	public void log(ProcessLog log){
		synchronized(this.logs){
			this.logs.add(log);
			this.logs.notifyAll();
		}
	}

	
	public boolean isRunning(){
		return this.running;
	}
	
	public void stop(){
		this.stop = true;
	}
	
	protected ProcessLog readNextLog() throws InterruptedException{
		while(this.logs.isEmpty()){
			synchronized(this.logs){
				this.logs.wait();
			}
		}
		synchronized(this.logs){
			this.logs.notifyAll();
			return this.logs.poll();	
		}
	}
	
	public ProcessLog getLastLog(Station station){
		return this.logger.fetchLast(station);
	}

}
