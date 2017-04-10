package ar.asimov.acumar.ema.wlk.reader;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import ar.asimov.acumar.ema.wlk.data.DailySummaryData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;
import ar.asimov.acumar.ema.wlk.record.DailySummary1;
import ar.asimov.acumar.ema.wlk.record.DailySummary2;
import ar.asimov.acumar.ema.wlk.record.DayIndex;
import ar.asimov.acumar.ema.wlk.record.HeaderBlock;
import ar.asimov.acumar.ema.wlk.record.WeatherDataRecord;

public final class WLinkFileReader{ 
	
	private final int HEADER_RECORD_SIZE = 212;
	private final int DATA_RECORD_SIZE = 88;
	private final int SUMMARY1_DATA_TYPE = 2;
	private final int SUMMARY2_DATA_TYPE = 3;
	private final int WEATHER_DATA_TYPE = 1;
	
	private final File file;
	private final YearMonth fileDate;
	private final HeaderBlock header;

	private final SeekableByteChannel byteChannel;
	
	private final Map<Integer,SoftReference<DailySummaryData>> dailySummaryCache;
	private final Map<Integer,Map<Integer,SoftReference<DailyWeatherData>>> dailyWeatherDataCache;
	
	public WLinkFileReader(Path path) throws IOException{
		this(path.toFile());
	}
	
	public WLinkFileReader(File file) throws IOException{
		this.file = file;
		this.byteChannel = Files.newByteChannel(this.file.toPath(),StandardOpenOption.READ);
		this.dailySummaryCache = new LinkedHashMap<>();
		this.dailyWeatherDataCache = new LinkedHashMap<>();
		this.header = readHeader(this.byteChannel);
		this.fileDate = YearMonth.parse(FilenameUtils.removeExtension(this.file.getName()),DateTimeFormatter.ofPattern("y-M"));
	}

	public WLinkFileReader(String file) throws IOException {
		this(new File(file));
	}
	
		
	private HeaderBlock readHeader(final SeekableByteChannel chanel) throws IOException{
		HeaderBlock header = new HeaderBlock();
		ByteBuffer buffer = ByteBuffer.allocate(HEADER_RECORD_SIZE);
		if(chanel.position(0).read(buffer)>0){
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			header.setByteBuffer(buffer, 0);
		}
		return header;
	}

	private DailySummaryData readSummary(int dayIndex,final SeekableByteChannel channel) throws IOException{
		if(this.dailySummaryCache.containsKey(dayIndex)){
			DailySummaryData data = this.dailySummaryCache.get(dayIndex).get();
			if(null != data) return data;
		}
		DayIndex day = this.header.dayIndex[dayIndex];
		DailySummary1 summary1 = new DailySummary1();
		DailySummary2 summary2 = new DailySummary2();
		if(day.recordsInDay.get() > 0){
			int streamOffset = (day.startPosition.get() * DATA_RECORD_SIZE) + HEADER_RECORD_SIZE;
			ByteBuffer buffer = ByteBuffer.allocate(DATA_RECORD_SIZE*2);
			if(channel.position(streamOffset).read(buffer)>0){
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				summary1.setByteBuffer(buffer, 0);
				summary2.setByteBuffer(buffer,DATA_RECORD_SIZE);
				if(summary1.dataType.get() == SUMMARY1_DATA_TYPE  && summary2.dataType.get() == SUMMARY2_DATA_TYPE){
					DailySummaryData summary = DailySummaryData.from(this.fileDate.atDay(dayIndex),summary1,summary2);
					this.dailySummaryCache.put(dayIndex, new SoftReference<>(summary));
					return summary;
				}
			}
		}
		return null;
	}
	
	private DailyWeatherData readWeather(int dayIndex,int index,final SeekableByteChannel channel)throws IOException{
		if(this.dailyWeatherDataCache.containsKey(dayIndex) && this.dailyWeatherDataCache.get(dayIndex).containsKey(index)){
			DailyWeatherData data = this.dailyWeatherDataCache.get(dayIndex).get(index).get();
			if(null != data) return data;
		}
		DayIndex day = this.header.dayIndex[dayIndex];
		WeatherDataRecord record = new WeatherDataRecord();
		if(day.recordsInDay.get() > 0){
			int streamOffset = (int)(day.startPosition.get()+2+index)* DATA_RECORD_SIZE + HEADER_RECORD_SIZE;
			ByteBuffer buffer = ByteBuffer.allocate(DATA_RECORD_SIZE);
			if(channel.position(streamOffset).read(buffer)>0){
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				record.setByteBuffer(buffer, 0);
				if(record.dataType.get() == WEATHER_DATA_TYPE){
					DailyWeatherData data = DailyWeatherData.from(this.fileDate.atDay(dayIndex), record);
					if(!this.dailyWeatherDataCache.containsKey(dayIndex)){
						this.dailyWeatherDataCache.put(dayIndex, new LinkedHashMap<>());
					}
					this.dailyWeatherDataCache.get(dayIndex).put(index, new SoftReference<>(data));
					return data;
				}
			}
		}
		return null;
	}
	
	public YearMonth getFilePeriod(){
		return this.fileDate;
	}
	
	public boolean isEmpty(int dayIndex){
		return this.header.dayIndex[dayIndex].recordsInDay.get() == 0;
	}
	
	public String getIdCode(){
		return this.header.idCode.get();
	}
	
	public Integer getTotalRecords(){
		return this.header.totalRecords.get();
	}
	
	
	public void close() throws IOException {
		this.byteChannel.close();
	}
			
	public boolean contains(LocalDate date){
		return YearMonth.from(date).equals(this.fileDate) && this.header.dayIndex[date.getDayOfMonth()].recordsInDay.get() > 0;
	}
	
	public int recordsInDay(int dayIndex){
		int records = this.header.dayIndex[dayIndex].recordsInDay.get();
		return (records>=2)?records-2:0;
	}
	
	public int totalRecords(){
		return this.header.totalRecords.get();
	}

	public synchronized DailySummaryData readDay(int dayIndex) throws IOException{
		return this.readSummary(dayIndex,this.byteChannel);
	}
	
	public synchronized DailyWeatherData read(int dayIndex,int recordIndex) throws IOException{
		return this.readWeather(dayIndex, recordIndex,this.byteChannel);
	}
}
