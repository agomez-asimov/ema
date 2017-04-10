package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: LeafTemperature
 *
 */
@Entity
@Table(name="ta_ams_weather_leaf_temperature")
public class LeafTemperature extends ListParameterValue implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public LeafTemperature() {
		super();
	}

}
