package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: Configuration
 *
 */
@Entity
@Table(name="ta_sys_configuration")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Configuration implements Serializable {

		
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="config_key")
	private String id;
	@Column(name="config_value")
	private String value;

	public Configuration() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
   
}
