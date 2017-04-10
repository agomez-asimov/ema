package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ta_ams_weather_extra_humidity")
public class ExtraHumidity extends ListParameterValue implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExtraHumidity() {
	 super();
	}

}
