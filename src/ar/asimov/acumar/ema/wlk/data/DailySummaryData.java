package ar.asimov.acumar.ema.wlk.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import ar.asimov.acumar.ema.wlk.record.DailySummary1;
import ar.asimov.acumar.ema.wlk.record.DailySummary2;
import javolution.io.Struct.Unsigned8;

public class DailySummaryData implements Serializable {
	
	public static final DailySummaryData from(LocalDate date,DailySummary1 summary1,DailySummary2 summary2){
		DailySummaryData summary = new DailySummaryData();
		summary.setDate(date);
		
		summary.setDataSpan(summary1.dataSpan.get());
		summary.setMaxOutTemperature((summary1.hiOutTemp.get() / 1e1));
		summary.setMinOutTemperature((summary1.lowOutTemp.get() / 1e1));
		summary.setAvgOutTemperature((summary1.avgOutTemp.get() / 1e1));
		summary.setMaxOutTemperatureTime(unpackTime(summary1.timeValues,0));
		summary.setMinOutTemperatureTime(unpackTime(summary1.timeValues,1));
		summary.setMaxInTemperature((summary1.hiInTemp.get() / 1e1));
		summary.setMinInTemperature((summary1.lowInTemp.get() / 1e1));
		summary.setAvgInTemperature((summary1.avgInTemp.get() / 1e1));
		summary.setMaxInTemperatureTime(unpackTime(summary1.timeValues,2));
		summary.setMinInTemperatureTime(unpackTime(summary1.timeValues,3));
		summary.setMaxWindChill((summary1.hiChill.get() / 1e1));
		summary.setMinWindChill((summary1.lowChill.get() / 1e1));
		summary.setAvgWindChill((summary1.avgChill.get() / 1e1));
		summary.setMaxWindChillTime(unpackTime(summary1.timeValues,4));
		summary.setMinWindChillTime(unpackTime(summary1.timeValues,5));
		summary.setMaxDewPoint((summary1.hiDew.get() / 1e1));
		summary.setMinDewPoint((summary1.lowDew.get() / 1e1));
		summary.setAvgDewPoint((summary1.avgDew.get() / 1e1));
		summary.setMaxDewPointTime(unpackTime(summary1.timeValues,6));
		summary.setMinDewPointTime(unpackTime(summary1.timeValues,7));
		summary.setMaxOutHumidity((summary1.hiOutHum.get() / 1e1));
		summary.setMinOutHumidity((summary1.lowOutHum.get() / 1e1));
		summary.setAvgOutHumidity((summary1.avgOutHum.get() / 1e1));
		summary.setMaxOutHumidityTime(unpackTime(summary1.timeValues,8));
		summary.setMinOutHumidityTime(unpackTime(summary1.timeValues,9));
		summary.setMaxInHumidity((summary1.hiInHum.get() / 1e1));
		summary.setMinInHumidity((summary1.lowInHum.get() / 1e1));
		summary.setMaxInHumidityTime(unpackTime(summary1.timeValues,10));
		summary.setMinInInHumidityTime(unpackTime(summary1.timeValues,11));
		summary.setMaxPressure((summary1.hiBar.get() / 1e3 ));
		summary.setMinPressure((summary1.lowBar.get() / 1e3));
		summary.setAvgPressure((summary1.avgBar.get() / 1e3));
		summary.setMaxPressureTime(unpackTime(summary1.timeValues,12));
		summary.setMinPressureTime(unpackTime(summary1.timeValues,13));
		summary.setMaxWindSpeed((summary1.hiSpeed.get() / 1e1));
		summary.setMaxWindSpeedDirection(summary1.dirHiSpeed.get());
		summary.setMaxWindSpeedTime(unpackTime(summary1.timeValues,14));
		summary.setMaxAvg10MinWindSpeed((Short.MIN_VALUE == summary1.hi10MinSpeed.get())?null:(summary1.hi10MinSpeed.get() / 1e1));
		summary.setMaxAvg10MinWindSpeedDirection(summary1.hi10MinDir.get());
		summary.setMaxAvg10MinWindSpeedTime(unpackTime(summary1.timeValues,15));
		summary.setDailyPrecipitation((summary1.dailyRainTotal.get() / 1e3));
		summary.setMaxPrecipitationRate((summary1.hiRainRate.get() / 1e2));
		summary.setMaxPrecipitationRateTime(unpackTime(summary1.timeValues,16));
		summary.setDailyUVDose((Short.MIN_VALUE == summary1.dailyUVDose.get())?null:(summary1.dailyUVDose.get() / 1e1));
		summary.setMaxUVDose((Short.MIN_VALUE == summary1.hiUV.get())?null:(summary1.hiUV.get() / 1e1));
		summary.setMaxUVDoseTime(unpackTime(summary1.timeValues,17));
		summary.setWindPackets(summary2.numWindPackets.get());
		summary.setMaxSolar((Short.MIN_VALUE == summary2.hiSolar.get())?null:summary2.hiSolar.get());
		summary.setMaxSolarTime(unpackTime(summary2.timeValues,0));
		summary.setDailySolarEnergy((Short.MIN_VALUE == summary2.dailySolarEnergy.get())?null:(summary2.dailySolarEnergy.get() / 1e1));
		summary.setMinSunlinght((Short.MIN_VALUE == summary2.minSunlinght.get())?null:summary2.minSunlinght.get());
		summary.setDailyET((summary2.dailyETTotal.get() / 1e3));
		summary.setMaxHeat((summary2.hiHeat.get() / 1e1));
		summary.setMinHeat((summary2.lowHeat.get() / 1e1));
		summary.setMaxHeatTime(unpackTime(summary2.timeValues, 1));
		summary.setMinHeatTime(unpackTime(summary2.timeValues,2));
		summary.setAvgHeat((summary2.avgHeat.get() / 1e1));
		summary.setMaxTHSW((Short.MIN_VALUE == summary2.hiTHSW.get())?null:(summary2.hiTHSW.get() / 1e1));
		summary.setMaxTHSWTime(unpackTime(summary2.timeValues,3));
		summary.setMinTHSW((Short.MIN_VALUE == summary2.lowTHSW.get())?null:(summary2.lowTHSW.get() / 1e1));
		summary.setMinTHSWTime(unpackTime(summary2.timeValues,4));
		summary.setMaxTHW((summary2.hiTHW.get() / 1e1));
		summary.setMaxTHWTime(unpackTime(summary2.timeValues,5));
		summary.setMinTHW((summary2.lowTHW.get() / 1e1));
		summary.setMinTHWTime(unpackTime(summary2.timeValues,6));
		summary.setIntegratedHeatDD65((summary2.integratedHeatDD65.get() / 1e1));
		summary.setWindDirectionDistribution(unpackWindDirectionDistribution(summary2.dirBins));
		summary.setIntegratedCoolDD65((summary2.integratedCoolDD65.get() / 1e1));
		
		return summary;
	}
	
