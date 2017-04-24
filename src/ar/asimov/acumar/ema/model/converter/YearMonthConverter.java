package ar.asimov.acumar.ema.model.converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

	@Override
	public String convertToDatabaseColumn(YearMonth yearMonth) {
		if(null == yearMonth) return null;
		return yearMonth.format(DateTimeFormatter.ISO_DATE);
	}

	@Override
	public YearMonth convertToEntityAttribute(String yearMonth) {
		if(null == yearMonth) return null;
		return YearMonth.parse(yearMonth,DateTimeFormatter.ISO_DATE);
	}

	
	
	
}