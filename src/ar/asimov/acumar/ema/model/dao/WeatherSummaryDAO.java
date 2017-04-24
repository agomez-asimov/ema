package ar.asimov.acumar.ema.model.dao;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherSummary;

public class WeatherSummaryDAO {

	private final EntityManager entityManager;
	
	
	public WeatherSummaryDAO(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	
	public WeatherSummary fetch(Station station,LocalDate date){
		WeatherSummary.PrimaryKey pk = new WeatherSummary.PrimaryKey();
		pk.setStation(station.getId());
		pk.setDate(date);
		return this.entityManager.find(WeatherSummary.class, pk);
	}
	
	public List<WeatherSummary> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherSummary> cq = cb.createQuery(WeatherSummary.class);
		Root<WeatherSummary> root = cq.from(WeatherSummary.class);
		cq.select(root);
		TypedQuery<WeatherSummary> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherSummary> fetchAll(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherSummary> cq = cb.createQuery(WeatherSummary.class);
		Root<WeatherSummary> root = cq.from(WeatherSummary.class);
		EntityType<WeatherSummary> WeatherSummary_ = this.entityManager.getMetamodel().entity(WeatherSummary.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherSummary> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public WeatherSummary fetchLast(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherSummary> cq = cb.createQuery(WeatherSummary.class);
		Root<WeatherSummary> root = cq.from(WeatherSummary.class);
		EntityType<WeatherSummary> WeatherSummary_ = this.entityManager.getMetamodel().entity(WeatherSummary.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherSummary_.getSingularAttribute("date"))));
		TypedQuery<WeatherSummary> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	
	
	public void create(WeatherSummary report){
		this.entityManager.persist(report);
	}
	
	public void update(WeatherSummary report){
		this.entityManager.merge(report);
	}
}
