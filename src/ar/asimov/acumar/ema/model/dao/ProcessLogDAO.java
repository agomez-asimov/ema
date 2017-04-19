package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.ProcessLog;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherReport;

public class ProcessLogDAO {
	
	private final EntityManager entityManager;
	
	public ProcessLogDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public ProcessLog fetch(Integer id){
		return this.entityManager.find(ProcessLog.class, id);
	}
	
	public List<ProcessLog> fetchAll(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<ProcessLog> cq = cb.createQuery(ProcessLog.class);
		Root<ProcessLog> root = cq.from(ProcessLog.class);
		EntityType<ProcessLog> ProcessLog_ = this.entityManager.getMetamodel().entity(ProcessLog.class);
		cq.where(
				cb.equal(root.get(ProcessLog_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<ProcessLog> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public ProcessLog fetchLast(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<ProcessLog> cq = cb.createQuery(ProcessLog.class);
		Root<ProcessLog> root = cq.from(ProcessLog.class);
		EntityType<ProcessLog> ProcessLog_ = this.entityManager.getMetamodel().entity(ProcessLog.class);
		cq.where(
				cb.equal(root.get(ProcessLog_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(ProcessLog_.getSingularAttribute("start"))));
		TypedQuery<ProcessLog> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	public void create(ProcessLog processLog){
		this.entityManager.persist(processLog);
	}
	
	public void update(ProcessLog processLog){
		this.entityManager.merge(processLog);
	}

}
