package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: SoilMoisture
 *
 */
@Entity
@Table(name="ta_ams_weather_soil_moisture")
public class SoilMoisture extends ListParameterValue implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public SoilMoisture() {
		super();
	}
  
}
