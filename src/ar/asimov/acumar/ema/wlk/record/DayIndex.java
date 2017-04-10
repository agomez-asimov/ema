package ar.asimov.acumar.ema.wlk.record;

import java.nio.ByteOrder;

import javolution.io.Struct;

public class DayIndex extends Struct {
	
	/**
	 * Includes any daily summary records
	 */
	public final Signed16 recordsInDay = new Signed16();
	
	/**
	 * The index (starting at 0) of the first daily summary records
	 */
	public final Signed32 startPosition = new Signed32();

	@Override
	public boolean isPacked() {
		return true;
	}

	@Override
	public ByteOrder byteOrder() {
		return ByteOrder.LITTLE_ENDIAN;
	}
	
	


}
