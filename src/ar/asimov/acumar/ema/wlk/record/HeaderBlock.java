package ar.asimov.acumar.ema.wlk.record;

import java.nio.ByteOrder;

import javolution.io.Struct;

public class HeaderBlock extends Struct{
	
	/**
	 * The first 16 bytes are used to identify a weather database file
	 * and to identify diffrent file formats (used for converting 
	 * older database files).
	 */
	public final UTF8String idCode = new UTF8String(16);
	
	/**
	 * The total number of records for this month. The number depends
	 * on the selected archive interval recording. It also contains
	 * two additional records (daily summary) on a day. The size is 4 bytes.
	 */
	public final Signed32 totalRecords = new Signed32();
	
	/**
	 * An array of DayIndex, which contains index record for each day.
	 * Index 0 is not used (i.e. the 1'st is at index 1, not index 0).
	 * The size of one object is 6B	.
	 */
	public final DayIndex[] dayIndex = array(new DayIndex[32]);

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
