package ar.asimov.acumar.ema.wlk.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ar.asimov.acumar.ema.wlk.record.DailySummary1;
import ar.asimov.acumar.ema.wlk.record.DailySummary2;
import ar.asimov.acumar.ema.wlk.record.DayIndex;
import ar.asimov.acumar.ema.wlk.record.HeaderBlock;
import ar.asimov.acumar.ema.wlk.record.WeatherDataRecord;

public class RecordsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		DailySummary1 summary1 = new DailySummary1();
		DailySummary2 summary2 = new DailySummary2();
		DayIndex dayIndex = new DayIndex();
		HeaderBlock headerBlock = new HeaderBlock();
		WeatherDataRecord weatherRecord = new WeatherDataRecord();
		System.out.println(summary1.getClass().getName()+": "+summary1.size());
		System.out.println(summary2.getClass().getName()+": "+summary2.size());
		System.out.println(dayIndex.getClass().getName()+": "+dayIndex.size());
		System.out.println(headerBlock.getClass().getName()+": "+headerBlock.size());
		System.out.println(weatherRecord.getClass().getName()+": "+weatherRecord.size());
	}
	
	@Test
	public void testMinMaxValues(){
		System.out.println("Integer.MIN_VALUE: "+Integer.MIN_VALUE);
		System.out.println("Integer.MAX_VALUE: "+Integer.MAX_VALUE);
		System.out.println("Integer.BYTES: "+Integer.BYTES);
		System.out.println("Short.MIN_VALUE: "+Short.MIN_VALUE);
		System.out.println("Short.MAX_VALUE: "+Short.MAX_VALUE);
		System.out.println("Short.BYTES: "+Short.BYTES);
		
	}

}
