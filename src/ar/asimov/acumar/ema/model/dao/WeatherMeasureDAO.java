package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherMeasure;

public class WeatherMeasureDAO {
	
	private final EntityManager entityManager;
	
	
	public WeatherMeasureDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	public WeatherMeasure fetch(WeatherMeasure.PrimaryKey id){
		return this.entityManager.find(WeatherMeasure.class, id);
	}
	
	public List<WeatherMeasure> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherMeasure> cq = cb.createQuery(WeatherMeasure.class);
		Root<WeatherMeasure> root = cq.from(WeatherMeasure.class);
		cq.select(root);
		TypedQuery<WeatherMeasure> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherMeasure> fetchAll(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherMeasure> cq = cb.createQuery(WeatherMeasure.class);
		Root<WeatherMeasure> root = cq.from(WeatherMeasure.class);
		EntityType<WeatherMeasure> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherMeasure.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherMeasure> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public void create(Station station){
		this.entityManager.persist(station);
	}
	
	public void update(Station station){
		this.entityManager.merge(station);
	}
		
}

