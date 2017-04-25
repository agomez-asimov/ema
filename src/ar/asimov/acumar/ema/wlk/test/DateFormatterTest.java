package ar.asimov.acumar.ema.wlk.test;

import static org.junit.Assert.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class DateFormatterTest {

	@Test
	public void test() {
		System.out.println(YearMonth.now().format(DateTimeFormatter.ofPattern("yMM")));
	}

}
