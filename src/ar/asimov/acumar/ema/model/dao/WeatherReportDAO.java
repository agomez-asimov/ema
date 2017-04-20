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
import ar.asimov.acumar.ema.model.WeatherReport;

public class WeatherReportDAO {
	
	private final EntityManager entityManager;
	
	
	public WeatherReportDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	public WeatherReport fetch(WeatherReport.PrimaryKey id){
		return this.entityManager.find(WeatherReport.class, id);
	}
	
	public List<WeatherReport> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherReport> cq = cb.createQuery(WeatherReport.class);
		Root<WeatherReport> root = cq.from(WeatherReport.class);
		cq.select(root);
		TypedQuery<WeatherReport> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherReport> fetchAll(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherReport> cq = cb.createQuery(WeatherReport.class);
		Root<WeatherReport> root = cq.from(WeatherReport.class);
		EntityType<WeatherReport> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherReport.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherReport> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public WeatherReport fetchLast(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherReport> cq = cb.createQuery(WeatherReport.class);
		Root<WeatherReport> root = cq.from(WeatherReport.class);
		EntityType<WeatherReport> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherReport.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherMeasure_.getSingularAttribute("date"))),cb.desc(root.get(WeatherMeasure_.getSingularAttribute("time"))));
		TypedQuery<WeatherReport> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	public Integer count(Station station,LocalDate date){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherReport> cq = cb.createQuery(WeatherReport.class);
		Root<WeatherReport> root = cq.from(WeatherReport.class);
		EntityType<WeatherReport> WeatherMeasure_ = this.entityManager.getMetamodel().entity(WeatherReport.class);
		cq.where(cb.and(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station),
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("date")),date))
				);
		cq.select(root);
		TypedQuery<WeatherReport> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return 0;
		}
		return tq.getResultList().size();
	}


	public void create(WeatherReport weatherMeasure) {
		this.entityManager.persist(weatherMeasure);
		
	}
	
}

