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
import ar.asimov.acumar.ema.model.WeatherData;

public class WeatherDataDAO {
	
	private final EntityManager entityManager;
	
	
	public WeatherDataDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	public WeatherData fetch(WeatherData.PrimaryKey id){
		return this.entityManager.find(WeatherData.class, id);
	}
	
	public List<WeatherData> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		cq.select(root);
		TypedQuery<WeatherData> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherData> fetchAll(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		EntityType<WeatherData> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherData.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherData> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public WeatherData fetchLast(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		EntityType<WeatherData> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherData.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherMeasure_.getSingularAttribute("date"))),cb.desc(root.get(WeatherMeasure_.getSingularAttribute("time"))));
		TypedQuery<WeatherData> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	public Integer count(Station station,LocalDate date){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		EntityType<WeatherData> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherData.class);
		cq.where(cb.and(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station),
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("date")),date))
				);
		cq.select(root);
		TypedQuery<WeatherData> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return 0;
		}
		return tq.getResultList().size();
	}


	public void create(WeatherData weatherMeasure) {
		this.entityManager.persist(weatherMeasure);
		
	}
	
}

