package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: NewSensor
 *
 */
@Entity
@Table(name="ta_ams_weather_new_sensor")
public class NewSensor extends ListParameterValue implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public NewSensor() {
		super();
	}
	
  
}
