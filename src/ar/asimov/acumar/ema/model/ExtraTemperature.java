package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: ExtraTemperature
 *
 */
@Entity
@Table(name="ta_ams_weather_extra_temperature")
public class ExtraTemperature extends ListParameterValue implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	public ExtraTemperature() {
		super();
	}

	
}
