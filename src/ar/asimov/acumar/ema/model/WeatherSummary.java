package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: WeatherSummary
 *
 */
@IdClass(WeatherSummary.PrimaryKey.class)
@Entity
@Table(name="ta_ams_weather_summary")
public class WeatherSummary implements Serializable {
	
	public static class PrimaryKey implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		protected LocalDate date;
		
		protected String station;
		
		public LocalDate getDate() {
			return date;
		}
		public void setDate(LocalDate date) {
			this.date = date;
		}
		public String getStation() {
			return station;
		}
		public void setStation(String station) {
			this.station = station;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result + ((station == null) ? 0 : station.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PrimaryKey other = (PrimaryKey) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (station == null) {
				if (other.station != null)
					return false;
			} else if (!station.equals(other.station))
				return false;
			return true;
		}
	
		
		
		
	}

	
	private static final long serialVersionUID = 1L;
	
	//DailySummary1 fields
	@Id
	@Column(name="date")
	private LocalDate date;
	@Id
	@ManyToOne
	@JoinColumn(name="station_id", referencedColumnName="station_id")
	private Station station;
	
	/**
	 * Total # of minutes accounted for by physical records for this day
	 */
	@Column(name="data_span")
	private Short dataSpan;
	@Column(name="max_out_temp")
	private Double maxOutTemperature;
	@Column(name="max_out_temp_time")
	private LocalTime maxOutTemperatureTime;
	@Column(name="min_out_temp")
	private Double minOutTemperature;
	@Column(name="min_out_temp_time")
	private LocalTime minOutTemperatureTime;
	@Column(name="avg_out_temp")
	private Double avgOutTemperature;
	@Column(name="max_in_temp")
	private Double maxInTemperature;
	@Column(name="max_in_temp_time")
	private LocalTime maxInTemperatureTime;
	@Column(name="min_in_temp")
	private Double minInTemperature;
	@Column(name="min_in_temp_time")
	private LocalTime minInTemperatureTime;
	@Column(name="avg_in_temp")
	private Double avgInTemperature;
	@Column(name="max_wind_chill")
	private Double maxWindChill;
	@Column(name="max_wind_chill_time")
	private LocalTime maxWindChillTime;
	@Column(name="min_wind_chill")
	private Double minWindChill;
	@Column(name="min_wind_chill_time")
	private LocalTime minWindChillTime;
	@Column(name="avg_wind_chill")
	private Double avgWindChill;
	@Column(name="max_dew_point")
	private Double maxDewPoint;
	@Column(name="max_dew_point_time")
	private LocalTime maxDewPointTime;
	@Column(name="min_dew_point")
	private Double minDewPoint;
	@Column(name="min_dew_point_time")
	private LocalTime minDewPointTime;
	@Column(name="avg_dew_point")
	private Double avgDewPoint;
	@Column(name="max_out_hum")
	private Double maxOutHumidity;
	@Column(name="max_out_hum_time")
	private LocalTime maxOutHumidityTime;
	@Column(name="min_out_hum")
	private Double minOutHumidity;
	@Column(name="min_out_hum_time")
	private LocalTime minOutHumidityTime;
	@Column(name="avg_out_hum")
	private Double avgOutHumidity;
	@Column(name="max_in_hum")
	private Double maxInHumidity;
	@Column(name="max_in_hum_time")
	private LocalTime maxInHumidityTime;
	@Column(name="min_in_hum")
	private Double minInHumidity;
	@Column(name="min_in_humidity_time")
	private LocalTime minInHumidityTime;
	@Column(name="max_pressure")
	private Double maxPressure;
	@Column(name="max_pressure_time")
	private LocalTime maxPressureTime;
	@Column(name="min_pressure")
	private Double minPressure;
	@Column(name="min_pressure_time")
	private LocalTime minPressureTime;
	@Column(name="avg_pressure")
	private Double avgPressure;
	@Column(name="dailyWindRun")
	private Double dailyWindRun;
	@Column(name="max_avg10Min_wind_speed")
	private Double maxAvg10MinWindSpeed;
	@Column(name="max_avg10Min_wind_speed_time")
	private LocalTime maxAvg10MinWindSpeedTime;
	@Column(name="max_avg10Min_wind_speed_dir")
	private Short maxAvg10MinWindSpeedDirection;
	@Column(name="avg_wind_speed")
	private Double avgWindSpeed;
	@Column(name="max_wind_speed")
	private Double maxWindSpeed;
	@Column(name="max_wind_speed_time")
	private LocalTime maxWindSpeedTime;
	@Column(name="max_wind_speed_direction")
	private Short maxWindSpeedDirection;
	@Column(name="daily_rain")
	private Double dailyPrecipitation;
	@Column(name="max_rain_rate")
	private Double maxPrecipitationRate;
	@Column(name="max_rain_rate_time")
	private LocalTime maxPrecipitationRateTime;
	@Column(name="daily_uv_dose")
	private Double dailyUVDose;
	@Column(name="max_uv_dose")
	private Double maxUVDose;
	@Column(name="max_uv_dose_time")
	private LocalTime maxUVDoseTime;
	
	
	//DailySummary2 fields
	//TODO: add support for todaysWeather field when implemented
	
