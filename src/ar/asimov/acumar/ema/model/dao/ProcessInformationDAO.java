package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ar.asimov.acumar.ema.model.ProcessInformation;
import ar.asimov.acumar.ema.model.WeatherSummary;

public class ProcessInformationDAO {
	
	private final EntityManager entityManager;
	
	
	public ProcessInformationDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public List<ProcessInformation> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<ProcessInformation> cq = cb.createQuery(ProcessInformation.class);
		Root<ProcessInformation> root = cq.from(ProcessInformation.class);
		cq.select(root);
		TypedQuery<ProcessInformation> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
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
