package ar.asimov.acumar.ema.model.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherData;

public class WeatherDataDAO extends DAO<WeatherData>{
	

	public WeatherDataDAO(EntityManager entityManager) {
		super(entityManager);
	}
	
	@Override
	public WeatherData fetch(Object...id){
		if(id.length != 3) throw new IllegalArgumentException("WeatherDataDAO.fetch() requires exactly 3 parameters");
		if(!(id[0] instanceof String)) throw new IllegalArgumentException("WeatherDataDAO.fetch() expects first parameter to be of type String");
		if(!(id[1] instanceof LocalDate)) throw new IllegalArgumentException("WeatherDataDAO.fetch() expects second parameter to be of type LocalDate");
		if(!(id[2] instanceof LocalTime)) throw new IllegalArgumentException("WeatherDataDAO.fetch() expects third parameter to be of type LocalTime");
		WeatherData.PrimaryKey localId = new WeatherData.PrimaryKey();
		localId.setStation((String)id[0]);
		localId.setDate((LocalDate)id[1]);
		localId.setStartTime((LocalTime)id[2]);
		return this.getEntityManager().find(WeatherData.class, localId);
	}
	
	public WeatherData fetch(Station station,LocalDate date,LocalTime time){
		WeatherData.PrimaryKey id = new WeatherData.PrimaryKey();
		id.setStation(station.getId());
		id.setDate(date);
		id.setStartTime(time);
		return this.getEntityManager().find(WeatherData.class, id);
	}
	
	public List<WeatherData> fetchAll(){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		cq.select(root);
		TypedQuery<WeatherData> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	
	public List<WeatherData> fetchAll(Station station){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		EntityType<WeatherData> WeatherMeasure_ = this.getEntityManager().getMetamodel().entity(WeatherData.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		TypedQuery<WeatherData> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	
	public WeatherData fetchLast(Station station){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		EntityType<WeatherData> WeatherMeasure_ = this.getEntityManager().getMetamodel().entity(WeatherData.class);
		cq.where(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherMeasure_.getSingularAttribute("date"))),cb.desc(root.get(WeatherMeasure_.getSingularAttribute("time"))));
		TypedQuery<WeatherData> tq = this.getEntityManager().createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}
	
	public Integer count(Station station,LocalDate date){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherData> cq = cb.createQuery(WeatherData.class);
		Root<WeatherData> root = cq.from(WeatherData.class);
		EntityType<WeatherData> WeatherMeasure_ = this.getEntityManager().getMetamodel().entity(WeatherData.class);
		cq.where(cb.and(
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("station")),station),
				cb.equal(root.get(WeatherMeasure_.getSingularAttribute("date")),date))
				);
		cq.select(root);
		TypedQuery<WeatherData> tq = this.getEntityManager().createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return 0;
		}
		return tq.getResultList().size();
	}

	@Override
	public void localCreate(WeatherData weatherMeasure){
		this.getEntityManager().persist(weatherMeasure);
	}
	
	@Override
	public void localUpdate(WeatherData weatherMeasure){
		this.getEntityManager().merge(weatherMeasure);

	}

	@Override
	protected void localDelete(WeatherData entity) {
		throw new UnsupportedOperationException("WeatherDataDAO.delete() is not implemented yet");
		
	}

	@Override
	protected String printEntityId(WeatherData entity) {
		return "["+entity.getStation().getId()+", "+entity.getDate().format(DateTimeFormatter.ofPattern("dd/MM/Y"))+", "+entity.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
	}
	
}

