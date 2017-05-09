package ar.asimov.acumar.ema.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ar.asimov.acumar.ema.model.ProcessInformation;

public class ProcessInformationDAO extends DAO<ProcessInformation>{
	
	
	public ProcessInformationDAO(EntityManager entityManager) {
		super(entityManager);
	}
	
	public List<ProcessInformation> fetchAll(){
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ProcessInformation> cq = cb.createQuery(ProcessInformation.class);
		Root<ProcessInformation> root = cq.from(ProcessInformation.class);
		cq.select(root);
		TypedQuery<ProcessInformation> tq = this.getEntityManager().createQuery(cq);
		return tq.getResultList();
	}
	@Override
	public void localCreate(ProcessInformation processInformation){
		this.getEntityManager().persist(processInformation);
	}
	
	public void localUpdate(ProcessInformation processInformation){
			this.getEntityManager().merge(processInformation);
	}
	
	@Override
	public ProcessInformation fetch(Object...id){
		if(id.length != 1) throw new IllegalArgumentException("ProcessInformationDAO.fetch() expects exactly 1 parameter");
		if(!(id[0] instanceof Long)) throw new IllegalArgumentException("ProcessInformationDAO.fetch() requires parameter to be of type Long");
		return this.getEntityManager().find(ProcessInformation.class, id);
	}

	@Override
	protected void localDelete(ProcessInformation entity) {
		throw new UnsupportedOperationException("ProcessInformationDAO.delete() is not implemented yet");
	}

	@Override
	protected String printEntityId(ProcessInformation entity) {
		return "["+entity.getStation().getId()+", "+entity.getProcessId().toString()+"]";
	}

}
