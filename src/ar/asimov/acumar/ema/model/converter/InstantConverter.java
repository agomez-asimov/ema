package ar.asimov.acumar.ema.model.converter;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.AttributeConverter;

public class InstantConverter implements AttributeConverter<Instant, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(Instant arg0) {
		return (null == arg0)?null:Timestamp.from(arg0);
	}

	@Override
	public Instant convertToEntityAttribute(Timestamp arg0) {
		return (null == arg0)?null:arg0.toInstant();
	}
	
	

}
