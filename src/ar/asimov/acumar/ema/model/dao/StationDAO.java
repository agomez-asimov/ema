/**
 * 
 */
package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import ar.asimov.acumar.ema.model.Station;

/**
 * @author agomez
 *
 */
public class StationDAO extends DAO<Station>{
	
	
	public StationDAO(EntityManager em){
		super(em);
	}
	
	@Override
	public Station fetch(Object...id){
		if(id.length != 1) throw new IllegalArgumentException("StationDAO.fetch() expects exactly 1 parameter");
		if(!(id[0] instanceof String)) throw new IllegalArgumentException("StationDAO.fetch() expects the parameter to be of type String");
		String stringId = (String)id[0];
		return this.getEntityManager().find(Station.class, stringId);
	}
	
	public List<Station> fetchAll(Boolean active){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Station> cq = cb.createQuery(Station.class);
		EntityType<Station> Station_ = this.getEntityManager().getMetamodel().entity(Station.class);
		Root<Station> root = cq.from(Station.class);
		cq.where(
				cb.equal(root.get(Station_.getSingularAttribute("active")),active)
				);
		cq.select(root);
		TypedQuery<Station> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	
	public List<Station> fetchAll(){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Station> cq = cb.createQuery(Station.class);
		Root<Station> root = cq.from(Station.class);
		cq.select(root);
		TypedQuery<Station> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	
	@Override
	public void localCreate(Station station){
		this.getEntityManager().persist(station);
	}
	
	@Override
	public void localUpdate(Station station){
		this.getEntityManager().merge(station);
	}

	@Override
	protected void localDelete(Station entity) {
		throw new UnsupportedOperationException("StationDAO.delete() is not implemented yet");
	}

	@Override
	protected String printEntityId(Station entity) {
		return "["+entity.getId()+"]";
	}
		

}
