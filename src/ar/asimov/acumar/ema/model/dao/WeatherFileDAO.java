package ar.asimov.acumar.ema.model.dao;

import java.time.YearMonth;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherFile;
import ar.asimov.acumar.ema.model.WeatherSummary;

public class WeatherFileDAO {
	
	private final EntityManager entityManager;
	
	
	public WeatherFileDAO(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	
	public WeatherFile fetch(Station station,YearMonth period){
		WeatherFile.PrimaryKey pk = new WeatherFile.PrimaryKey();
		pk.setStation(station.getId());
		pk.setPeriod(period);
		return this.entityManager.find(WeatherFile.class, pk);
	}
	
	public void create(WeatherFile weatherFile){
		this.entityManager.persist(weatherFile);
	}
	
	public void update(WeatherFile weatherFile){
		this.entityManager.merge(weatherFile);
	}
	
	public WeatherFile fetchLast(Station station){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<WeatherFile> cq = cb.createQuery(WeatherFile.class);
		Root<WeatherFile> root = cq.from(WeatherFile.class);
		EntityType<WeatherFile> WeatherSummary_ = this.entityManager.getMetamodel().entity(WeatherFile.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherSummary_.getSingularAttribute("period"))));
		TypedQuery<WeatherFile> tq = this.entityManager.createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}

}
