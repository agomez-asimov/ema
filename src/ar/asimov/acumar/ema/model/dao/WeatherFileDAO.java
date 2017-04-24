package ar.asimov.acumar.ema.model.dao;

import java.time.YearMonth;

import javax.persistence.EntityManager;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherFile;

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
		return null;
	}

}
