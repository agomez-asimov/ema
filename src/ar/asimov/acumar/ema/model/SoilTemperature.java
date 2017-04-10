package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: SoilTemperature
 *
 */
@Entity
@Table(name="ta_ams_weather_soil_temperature")
public class SoilTemperature extends ListParameterValue implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	public SoilTemperature() {
		super();
	}
   
}
