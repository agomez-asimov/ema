package ar.asimov.acumar.ema.model.helper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

public class EntityManagerHelper {
	
	private static final EntityManagerFactory emf;
	private static final ThreadLocal<EntityManager> threadLocal;
	
	static{
		emf = Persistence.createEntityManagerFactory("EMA");
		threadLocal = new ThreadLocal<>();
	}
	
	public static EntityManager getEntityManager(){
		EntityManager em = threadLocal.get();
		if(null == em){
			em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			threadLocal.set(em);
		}
		return em;
	}
	
	public static void closeEntityManager(){
		EntityManager em = threadLocal.get();
		if(null != em){
			em.close();
			threadLocal.set(null);
		}
	}
	
	public static void beginTransaction(){
		getEntityManager().getTransaction().begin();
	}
	
	public static void commitTransaction(){
		getEntityManager().getTransaction().commit();
	}
	
	public static void rollbackTransaction(){
		getEntityManager().getTransaction().rollback();
	}

}
