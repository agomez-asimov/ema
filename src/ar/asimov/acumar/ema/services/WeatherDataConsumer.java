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
	private boolean commit;
	
	public WeatherDataConsumer(Queue<WeatherMeasure> sharedQueue) {
		this.measures = sharedQueue;
		this.stop = false;
		this.logger = LogFactory.getLog(this.getClass());
		this.exitCode = 0;
		this.running = false;
		this.commit = false;
		this.consumed= new HashMap<>();
	}
	
	protected void registerStation(Station station){
		this.setConsumed(station, 0);
	}
	
	public Integer getConsumed(Station station){
		if(!this.consumed.containsKey(station)) this.registerStation(station);
		return this.consumed.get(station);
	}
	
	public void setConsumed(Station station,Integer consumed){
		this.consumed.put(station, consumed);
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
		Map<Station,Integer> processInfo = new HashMap<>();
		//EntityManagerHelper.beginTransaction();
		try{
			while(!this.stop){
				if(!this.commit){
					EntityManagerHelper.beginTransaction();
				}
				WeatherMeasure consumed = this.consume();
				this.commit = (totalConsumed == COMMIT_TRANSACTION_LIMIT);
				if(null == consumed){
					this.getLogger().info("Null object encountered");
				}else{
					EntityManagerHelper.getEntityManager().persist(consumed);
					processInfo.put(consumed.getStation(),processInfo.get(consumed.getStation())+1);
				}
				Thread.sleep(50);
				if(this.commit){
					EntityManagerHelper.commitTransaction();
					for(Station station : processInfo.keySet()){
						this.setConsumed(station, this.getConsumed(station)+processInfo.get(station));
					}
					processInfo.clear();
					this.commit = false;
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
			processInfo.clear();
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
