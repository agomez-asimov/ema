package ar.asimov.acumar.ema.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class EmailConsumer implements Runnable{
	
	private static final Map<String,Object> DEFAULT_OPTIONS;
	static{
		DEFAULT_OPTIONS = new HashMap<>();
		DEFAULT_OPTIONS.put("ar.asimov.acumar.ema.email.host","smtp.googlemail.com");
		DEFAULT_OPTIONS.put("ar.asimov.acumar.ema.email.port", 465);
		DEFAULT_OPTIONS.put("ar.asimov.acumar.ema.email.user", "useraname");
		DEFAULT_OPTIONS.put("ar_asimov.acumar.ema.email.clave", "clave");
		DEFAULT_OPTIONS.put("ar.asimov.acumar.ema.email.ssl", true);
		DEFAULT_OPTIONS.put("ar.asimov.acumar.ema.email.account" , "agomez@acumar.gov.ar");
	}

	private Queue<String> messages;
	private Map<String,?> options;
	private Email email;
	private boolean stop;
	private boolean running;
	

	public EmailConsumer(Map<String,?> options) {
		this.options = (null == options)?DEFAULT_OPTIONS:options;
		this.email = new HtmlEmail();
		this.email.setHostName(String.valueOf(this.options.get("ar.asimov.acumar.email.host")));
		this.email.setSmtpPort(Integer.valueOf(String.valueOf(this.options.get("ar.asimov.acumar.ema.email.port"))));
		this.email.setAuthenticator(new DefaultAuthenticator(String.valueOf(this.options.get("ar.asimov.acumar.ema.email.user")),String.valueOf(this.options.get("ar.asimov.acumar.email.password"))));
		this.email.setSSLOnConnect(Boolean.valueOf(String.valueOf(this.options.get("ar.asimov.acumar.ema.email.ssl"))));
		try{
			this.email.setFrom(String.valueOf(this.options.get("ar.asimov.acumar.ema.email.ssl")));
		}catch(EmailException e){
			throw new RuntimeException(e);
		}
		this.stop = false;
		this.running = true;
	}
	
	private String consume() throws InterruptedException{
		while(this.messages.isEmpty()){
			synchronized(this.messages){
				this.messages.wait();
			}
		}
		synchronized(this.messages){
			this.messages.notifyAll();
			return this.messages.poll();
		}
	}

	@Override
	public void run() {
		this.running = true;
		try{
			while(!this.stop){
				String message = this.consume();
				this.email.setMsg(message);
			}
			this.running = false;
		}catch(InterruptedException | EmailException e){
			throw new RuntimeException(e);
		}finally{
			this.running = false;
		}
	}
	
	public Boolean isRunning(){
		return this.running;
	}
	
	
}
