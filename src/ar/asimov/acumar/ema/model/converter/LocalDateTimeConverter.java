package ar.asimov.acumar.ema.model.converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime arg0) {
		return (null == arg0)?null:Timestamp.valueOf(arg0);
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp arg0) {
		return (null==arg0)?null:arg0.toLocalDateTime();
	}

}
