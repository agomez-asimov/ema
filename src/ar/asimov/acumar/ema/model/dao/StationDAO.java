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
public class StationDAO {
	
	private final EntityManager entityManager;
	
	public StationDAO(EntityManager em){
		this.entityManager = em;
	}
	
	public Station fetch(String id){
		return this.entityManager.find(Station.class, id);
	}
	
	public List<Station> fetchAll(Boolean active){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Station> cq = cb.createQuery(Station.class);
		EntityType<Station> Station_ = this.entityManager.getMetamodel().entity(Station.class);
		Root<Station> root = cq.from(Station.class);
		cq.where(
				cb.equal(root.get(Station_.getSingularAttribute("active")),active)
				);
		cq.select(root);
		TypedQuery<Station> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public List<Station> fetchAll(){
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Station> cq = cb.createQuery(Station.class);
		Root<Station> root = cq.from(Station.class);
		cq.select(root);
		TypedQuery<Station> tq = this.entityManager.createQuery(cq);
		return tq.getResultList();
	}
	
	public void create(Station station){
		this.entityManager.persist(station);
	}
	
	public void update(Station station){
		this.entityManager.merge(station);
	}
		

}