	private static final LocalTime unpackTime(Unsigned8[] pack,int index){
		int fieldIndex = (index/2)*3;
		int value;
		if(index % 2 == 0){
			value = pack[fieldIndex].get() + ((pack[fieldIndex+2].get() & 0x0F)<<8);	
		}else{
			value = pack[fieldIndex+1].get() + ((pack[fieldIndex+2].get() & 0xF0)<<4); 
		}
		if(value == 0x0FFF || value == 0x07FF || value == 0x1000 || value == 0x0800) return null;
		int hour = (int)Math.floor(value/60);
		int minutes = value%60;
		if(hour == 24) hour = 0;
		return LocalTime.of(hour,minutes);
	}
	
	private static final LocalTime[] unpackWindDirectionDistribution(Unsigned8[] directionBins){
		LocalTime[] times = new LocalTime[16];
		for(int i=0;i<times.length;i++){
			times[i] = unpackTime(directionBins,i);
		}
		return times;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//DailySummary1 fields

	private LocalDate date;
	
	/**
	 * Total # of minutes accounted for by physical records for this day
	 */
	private Short dataSpan;
	
	
	private Double maxOutTemperature;
	private LocalTime maxOutTemperatureTime;
	private Double minOutTemperature;
	private LocalTime minOutTemperatureTime;
	private Double avgOutTemperature;
	
	private Double maxInTemperature;
	private LocalTime maxInTemperatureTime;
	private Double minInTemperature;
	private LocalTime minInTemperatureTime;
	private Double avgInTemperature;
	
	private Double maxWindChill;
	private LocalTime maxWindChillTime;
	private Double minWindChill;
	private LocalTime minWindChillTime;
	private Double avgWindChill;
	
	
	private Double maxDewPoint;
	private LocalTime maxDewPointTime;
	private Double minDewPoint;
	private LocalTime minDewPointTime;
	private Double avgDewPoint;
		
	private Double maxOutHumidity;
	private LocalTime maxOutHumidityTime;
	private Double minOutHumidity;
	private LocalTime minOutHumidityTime;
	private Double avgOutHumidity;
	
	private Double maxInHumidity;
	private LocalTime maxInHumidityTime;
	private Double minInHumidity;
	private LocalTime minInInHumidityTime;
	
	private Double maxPressure;
	private LocalTime maxPressureTime;
	private Double minPressure;
	private LocalTime minPressureTime;
	private Double avgPressure;
	
	private Double dailyWindRun;
	
	private Double maxAvg10MinWindSpeed;
	private LocalTime maxAvg10MinWindSpeedTime;
	private Short maxAvg10MinWindSpeedDirection;

	private Double avgWindSpeed;
	private Double maxWindSpeed;
	private LocalTime maxWindSpeedTime;
	private Short maxWindSpeedDirection;
	
	private Double dailyPrecipitation;
	
	private Double maxPrecipitationRate;
	private LocalTime maxPrecipitationRateTime;
	
	private Double dailyUVDose;
	
	private Double maxUVDose;
	private LocalTime maxUVDoseTime;
	
	
	//DailySummary2 fields
	//TODO: add support for todaysWeather field when implemented
	
	private Short windPackets;
	
	private Short maxSolar;
	private LocalTime maxSolarTime;
	
	private Short minSunlinght;
	
	private Double dailySolarEnergy;
	private Double dailyET;
	
	private Double maxHeat;
	private LocalTime maxHeatTime;
	private Double minHeat;
	private LocalTime minHeatTime;
	private Double avgHeat;
	
	private Double maxTHSW;
	private LocalTime maxTHSWTime;
	private Double minTHSW;
	private LocalTime minTHSWTime;
	
	private Double maxTHW;
	private LocalTime maxTHWTime;
	private Double minTHW;
	private LocalTime minTHWTime;
	
	private Double integratedHeatDD65;
	
	//TODO: add support for Web Bulb fields currently not supported
	private LocalTime[] windDirectionDistribution;
	
	
	private Double integratedCoolDD65;


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	public Short getDataSpan() {
		return dataSpan;
	}


	public void setDataSpan(Short dataSpan) {
		this.dataSpan = dataSpan;
	}


	public Double getMaxOutTemperature() {
		return maxOutTemperature;
	}


	public void setMaxOutTemperature(Double maxOutTemperature) {
		this.maxOutTemperature = maxOutTemperature;
	}


	public LocalTime getMaxOutTemperatureTime() {
		return maxOutTemperatureTime;
	}


	public void setMaxOutTemperatureTime(LocalTime maxOutTemperatureTime) {
		this.maxOutTemperatureTime = maxOutTemperatureTime;
	}


	public Double getMinOutTemperature() {
		return minOutTemperature;
	}


	public void setMinOutTemperature(Double minOutTemperature) {
		this.minOutTemperature = minOutTemperature;
	}


	public LocalTime getMinOutTemperatureTime() {
		return minOutTemperatureTime;
	}


	public void setMinOutTemperatureTime(LocalTime minOutTemperatureTime) {
		this.minOutTemperatureTime = minOutTemperatureTime;
	}


	public Double getAvgOutTemperature() {
		return avgOutTemperature;
	}


	public void setAvgOutTemperature(Double avgOutTemperature) {
		this.avgOutTemperature = avgOutTemperature;
	}


	public Double getMaxInTemperature() {
		return maxInTemperature;
	}


	public void setMaxInTemperature(Double maxInTemperature) {
		this.maxInTemperature = maxInTemperature;
	}


	public LocalTime getMaxInTemperatureTime() {
		return maxInTemperatureTime;
	}


	public void setMaxInTemperatureTime(LocalTime maxInTemperatureTime) {
		this.maxInTemperatureTime = maxInTemperatureTime;
	}


	public Double getMinInTemperature() {
		return minInTemperature;
	}


	public void setMinInTemperature(Double minInTemperature) {
		this.minInTemperature = minInTemperature;
	}


	public LocalTime getMinInTemperatureTime() {
		return minInTemperatureTime;
	}


	public void setMinInTemperatureTime(LocalTime minInTemperatureTime) {
		this.minInTemperatureTime = minInTemperatureTime;
	}


	public Double getAvgInTemperature() {
		return avgInTemperature;
	}


	public void setAvgInTemperature(Double avgInTemperature) {
		this.avgInTemperature = avgInTemperature;
	}


	public Double getMaxWindChill() {
		return maxWindChill;
	}


	public void setMaxWindChill(Double maxWindChill) {
		this.maxWindChill = maxWindChill;
	}


	public LocalTime getMaxWindChillTime() {
		return maxWindChillTime;
	}


	public void setMaxWindChillTime(LocalTime maxWindChillTime) {
		this.maxWindChillTime = maxWindChillTime;
	}


	public Double getMinWindChill() {
		return minWindChill;
	}


	public void setMinWindChill(Double minWindChill) {
		this.minWindChill = minWindChill;
	}


	public LocalTime getMinWindChillTime() {
		return minWindChillTime;
	}


	public void setMinWindChillTime(LocalTime minWindChillTime) {
		this.minWindChillTime = minWindChillTime;
	}


	public Double getAvgWindChill() {
		return avgWindChill;
	}


	public void setAvgWindChill(Double avgWindChill) {
		this.avgWindChill = avgWindChill;
	}


	public Double getMaxDewPoint() {
		return maxDewPoint;
	}


	public void setMaxDewPoint(Double maxDewPoint) {
		this.maxDewPoint = maxDewPoint;
	}


	public LocalTime getMaxDewPointTime() {
		return maxDewPointTime;
	}


	public void setMaxDewPointTime(LocalTime maxDewPointTime) {
		this.maxDewPointTime = maxDewPointTime;
	}


	public Double getMinDewPoint() {
		return minDewPoint;
	}


	public void setMinDewPoint(Double minDewPoint) {
		this.minDewPoint = minDewPoint;
	}


	public LocalTime getMinDewPointTime() {
		return minDewPointTime;
	}


	public void setMinDewPointTime(LocalTime minDewPointTime) {
		this.minDewPointTime = minDewPointTime;
	}


	public Double getAvgDewPoint() {
		return avgDewPoint;
	}


	public void setAvgDewPoint(Double avgDewPoint) {
		this.avgDewPoint = avgDewPoint;
	}


	public Double getMaxOutHumidity() {
		return maxOutHumidity;
	}


	public void setMaxOutHumidity(Double maxOutHumidity) {
		this.maxOutHumidity = maxOutHumidity;
	}


	public LocalTime getMaxOutHumidityTime() {
		return maxOutHumidityTime;
	}


	public void setMaxOutHumidityTime(LocalTime maxOutHumidityTime) {
		this.maxOutHumidityTime = maxOutHumidityTime;
	}


	public Double getMinOutHumidity() {
		return minOutHumidity;
	}


	public void setMinOutHumidity(Double minOutHumidity) {
		this.minOutHumidity = minOutHumidity;
	}


	public LocalTime getMinOutHumidityTime() {
		return minOutHumidityTime;
	}


	public void setMinOutHumidityTime(LocalTime minOutHumidityTime) {
		this.minOutHumidityTime = minOutHumidityTime;
	}


	public Double getAvgOutHumidity() {
		return avgOutHumidity;
	}


	public void setAvgOutHumidity(Double avgOutHumidity) {
		this.avgOutHumidity = avgOutHumidity;
	}


	public Double getMaxInHumidity() {
		return maxInHumidity;
	}


	public void setMaxInHumidity(Double maxInHumidity) {
		this.maxInHumidity = maxInHumidity;
	}


	public LocalTime getMaxInHumidityTime() {
		return maxInHumidityTime;
	}


	public void setMaxInHumidityTime(LocalTime maxInHumidityTime) {
		this.maxInHumidityTime = maxInHumidityTime;
	}


	public Double getMinInHumidity() {
		return minInHumidity;
	}


	public void setMinInHumidity(Double minInHumidity) {
		this.minInHumidity = minInHumidity;
	}


	public LocalTime getMinInHumidityTime() {
		return minInInHumidityTime;
	}


	public void setMinInInHumidityTime(LocalTime minInInHumidityTime) {
		this.minInInHumidityTime = minInInHumidityTime;
	}	


	public Double getMaxPressure() {
		return maxPressure;
	}


	public void setMaxPressure(Double maxPressure) {
		this.maxPressure = maxPressure;
	}


	public LocalTime getMaxPressureTime() {
		return maxPressureTime;
	}


	public void setMaxPressureTime(LocalTime maxPressureTime) {
		this.maxPressureTime = maxPressureTime;
	}


	public Double getMinPressure() {
		return minPressure;
	}


	public void setMinPressure(Double minPressure) {
		this.minPressure = minPressure;
	}


	public LocalTime getMinPressureTime() {
		return minPressureTime;
	}


	public void setMinPressureTime(LocalTime minPressureTime) {
		this.minPressureTime = minPressureTime;
	}


	public Double getAvgPressure() {
		return avgPressure;
	}


	public void setAvgPressure(Double avgPressure) {
		this.avgPressure = avgPressure;
	}


	public Double getDailyWindRun() {
		return dailyWindRun;
	}


	public void setDailyWindRun(Double dailyWindRun) {
		this.dailyWindRun = dailyWindRun;
	}


	public Double getMaxAvg10MinWindSpeed() {
		return maxAvg10MinWindSpeed;
	}


	public void setMaxAvg10MinWindSpeed(Double maxAvg10MinWindSpeed) {
		this.maxAvg10MinWindSpeed = maxAvg10MinWindSpeed;
	}


	public LocalTime getMaxAvg10MinWindSpeedTime() {
		return maxAvg10MinWindSpeedTime;
	}


	public void setMaxAvg10MinWindSpeedTime(LocalTime maxAvg10MinWindSpeedTime) {
		this.maxAvg10MinWindSpeedTime = maxAvg10MinWindSpeedTime;
	}


	public Short getMaxAvg10MinWindSpeedDirection() {
		return maxAvg10MinWindSpeedDirection;
	}


	public void setMaxAvg10MinWindSpeedDirection(Short maxAvg10MinWindSpeedDirection) {
		this.maxAvg10MinWindSpeedDirection = maxAvg10MinWindSpeedDirection;
	}


	public Double getAvgWindSpeed() {
		return avgWindSpeed;
	}


	public void setAvgWindSpeed(Double avgWindSpeed) {
		this.avgWindSpeed = avgWindSpeed;
	}


	public Double getMaxWindSpeed() {
		return maxWindSpeed;
	}


	public void setMaxWindSpeed(Double maxWindSpeed) {
		this.maxWindSpeed = maxWindSpeed;
	}


	public LocalTime getMaxWindSpeedTime() {
		return maxWindSpeedTime;
	}


	public void setMaxWindSpeedTime(LocalTime maxWindSpeedTime) {
		this.maxWindSpeedTime = maxWindSpeedTime;
	}


	public Short getMaxWindSpeedDirection() {
		return maxWindSpeedDirection;
	}


	public void setMaxWindSpeedDirection(Short maxWindSpeedDirection) {
		this.maxWindSpeedDirection = maxWindSpeedDirection;
	}


	public Double getDailyPrecipitation() {
		return dailyPrecipitation;
	}


	public void setDailyPrecipitation(Double dailyPrecipitation) {
		this.dailyPrecipitation = dailyPrecipitation;
	}


	public Double getMaxPrecipitationRate() {
		return maxPrecipitationRate;
	}


	public void setMaxPrecipitationRate(Double maxPrecipitationRate) {
		this.maxPrecipitationRate = maxPrecipitationRate;
	}


	public LocalTime getMaxPrecipitationRateTime() {
		return maxPrecipitationRateTime;
	}


	public void setMaxPrecipitationRateTime(LocalTime maxPrecipitationRateTime) {
		this.maxPrecipitationRateTime = maxPrecipitationRateTime;
	}


	public Double getDailyUVDose() {
		return dailyUVDose;
	}


	public void setDailyUVDose(Double dailyUVDose) {
		this.dailyUVDose = dailyUVDose;
	}


	public Double getMaxUVDose() {
		return maxUVDose;
	}


	public void setMaxUVDose(Double maxUVDose) {
		this.maxUVDose = maxUVDose;
	}


	public LocalTime getMaxUVDoseTime() {
		return maxUVDoseTime;
	}


	public void setMaxUVDoseTime(LocalTime maxUVDoseTime) {
		this.maxUVDoseTime = maxUVDoseTime;
	}


	public Short getWindPackets() {
		return windPackets;
	}


	public void setWindPackets(Short windPackets) {
		this.windPackets = windPackets;
	}


	public Short getMaxSolar() {
		return maxSolar;
	}


	public void setMaxSolar(Short maxSolar) {
		this.maxSolar = maxSolar;
	}


	public LocalTime getMaxSolarTime() {
		return maxSolarTime;
	}


	public void setMaxSolarTime(LocalTime maxSolarTime) {
		this.maxSolarTime = maxSolarTime;
	}


	public Short getMinSunlinght() {
		return minSunlinght;
	}


	public void setMinSunlinght(Short minSunlinght) {
		this.minSunlinght = minSunlinght;
	}


	public Double getDailySolarEnergy() {
		return dailySolarEnergy;
	}


	public void setDailySolarEnergy(Double dailySolarEnergy) {
		this.dailySolarEnergy = dailySolarEnergy;
	}


	public Double getDailyET() {
		return dailyET;
	}


	public void setDailyET(Double dailyET) {
		this.dailyET = dailyET;
	}


	public Double getMaxHeat() {
		return maxHeat;
	}


	public void setMaxHeat(Double maxHeat) {
		this.maxHeat = maxHeat;
	}


	public LocalTime getMaxHeatTime() {
		return maxHeatTime;
	}


	public void setMaxHeatTime(LocalTime maxHeatTime) {
		this.maxHeatTime = maxHeatTime;
	}


	public Double getMinHeat() {
		return minHeat;
	}


	public void setMinHeat(Double minHeat) {
		this.minHeat = minHeat;
	}


	public LocalTime getMinHeatTime() {
		return minHeatTime;
	}


	public void setMinHeatTime(LocalTime minHeatTime) {
		this.minHeatTime = minHeatTime;
	}


	public Double getAvgHeat() {
		return avgHeat;
	}


	public void setAvgHeat(Double avgHeat) {
		this.avgHeat = avgHeat;
	}


	public Double getMaxTHSW() {
		return maxTHSW;
	}


	public void setMaxTHSW(Double maxTHSW) {
		this.maxTHSW = maxTHSW;
	}


	public LocalTime getMaxTHSWTime() {
		return maxTHSWTime;
	}


	public void setMaxTHSWTime(LocalTime maxTHSWTime) {
		this.maxTHSWTime = maxTHSWTime;
	}


	public Double getMinTHSW() {
		return minTHSW;
	}


	public void setMinTHSW(Double minTHSW) {
		this.minTHSW = minTHSW;
	}


	public LocalTime getMinTHSWTime() {
		return minTHSWTime;
	}


	public void setMinTHSWTime(LocalTime minTHSWTime) {
		this.minTHSWTime = minTHSWTime;
	}


	public Double getMaxTHW() {
		return maxTHW;
	}


	public void setMaxTHW(Double maxTHW) {
		this.maxTHW = maxTHW;
	}


	public LocalTime getMaxTHWTime() {
		return maxTHWTime;
	}


	public void setMaxTHWTime(LocalTime maxTHWTime) {
		this.maxTHWTime = maxTHWTime;
	}


	public Double getMinTHW() {
		return minTHW;
	}


	public void setMinTHW(Double minTHW) {
		this.minTHW = minTHW;
	}


	public LocalTime getMinTHWTime() {
		return minTHWTime;
	}


	public void setMinTHWTime(LocalTime minTHWTime) {
		this.minTHWTime = minTHWTime;
	}


	public Double getIntegratedHeatDD65() {
		return integratedHeatDD65;
	}


	public void setIntegratedHeatDD65(Double integratedHeatDD65) {
		this.integratedHeatDD65 = integratedHeatDD65;
	}


	public LocalTime[] getWindDirectionDistribution() {
		return windDirectionDistribution;
	}


	public void setWindDirectionDistribution(LocalTime[] windDirectionDistribution) {
		this.windDirectionDistribution = windDirectionDistribution;
	}


	public Double getIntegratedCoolDD65() {
		return integratedCoolDD65;
	}


	public void setIntegratedCoolDD65(Double integratedCoolDD65) {
		this.integratedCoolDD65 = integratedCoolDD65;
	}


	
}
