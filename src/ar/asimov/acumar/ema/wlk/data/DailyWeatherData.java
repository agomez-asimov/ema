package ar.asimov.acumar.ema.wlk.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import ar.asimov.acumar.ema.wlk.record.WeatherDataRecord;
import javolution.io.Struct.Unsigned16;

public class DailyWeatherData implements Serializable {

	public static final DailyWeatherData from(LocalDate date, WeatherDataRecord record) {
		if (null == date)
			throw new IllegalArgumentException("Date can't be null for DailyWeatherData.from()");
		if (null == record)
			throw new IllegalArgumentException("WeatherDataRecord can't be null for DailyWeatherData.from()");
		DailyWeatherData data = new DailyWeatherData();
		data.setDate(date);
		data.setStartTime(unpackTime((short) (record.packedTime.get() - record.archiveInterval.get())));
		data.setEndTime(unpackTime(record.packedTime.get()));
		data.setIconFlags(record.iconFlags.get());
		data.setMoreFlags(record.moreFlags.get());
		data.setOutsideTemperature((record.outsideTemp.get() / 1e1));
		data.setMaxOutsideTemperature((record.hiOutsideTemp.get() / 1e1));
		data.setMinOutsideTemperature((record.lowOutsideTemp.get() / 1e1));
		data.setInsideTemperature((record.insideTemp.get() / 1e1));
		data.setPressure((record.insideTemp.get() / 1e3));
		data.setOutsideHumidity((record.outsiedHum.get() / 1e1));
		data.setInsideHumidity((record.insideHum.get() / 1e1));
		data.setRainCollectorType(unpackRainCollector(record.rain));
		data.setPrecipitation(unpackRainClicks(record.rain));
		data.setMaxPrecipitationRate(record.hiRainRate.get());
		data.setWindSpeed((record.windSpeed.get() / 1e1));
		data.setWindDirection((record.windDirection.get() == 255) ? null : record.windDirection.get());
		data.setMaxWindSpeed((record.hiWindSpeed.get() / 1e1));
		data.setMaxWindDirection((255 == record.hiWindDirection.get()) ? null : record.hiWindDirection.get());
		data.setWindSamplesNumber(record.numWindSamples.get());
		data.setSolarRadiation((Short.MIN_VALUE == record.solarRad.get()) ? null : record.solarRad.get());
		data.setMaxSolarRadiation((Short.MIN_VALUE == record.hiSolarRad.get()) ? null : record.hiSolarRad.get());
		data.setUVIndex((255 != record.UV.get()) ? null : (record.UV.get() / 1e1));
		data.setMaxUVIndex((255 != record.hiUV.get()) ? null : (record.hiUV.get() / 1e1));
		for (int i = 0; i < record.newSensors.length; i++) {
			if (Short.MIN_VALUE != record.newSensors[i].get()) {
				data.addNewSensor(record.newSensors[i].get());
			}
		}
		for (int i = 0; i < record.leafTemp.length; i++) {
			if (0 > record.leafTemp[i].get()) {
				data.addLeafTemperature((short) (record.leafTemp[i].get() - 90));
			}
		}
		data.setExtraRadiation((Short.MIN_VALUE == record.extraRad.get()) ? null : record.extraRad.get());
		data.setET((record.ET.get() / 1e3));
		for (int i = 0; i < record.soilTemp.length; i++) {
			if (255 != record.soilTemp[i].get()) {
				data.addSoilTemperature((short) (record.soilTemp[i].get() - 90));
			}
		}
		for (int i = 0; i < record.soilMoisture.length; i++) {
			if (255 != record.soilMoisture[i].get()) {
				data.addSoilMoisture(record.soilMoisture[i].get());
			}
		}
		for (int i = 0; i < record.leafWetness.length; i++) {
			if (255 != record.leafWetness[i].get()) {
				data.addLeafWetness(record.leafWetness[i].get());
			}
		}
		for (int i = 0; i < record.extraTemp.length; i++) {
			if (255 != record.extraTemp[i].get()) {
				data.addExtraTemperature((short) (record.extraTemp[i].get() - 90));
			}
		}
		for (int i = 0; i < record.extraHum.length; i++) {
			if (255 != record.extraHum[i].get()) {
				data.addExtraHumidity(record.extraHum[i].get());
			}
		}
		return data;
	}

