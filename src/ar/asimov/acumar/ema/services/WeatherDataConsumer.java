package ar.asimov.acumar.ema.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class WeatherDataConsumer implements Runnable {
	
	private static final int COMMIT_TRANSACTION_LIMIT = 1000;
	
	private Queue<WeatherMeasure> measures;
	private Map<Station,Integer> consumed;
	private boolean stop;
	private final Log logger;
	private Short exitCode;
	private boolean running;
	private int limit;
	
	public WeatherDataConsumer(Queue<WeatherMeasure> sharedQueue,int limit) {
		this.measures = sharedQueue;
		this.stop = false;
		this.logger = LogFactory.getLog(this.getClass());
		this.exitCode = 0;
		this.running = false;
		this.consumed= new HashMap<>();
	}
	
	protected Log getLogger(){
		return this.logger;
	}
	
	public short getExitCode(){
		return (this.running)?null:this.exitCode;
	}

	@Override
	public void run() {
		this.running = true;
		Integer totalConsumed = 0;
		boolean commit = false;
		//EntityManagerHelper.beginTransaction();
		try{
			EntityManagerHelper.beginTransaction();
			while(!this.stop && !this.measures.isEmpty()){
				WeatherMeasure consumed = this.consume();
				if(null == consumed){
					this.getLogger().info("Null object encountered");
				}else{
					EntityManagerHelper.getEntityManager().persist(consumed);
				}
				Thread.sleep(50);
				if(commit){
					EntityManagerHelper.commitTransaction();
				}
				if(this.getLogger().isDebugEnabled()){
					this.getLogger().debug(Thread.currentThread().getName()+": "+"Persisting "+consumed);
				}
			}
			//EntityManagerHelper.commitTransaction();
		}catch(InterruptedException e){
			this.getLogger().fatal("An InterruptedException happened",e);
			this.exitCode = 1;
			EntityManagerHelper.rollbackTransaction();
		}finally{
			EntityManagerHelper.closeEntityManager();
			this.running = false;
		}
	}

	
	protected WeatherMeasure consume() throws InterruptedException{
		while(this.measures.isEmpty()){
			synchronized(this.measures){
				this.measures.wait();
			}
		}
		synchronized(this.measures){
			this.measures.notifyAll();
			return this.measures.poll();
		}
	}
	
	public void stop(){
		this.stop = true;
	}

}
