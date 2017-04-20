package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherDailyReport;

public class WeatherDailyReportDAO {

	private final EntityManager entityManager;
	
	
	public WeatherDailyReportDAO(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	
	public WeatherDailyReport fetch(WeatherDailyReport.PrimaryKey id){
		return this.entityManager.find(WeatherDailyReport.class, id);
	}
	
	public List<WeatherDailyReport> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherDailyReport> cq = cb.createQuery(WeatherDailyReport.class);
		Root<WeatherDailyReport> root = cq.from(WeatherDailyReport.class);
		cq.select(root);
		TypedQuery<WeatherDailyReport> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherDailyReport> fetchAll(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherDailyReport> cq = cb.createQuery(WeatherDailyReport.class);
		Root<WeatherDailyReport> root = cq.from(WeatherDailyReport.class);
		EntityType<WeatherDailyReport> WeatherSummary_ = this.entityManager.getMetamodel().entity(WeatherDailyReport.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherDailyReport> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public WeatherDailyReport fetchLast(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherDailyReport> cq = cb.createQuery(WeatherDailyReport.class);
		Root<WeatherDailyReport> root = cq.from(WeatherDailyReport.class);
		EntityType<WeatherDailyReport> WeatherSummary_ = this.entityManager.getMetamodel().entity(WeatherDailyReport.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherSummary_.getSingularAttribute("date"))));
		TypedQuery<WeatherDailyReport> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	
	
	public void create(WeatherDailyReport report){
		this.entityManager.persist(report);
	}
	
	public void update(WeatherDailyReport report){
		this.entityManager.merge(report);
	}
}