	private static final LocalTime unpackTime(Short value) {
		int hourOfDay = (int) Math.floor(value / 60);
		int minutesOfHour = (short) value % 60;
		if (hourOfDay == 24)
			hourOfDay = 0;
		LocalTime time = LocalTime.of(hourOfDay, minutesOfHour);
		return time;
	}

	private static final Double unpackRainCollector(Unsigned16 rain) {
		switch (rain.get() & 0xF000) {
		case 0x0000:
			return 0.1;
		case 0x1000:
			return 0.01;
		case 0x2000:
			return 0.2;
		case 0x3000:
			return 1.0;
		case 0x6000:
			return 0.1;
		default:
			return null;
		}
	}

	private static final Short unpackRainClicks(Unsigned16 rain) {
		return (short) (rain.get() & 0x0FFF);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;

	private Byte iconFlags;
	private Byte moreFlags;

	private Double outsideTemperature;
	private Double maxOutsideTemperature;
	private Double minOutsideTemperature;

	private Double insideTemperature;

	private Double pressure;

	private Double outsideHumidity;
	private Double insideHumidity;

	private Short precipitation;
	private Double rainCollectorType;
	private Short maxPrecipitationRate;

	private Double windSpeed;
	private Double maxWindSpeed;
	private Short windDirection;
	private Short maxWindDirection;
	private Short windSamplesNumber;

	private Short solarRadiation;
	private Short maxSolarRadiation;

	private Double UVIndex;
	private Double maxUVIndex;

	private List<Short> leafTemperature;

	private Short extraRadiation;

	private List<Short> newSensors;

	private Byte forecast;

	private Double ET;

	private List<Short> soilTemperature;

	private List<Short> soilMoisture;

	private List<Short> leafWetness;

	private List<Short> extraTemperature;

	private List<Short> extraHumidity;

	public DailyWeatherData() {
		this.leafTemperature = new ArrayList<>();
		this.newSensors = new ArrayList<>();
		this.soilTemperature = new ArrayList<>();
		this.soilMoisture = new ArrayList<>();
		this.leafWetness = new ArrayList<>();
		this.extraTemperature = new ArrayList<>();
		this.extraHumidity = new ArrayList<>();
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public Double getOutsideTemperature() {
		return outsideTemperature;
	}

	public void setOutsideTemperature(Double outsideTemperature) {
		this.outsideTemperature = outsideTemperature;
	}

	public Double getMaxOutsideTemperature() {
		return maxOutsideTemperature;
	}

	public void setMaxOutsideTemperature(Double maxOutsideTemperature) {
		this.maxOutsideTemperature = maxOutsideTemperature;
	}

	public Double getMinOutsideTemperature() {
		return minOutsideTemperature;
	}

	public void setMinOutsideTemperature(Double minOutsideTemperature) {
		this.minOutsideTemperature = minOutsideTemperature;
	}

	public Double getInsideTemperature() {
		return insideTemperature;
	}

	public void setInsideTemperature(Double insideTemperature) {
		this.insideTemperature = insideTemperature;
	}

	public Double getPressure() {
		return pressure;
	}

	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	public Double getOutsideHumidity() {
		return outsideHumidity;
	}

	public void setOutsideHumidity(Double outsideHumidity) {
		this.outsideHumidity = outsideHumidity;
	}

	public Double getInsideHumidity() {
		return insideHumidity;
	}

	public void setInsideHumidity(Double insideHumidity) {
		this.insideHumidity = insideHumidity;
	}

	public Short getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(Short precipitation) {
		this.precipitation = precipitation;
	}

	public Short getMaxPrecipitationRate() {
		return maxPrecipitationRate;
	}

	public void setMaxPrecipitationRate(Short maxPrecipitationRate) {
		this.maxPrecipitationRate = maxPrecipitationRate;
	}

	public Double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(Double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public Double getMaxWindSpeed() {
		return maxWindSpeed;
	}

	public void setMaxWindSpeed(Double maxWindSpeed) {
		this.maxWindSpeed = maxWindSpeed;
	}

	public Short getWindSamplesNumber() {
		return windSamplesNumber;
	}

	public void setWindSamplesNumber(Short windSamplesNumber) {
		this.windSamplesNumber = windSamplesNumber;
	}

	public Short getSolarRadiation() {
		return solarRadiation;
	}

	public void setSolarRadiation(Short s) {
		this.solarRadiation = s;
	}

	public Short getMaxSolarRadiation() {
		return maxSolarRadiation;
	}

	public void setMaxSolarRadiation(Short maxSolarRadiation) {
		this.maxSolarRadiation = maxSolarRadiation;
	}

	public Double getUVIndex() {
		return UVIndex;
	}

	public void setUVIndex(Double uVIndex) {
		UVIndex = uVIndex;
	}

	public Double getMaxUVIndex() {
		return maxUVIndex;
	}

	public void setMaxUVIndex(Double maxUVIndex) {
		this.maxUVIndex = maxUVIndex;
	}

	public Short getExtraRadiation() {
		return extraRadiation;
	}

	public void setExtraRadiation(Short extraRadiation) {
		this.extraRadiation = extraRadiation;
	}

	public Byte getForecast() {
		return forecast;
	}

	public void setForecast(Byte forecast) {
		this.forecast = forecast;
	}

	public Double getET() {
		return ET;
	}

	public void setET(Double eT) {
		ET = eT;
	}

	public Byte getIconFlags() {
		return iconFlags;
	}

	public void setIconFlags(Byte iconFlags) {
		this.iconFlags = iconFlags;
	}

	public Double getRainCollectorType() {
		return rainCollectorType;
	}

	public void setRainCollectorType(Double rainCollectorType) {
		this.rainCollectorType = rainCollectorType;
	}

	public Short getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(Short windDirection) {
		this.windDirection = windDirection;
	}

	public Short getMaxWindDirection() {
		return maxWindDirection;
	}

	public void setMaxWindDirection(Short maxWindDirection) {
		this.maxWindDirection = maxWindDirection;
	}

	public Byte getMoreFlags() {
		return moreFlags;
	}

	public void setMoreFlags(Byte moreFlags) {
		this.moreFlags = moreFlags;
	}

	public Short getLeafTemperature(int index) {
		return this.leafTemperature.get(index);
	}

	public void addLeafTemperature(Short value) {
		this.leafTemperature.add(value);
	}

	public void addNewSensor(Short sensor) {
		this.newSensors.add(sensor);
	}

	public Short getNewSensor(int index) {
		return this.newSensors.get(index);
	}

	public Short getSoilTemperature(int index) {
		return this.soilTemperature.get(index);
	}

	public void addSoilTemperature(Short value) {
		this.soilTemperature.add(value);
	}

	public Short getSoilMoisture(int index) {
		return this.soilMoisture.get(index);
	}

	public void addSoilMoisture(Short value) {
		this.soilMoisture.add(value);
	}

	public Short getLeafWetness(int index) {
		return this.leafWetness.get(index);
	}

	public void addLeafWetness(Short value) {
		this.leafWetness.add(value);
	}

	public Short getExtraTemperature(int index) {
		return this.extraTemperature.get(index);
	}

	public void addExtraTemperature(Short value) {
		this.extraTemperature.add(value);
	}

	public Short getExtraHumidity(int index) {
		return this.extraHumidity.get(index);
	}

	public void addExtraHumidity(Short value) {
		this.extraHumidity.add(value);
	}

	public List<Short> getLeafTemperature() {
		return this.leafTemperature;
	}

	public void setLeafTemperature(List<Short> leafTemperature) {
		this.leafTemperature = leafTemperature;
	}

	public List<Short> getNewSensors() {
		return newSensors;
	}

	public void setNewSensors(List<Short> newSensors) {
		this.newSensors = newSensors;
	}

	public List<Short> getSoilTemperature() {
		return soilTemperature;
	}

	public void setSoilTemperature(List<Short> soilTemperature) {
		this.soilTemperature = soilTemperature;
	}

	public List<Short> getSoilMoisture() {
		return soilMoisture;
	}

	public void setSoilMoisture(List<Short> soilMoisture) {
		this.soilMoisture = soilMoisture;
	}

	public List<Short> getLeafWetness() {
		return leafWetness;
	}

	public void setLeafWetness(List<Short> leafWetness) {
		this.leafWetness = leafWetness;
	}

	public List<Short> getExtraTemperature() {
		return extraTemperature;
	}

	public void setExtraTemperature(List<Short> extraTemperature) {
		this.extraTemperature = extraTemperature;
	}

	public List<Short> getExtraHumidity() {
		return extraHumidity;
	}

	public void setExtraHumidity(List<Short> extraHumidity) {
		this.extraHumidity = extraHumidity;
	}

}
