package ar.asimov.acumar.ema.wlk.record;

import java.nio.ByteOrder;

import javolution.io.Struct;

public class WeatherDataRecord extends Struct {
	
	/**
	 * Safety check to make sure we are reading correct record type. For weather
	 * data record the dataType = 1.
	 */
	public final Signed8 dataType = new Signed8();
	/**
	 * Number of minutes in the archive interval.
	 */
	public final Signed8 archiveInterval = new Signed8();
	/**
	 * Icon associated with this record, plus Edit flags and data note
	 * association.
	 */
	public final Signed8 iconFlags = new Signed8();
	/**
	 * Transmitter ID.
	 */
	public final Signed8 moreFlags = new Signed8();
	/**
	 * Minutes past midnight of the end of the archive period.
	 */
	public final Signed16 packedTime = new Signed16();
	/**
	 * Current outside temperature in tenths of a degree F.
	 */
	public final Signed16 outsideTemp = new Signed16();
	/**
	 * Highest outside temperature in interval in tenths of a degree F.
	 */
	public final Signed16 hiOutsideTemp = new Signed16();
	/**
	 * Lowest outside temperature in interval in tenths of a degree F.
	 */
	public final Signed16 lowOutsideTemp = new Signed16();
	/**
	 * Current inside temperature in tenths of a degree F.
	 */
	public final Signed16 insideTemp = new Signed16();
	/**
	 * Current pressure in thousandths of an inch Hg.
	 */
	public final Signed16 barometer = new Signed16();
	/**
	 * Current outside humidity in tenths of a percent.
	 */
	public final Signed16 outsiedHum = new Signed16();
	/**
	 * Current inside humidity in tenths of a percent.
	 */
	public final Signed16 insideHum = new Signed16();
	/**
	 * Number of clicks + rain collector type code.
	 */
	public final Unsigned16 rain = new Unsigned16();
	/**
	 * Clicks per hour.
	 */
	public final Signed16 hiRainRate = new Signed16();
	/**
	 * Current wind speed in tenths of an MPH.
	 */
	public final Signed16 windSpeed = new Signed16();
	/**
	 * Current wind speed in tenths of an MPH.
	 */
	public final Signed16 hiWindSpeed = new Signed16();
	/**
	 * Direction code (0-15, 255).
	 */
	public final Unsigned8 windDirection = new Unsigned8();
	/**
	 * Direction code (0-15, 255).
	 */
	public final Unsigned8 hiWindDirection = new Unsigned8();
	/**
	 * Number of valid ISS packets containing wind data.
	 */
	public final Signed16 numWindSamples = new Signed16();
	/**
	 * Watts per meter squared.
	 */
	public final Signed16 solarRad = new Signed16();
	/**
	 * Watts per meter squared.
	 */
	public final Signed16 hiSolarRad = new Signed16();
	/**
	 * tenths of a UV index.
	 */
	public final Signed8 UV = new Signed8();
	/**
	 * tenths of a UV index.
	 */
	public final Signed8 hiUV = new Signed8();
	/**
	 * (whole degrees F) + 90
	 */
	public final Unsigned8[] leafTemp = array(new Unsigned8[4]);
	/**
	 * Used to calculate extra heating effects of the sun in THSW index.
	 */
	public final Signed16 extraRad = new Signed16();
	/**
	 * Reserved for future use.
	 */
	public final Signed16[] newSensors = array(new Signed16[6]);
	/**
	 * Forecast code during the archive interval.
	 */
	public final Unsigned8 forecast = new Unsigned8();
	/**
	 * In thousandths of an inch.
	 */
	public final Unsigned8 ET = new Unsigned8();
	/**
	 * (whole degrees F) + 90
	 */
	public final Unsigned8[] soilTemp = array(new Unsigned8[6]);

	/**
	 * Centibars of dryness.
	 */
	public final Unsigned8[] soilMoisture = array(new Unsigned8[6]);
	/**
	 * Leaf Wetness code (0-15, 255).
	 */
	public final Unsigned8[] leafWetness = array(new Unsigned8[4]);
	/**
	 * (whole degrees F) + 90
	 */
	public final Unsigned8[] extraTemp = array(new Unsigned8[7]);
	/**
	 * whole percent
	 */
	public final Unsigned8[] extraHum = array(new Unsigned8[7]);
	
	/**
	 * We set the byte order for this struct to LITTLE ENDIAN.
	 * http://javolution.org/target/site/apidocs/javolution/io/Struct.html#byteOrder%28%29
	 */
	@Override
	public ByteOrder byteOrder() {
		return ByteOrder.LITTLE_ENDIAN; 
	}
	
	/**
	 * WeatherDataRecord is packed. That means no bytes are added to make the
	 * fields on the correct address boundaries. The alignment is 1.
	 * http://javolution.org/target/site/apidocs/javolution/io/Struct.html#isPacked%28%29
	 */
	@Override
	public boolean isPacked() {
		return true;
	}
	
	
	

}
