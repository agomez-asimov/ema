package ar.asimov.acumar.ema.model.converter;

import java.sql.Time;
import java.time.LocalTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, Time> {

	@Override
	public Time convertToDatabaseColumn(LocalTime localTime) {
		if(null == localTime) return null;
		return Time.valueOf(localTime);
	}

	@Override
	public LocalTime convertToEntityAttribute(Time time) {
		if(null == time) return null;
		return time.toLocalTime();
	}

	
	
	
}
