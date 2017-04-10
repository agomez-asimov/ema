package ar.asimov.acumar.ema.wlk.test;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.helper.EntityManagerHelper;

public class EntityManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		EntityManagerHelper.beginTransaction();
				EntityManager em = EntityManagerHelper.getEntityManager();
				Station station = em.find(Station.class, "EMA01LABOCA");
				System.out.println("Station: "+station.getId()+" - "+station.getName());
				System.out.println("DB Location: "+station.getDbPath() );
				EntityManagerHelper.commitTransaction();
				EntityManagerHelper.closeEntityManager();
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				EntityManagerHelper.beginTransaction();
				EntityManager em = EntityManagerHelper.getEntityManager();
				Station station = em.find(Station.class, "EMA02MORON");
				System.out.println("Station: "+station.getId()+" - "+station.getName());
				System.out.println("DB Location: "+station.getDbPath() );
				EntityManagerHelper.commitTransaction();
				EntityManagerHelper.closeEntityManager();
			}
		});
		t2.start();
		try{
			t2.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

}
