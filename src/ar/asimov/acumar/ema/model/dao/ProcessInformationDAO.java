package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;

import ar.asimov.acumar.ema.model.ProcessInformation;

public class ProcessInformationDAO {
	
	private final EntityManager entityManager;
	
	
	public ProcessInformationDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public List<ProcessInformation> fetchAll(){
		return null;
	}
	
	public void create(ProcessInformation processInformation){
		this.entityManager.persist(processInformation);
	}
	
	public void update(ProcessInformation processInformation){
		this.entityManager.merge(processInformation);
	}
	
	public ProcessInformation fetch(Long id){
		return this.entityManager.find(ProcessInformation.class, id);
	}

}
