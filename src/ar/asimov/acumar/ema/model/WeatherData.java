package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@IdClass(WeatherData.PrimaryKey.class)
@Entity
@Table(name="ta_ams_weather")
public class WeatherData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final class PrimaryKey implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String station;
		
		private LocalDate date;
		
		private LocalTime startTime;
		
		public String getStation() {
			return station;
		}
		public void setStation(String estacion) {
			this.station = estacion;
		}
		public LocalDate getDate() {
			return date;
		}
		public void setDate(LocalDate fecha) {
			this.date = fecha;
		}
		public LocalTime getStartTime() {
			return startTime;
		}
		public void setStartTime(LocalTime horaDesde) {
			this.startTime = horaDesde;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((station == null) ? 0 : station.hashCode());
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
			if (station == null) {
				if (other.station != null)
					return false;
			} else if (!station.equals(other.station))
				return false;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (startTime == null) {
				if (other.startTime != null)
					return false;
			} else if (!startTime.equals(other.startTime))
				return false;
			return true;
		}
	
		
	}

	@Id
	@ManyToOne
	@JoinColumn(name="station_id",referencedColumnName="station_id")
	private Station station;
	@Id
	@Column(name="date")
	private LocalDate date;
	@Id
	@Column(name="start_time")
	private LocalTime startTime;
	@Column(name="end_time")
	private LocalTime endTime;
	@Column(name="icon_flags")
	private Byte iconFlags;
	@Column(name="more_flags")
	private Byte moreFlags;
	@Column(name="out_temp")
	private Double outsideTemperature;
	@Column(name="max_out_temp")
	private Double maxOutsideTemperature;
	@Column(name="min_out_temp")
	private Double minOutsideTemperature;
	@Column(name="in_temp")
	private Double insideTemperature;
	@Column(name="pressure")
	private Double pressure;
	@Column(name="out_hum")
	private Double outsideHumidity;
	@Column(name="in_hum")
	private Double insideHumidity;
	@Column(name="rain")
	private Short precipitation;
	@Column(name="rain_col_type")
	private Double rainCollectorType;
	@Column(name="max_rain_rate")
	private Short maxPrecipitationRate;
	@Column(name="wind_speed")
	private Double windSpeed;
	@Column(name="max_wind_speed")
	private Double maxWindSpeed;
	@Column(name="wind_dir")
	@Enumerated
	private WindDirection windDirection;
	@Column(name="max_wind_dir")
	@Enumerated
	private WindDirection maxWindDirection;
	@Column(name="wind_samples_num")
	private Short windSamplesNumber;
	@Column(name="solar_rad")
	private Short solarRadiation;
	@Column(name="max_solar_rad")
	private Short maxSolarRadiation;
	@Column(name="uv_index")
	private Double UVIndex;
	@Column(name="max_uv_index")
	private Double maxUVIndex;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<LeafTemperature> leafTemperature;

	@Column(name="extra_rad")
	private Short extraRadiation;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<NewSensor> newSensors;
	
	@Column(name="forecast")
	private Byte forecast;
	
	@Column(name="et")
	private Double ET;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<SoilTemperature> soilTemperature;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<SoilMoisture> soilMoisture;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<LeafWetness> leafWetness;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<ExtraTemperature> extraTemperature;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumns({
		@JoinColumn(name="station_id", referencedColumnName="station_id"),
		@JoinColumn(name="date", referencedColumnName="date"),
		@JoinColumn(name="start_time", referencedColumnName="start_time")
	})
	private List<ExtraHumidity> extraHumidity;

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

	public Byte getIconFlags() {
		return iconFlags;
	}

	public void setIconFlags(Byte iconFlags) {
		this.iconFlags = iconFlags;
	}

	public Byte getMoreFlags() {
		return moreFlags;
	}

	public void setMoreFlags(Byte moreFlags) {
		this.moreFlags = moreFlags;
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

	public Double getRainCollectorType() {
		return rainCollectorType;
	}

	public void setRainCollectorType(Double rainCollectorType) {
		this.rainCollectorType = rainCollectorType;
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

	public WindDirection getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(WindDirection windDirection) {
		this.windDirection = windDirection;
	}

	public WindDirection getMaxWindDirection() {
		return maxWindDirection;
	}

	public void setMaxWindDirection(WindDirection maxWindDirection) {
		this.maxWindDirection = maxWindDirection;
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

	public void setSolarRadiation(Short solarRadiation) {
		this.solarRadiation = solarRadiation;
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

	public List<LeafTemperature> getLeafTemperature() {
		return leafTemperature;
	}

	public void setLeafTemperature(List<LeafTemperature> leafTemperature) {
		this.leafTemperature = leafTemperature;
	}

	public Short getExtraRadiation() {
		return extraRadiation;
	}

	public void setExtraRadiation(Short extraRadiation) {
		this.extraRadiation = extraRadiation;
	}

	public List<NewSensor> getNewSensors() {
		return newSensors;
	}

	public void setNewSensors(List<NewSensor> newSensors) {
		this.newSensors = newSensors;
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

	public List<SoilTemperature> getSoilTemperature() {
		return soilTemperature;
	}

	public void setSoilTemperature(List<SoilTemperature> soilTemperature) {
		this.soilTemperature = soilTemperature;
	}

	public List<SoilMoisture> getSoilMoisture() {
		return soilMoisture;
	}

	public void setSoilMoisture(List<SoilMoisture> soilMoisture) {
		this.soilMoisture = soilMoisture;
	}

	public List<LeafWetness> getLeafWetness() {
		return leafWetness;
	}

	public void setLeafWetness(List<LeafWetness> leafWetness) {
		this.leafWetness = leafWetness;
	}

	public List<ExtraTemperature> getExtraTemperature() {
		return extraTemperature;
	}

	public void setExtraTemperature(List<ExtraTemperature> extraTemperature) {
		this.extraTemperature = extraTemperature;
	}

	public List<ExtraHumidity> getExtraHumidity() {
		return extraHumidity;
	}

	public void setExtraHumidity(List<ExtraHumidity> extraHumidity) {
		this.extraHumidity = extraHumidity;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
		WeatherData other = (WeatherData) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		return true;
	}

	
	
}
	