package ar.asimov.acumar.ema.model.dao;

import javax.persistence.EntityManager;

import ar.asimov.acumar.ema.model.Configuration;

public class ConfigurationDAO {
	
	private final EntityManager entityManager;
	
	
	public ConfigurationDAO(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	
	public Configuration fetch(String id){
		return this.entityManager.find(Configuration.class,id);
	}

}
