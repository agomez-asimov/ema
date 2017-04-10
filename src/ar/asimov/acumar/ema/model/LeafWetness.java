package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: LeafWetness
 *
 */
@Entity
@Table(name="ta_ams_weather_leaf_wetness")
public class LeafWetness extends ListParameterValue implements Serializable {

	
	private static final long serialVersionUID = 1L;


	public LeafWetness() {
		super();
	}

   
}
