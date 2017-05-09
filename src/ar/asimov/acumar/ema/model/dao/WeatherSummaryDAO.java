package ar.asimov.acumar.ema.model.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherSummary;

public class WeatherSummaryDAO extends DAO<WeatherSummary>{

	
	public WeatherSummaryDAO(EntityManager entityManager){
		super(entityManager);
	}
	
	@Override
	public WeatherSummary fetch(Object...id){
		if(id.length != 2) throw new IllegalArgumentException("WeatherSummaryDAO.fetch() requires 2 parameters");
		if(!(id[0] instanceof String)) throw new IllegalArgumentException("WeatherSummaryDAO.fetch() requires first parameter to be of type String");
		if(!(id[1] instanceof LocalDate)) throw new IllegalArgumentException("WeatherSummaryDAO.fetch() requires second parameter to be of type LocalDate");
		String stationId = (String)id[0];
		LocalDate date = (LocalDate)id[1];
		WeatherSummary.PrimaryKey pk = new WeatherSummary.PrimaryKey();
		pk.setStation(stationId);
		pk.setDate(date);
		return this.getEntityManager().find(WeatherSummary.class, pk);
	}
	
	@Override
	public List<WeatherSummary> fetchAll(){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherSummary> cq = cb.createQuery(WeatherSummary.class);
		Root<WeatherSummary> root = cq.from(WeatherSummary.class);
		cq.select(root);
		TypedQuery<WeatherSummary> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherSummary> fetchAll(Station station){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherSummary> cq = cb.createQuery(WeatherSummary.class);
		Root<WeatherSummary> root = cq.from(WeatherSummary.class);
		EntityType<WeatherSummary> WeatherSummary_ = this.getEntityManager().getMetamodel().entity(WeatherSummary.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherSummary> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	
	public WeatherSummary fetchLast(Station station){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherSummary> cq = cb.createQuery(WeatherSummary.class);
		Root<WeatherSummary> root = cq.from(WeatherSummary.class);
		EntityType<WeatherSummary> WeatherSummary_ = this.getEntityManager().getMetamodel().entity(WeatherSummary.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherSummary_.getSingularAttribute("date"))));
		TypedQuery<WeatherSummary> tq = this.getEntityManager().createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	
	@Override
	public void localCreate(WeatherSummary report){
		this.getEntityManager().persist(report);
	}
	
	@Override
	public void localUpdate(WeatherSummary report){
		this.getEntityManager().merge(report);
	}

	@Override
	protected void localDelete(WeatherSummary entity) {
		throw new UnsupportedOperationException("WeatherSummaryDAO.delete() is not implemented yet");
	}

	@Override
	protected String printEntityId(WeatherSummary entity) {
		return "["+entity.getStation().getId()+", "+entity.getDate().format(DateTimeFormatter.ofPattern("dd/MM/Y"));
	}
}