	@Column(name="wind_packets")
	private Short windPackets;
	@Column(name="max_solar")
	private Short maxSolar;
	@Column(name="max_solar_time")
	private LocalTime maxSolarTime;
	@Column(name="min_sunlinght")
	private Short minSunlinght;
	@Column(name="daily_solar_energy")
	private Double dailySolarEnergy;
	@Column(name="daily_et")
	private Double dailyET;
	@Column(name="max_heat")
	private Double maxHeat;
	@Column(name="max_heat_time")
	private LocalTime maxHeatTime;
	@Column(name="min_heat")
	private Double minHeat;
	@Column(name="min_heat_time")
	private LocalTime minHeatTime;
	@Column(name="avg_heat")
	private Double avgHeat;
	@Column(name="max_thsw")
	private Double maxTHSW;
	@Column(name="max_thsw_time")
	private LocalTime maxTHSWTime;
	@Column(name="min_thsw")
	private Double minTHSW;
	@Column(name="min_thsw_time")
	private LocalTime minTHSWTime;
	@Column(name="max_thw")
	private Double maxTHW;
	@Column(name="max_thw_time")
	private LocalTime maxTHWTime;
	@Column(name="min_thw")
	private Double minTHW;
	@Column(name="min_thwt_time")
	private LocalTime minTHWTime;
	@Column(name="integrated_heat_dd65")
	private Double integratedHeatDD65;
	//TODO: add support for Web Bulb fields currently not supported
	@OneToMany
	@JoinColumns({
		@JoinColumn(name="station_id",referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
	})
	private List<WindDistributionEntry> windDirectionDistribution;
	@Column(name="integrated_cool_dd65")
	private Double integratedCoolDD65;


	public WeatherSummary() {
		super();
	}


	public Station getStation() {
		return station;
	}


	public void setStation(Station station) {
		this.station = station;
	}


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
		return minInHumidityTime;
	}


	public void setMinInHumidityTime(LocalTime minInHumidityTime) {
		this.minInHumidityTime = minInHumidityTime;
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


	public List<WindDistributionEntry> getWindDirectionDistribution() {
		return windDirectionDistribution;
	}


	public void setWindDirectionDistribution(List<WindDistributionEntry> windDirectionDistribution) {
		this.windDirectionDistribution = windDirectionDistribution;
	}


	public Double getIntegratedCoolDD65() {
		return integratedCoolDD65;
	}


	public void setIntegratedCoolDD65(Double integratedCoolDD65) {
		this.integratedCoolDD65 = integratedCoolDD65;
	}
	
	
	
}
