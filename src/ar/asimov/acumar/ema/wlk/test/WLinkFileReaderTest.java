package ar.asimov.acumar.ema.wlk.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.YearMonth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import ar.asimov.acumar.ema.wlk.data.DailySummaryData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.reader.WLinkFileReader;

public class WLinkFileReaderTest {
	
	private WLinkFileReader reader;

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		try{
			if(this.reader == null){
				this.reader = new WLinkFileReader("C:/Program Files (x86)/WLink 01 La Boca/Ema01LaB/2015-02.wlk");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		this.reader.close();
		this.reader = null;
	}

	@Test
	public void testWeatherLinkFileReader() {
		System.out.println("Fecha: "+this.reader.getFilePeriod().toString());
		System.out.println("Provider: "+this.reader.getIdCode());
		System.out.println("Records: "+this.reader.getTotalRecords());
		assertTrue(this.reader!=null);
	}

	@Test
	public void testReadDay() {
		try{
			YearMonth filePeriod = this.reader.getFilePeriod();
			for(int i = 1;i<=filePeriod.atEndOfMonth().getDayOfMonth();i++){
				if(!this.reader.isEmpty(i)){
					DailySummaryData summary = 	this.reader.readDay(i);
					System.out.println(summary);
				}else{
					System.out.println("No data available for "+filePeriod.atDay(i).toString());
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			fail("Se produjo un error "+e.getMessage());
		}
	}

	@Test
	public void testRead() {
		try{
			YearMonth filePeriod = this.reader.getFilePeriod();
			for(int i = 1;i<=filePeriod.atEndOfMonth().getDayOfMonth();i++){
				for(int j = 0;j<this.reader.recordsInDay(i);j++){
					DailyWeatherData data = this.reader.read(i, j);
					System.out.println(data);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			fail("Se produjo un error "+e.getMessage());
		}
	}

}
