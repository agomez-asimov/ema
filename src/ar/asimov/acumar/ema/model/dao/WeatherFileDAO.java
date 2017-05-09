package ar.asimov.acumar.ema.model.dao;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherFile;

public class WeatherFileDAO extends DAO<WeatherFile>{
	
	
	public WeatherFileDAO(EntityManager entityManager){
		super(entityManager);
	}

	@Override
	public WeatherFile fetch(Object...id){
		if(id.length != 2) throw new IllegalArgumentException("WeatherFileDAO.fetch() requires exactly 2 parameters");
		if(!(id[0] instanceof String)) throw new IllegalArgumentException("WeatherFileDAO.fetch() expects first parameter to be of type String");
		if(!(id[1] instanceof YearMonth)) throw new IllegalArgumentException("WeatherfileDAO.fetch() expects second parameter to be of type YearMonth");
		WeatherFile.PrimaryKey pk = new WeatherFile.PrimaryKey();
		pk.setStation((String)id[0]);
		pk.setPeriod((YearMonth)id[1]);
		return this.getEntityManager().find(WeatherFile.class, pk);
	}
	
	
	@Override
	protected void localCreate(WeatherFile weatherFile){
		this.getEntityManager().persist(weatherFile);
	}
	
	@Override
	protected void localUpdate(WeatherFile weatherFile){
		this.getEntityManager().merge(weatherFile);
	}
	
	public WeatherFile fetchLast(Station station){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherFile> cq = cb.createQuery(WeatherFile.class);
		Root<WeatherFile> root = cq.from(WeatherFile.class);
		EntityType<WeatherFile> WeatherSummary_ = this.getEntityManager().getMetamodel().entity(WeatherFile.class);
		cq.where(
				cb.equal(root.get(WeatherSummary_.getSingularAttribute("station")),station)
				);
		cq.select(root);
		cq.orderBy(cb.desc(root.get(WeatherSummary_.getSingularAttribute("period"))));
		TypedQuery<WeatherFile> tq = this.getEntityManager().createQuery(cq);
		if(null == tq.getResultList() || tq.getResultList().isEmpty()){
			return null;
		}
		return tq.getResultList().get(0);
	}

	@Override
	public List<WeatherFile> fetchAll() {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<WeatherFile> cq = cb.createQuery(WeatherFile.class);
		Root<WeatherFile> root = cq.from(WeatherFile.class);
		cq.select(root);
		TypedQuery<WeatherFile> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}

	@Override
	protected void localDelete(WeatherFile entity) {
		throw new UnsupportedOperationException("WeatherFileDAO.delete() is not implemented yet");	
	}

	@Override
	protected String printEntityId(WeatherFile entity) {
		return "["+entity.getStation().getId()+", "+entity.getPeriod().format(DateTimeFormatter.ofPattern("Y-MM"))+"]";
	}

}
