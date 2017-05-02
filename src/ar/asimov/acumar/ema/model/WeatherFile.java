package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: WeatherFile
 *
 */
@IdClass(WeatherFile.PrimaryKey.class)
@Entity
@Table(name = "ta_ams_weather_file")
@Access(AccessType.FIELD)
public class WeatherFile implements Serializable {

	public static class PrimaryKey implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String station;

		private YearMonth period;

		/**
		 * @return the station
		 */
		public String getStation() {
			return station;
		}

		/**
		 * @param station
		 *            the station to set
		 */
		public void setStation(String station) {
			this.station = station;
		}

		/**
		 * @return the period
		 */
		public YearMonth getPeriod() {
			return period;
		}

		/**
		 * @param period
		 *            the period to set
		 */
		public void setPeriod(YearMonth period) {
			this.period = period;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((period == null) ? 0 : period.hashCode());
			result = prime * result + ((station == null) ? 0 : station.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PrimaryKey other = (PrimaryKey) obj;
			if (period == null) {
				if (other.period != null)
					return false;
			} else if (!period.equals(other.period))
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

	@Id
	@ManyToOne
	@JoinColumn(name = "station_id", referencedColumnName = "station_id")
	private Station station;
	@Id
	@Column(name="period")
	private YearMonth period;
	@Column(name = "total_records")
	private Integer totalRecords;
	@Column(name = "last_day_index")
	private Integer lastDayIndex;
	@Column(name = "last_day_records")
	private Integer lastDayRecords;
	@Column(name = "date_updated")
	private LocalDateTime dateUpdated;
	@Column(name = "date_created")
	private LocalDateTime dateCreated;
	@Transient
	private List<WeatherSummary> summaries;
	@Transient
	private List<WeatherData> data;
	
	@PostLoad
	protected void onPostLoad(){
	}

	public WeatherFile() {
		super();
	}

	/**
	 * @return the station
	 */
	public Station getStation() {
		return station;
	}

	/**
	 * @param station
	 *            the station to set
	 */
	public void setStation(Station station) {
		this.station = station;
	}

	/**
	 * @return the period
	 */
	public YearMonth getPeriod() {
		return period;
	}

	/**
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(YearMonth period) {
		this.period = period;
	}

	/**
	 * @return the totalRecords
	 */
	public Integer getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords
	 *            the totalRecords to set
	 */
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @return the lastDayIndex
	 */
	public Integer getLastDayIndex() {
		return lastDayIndex;
	}

	/**
	 * @param lastDayIndex
	 *            the lastDayIndex to set
	 */
	public void setLastDayIndex(Integer lastDayIndex) {
		this.lastDayIndex = lastDayIndex;
	}

	/**
	 * @return the lastDayRecords
	 */
	public Integer getLastDayRecords() {
		return lastDayRecords;
	}

	/**
	 * @param lastDayRecords
	 *            the lastDayRecords to set
	 */
	public void setLastDayRecords(Integer lastDayRecords) {
		this.lastDayRecords = lastDayRecords;
	}

	/**
	 * @return the dateUpdated
	 */
	public LocalDateTime getDateUpdated() {
		return dateUpdated;
	}

	/**
	 * @param dateUpdated
	 *            the dateUpdated to set
	 */
	public void setDateUpdated(LocalDateTime dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	/**
	 * @return the dateCreated
	 */
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public void addWeatherData(WeatherData data){
		if(null == this.data){
			this.data = new ArrayList<>();
		}
		if(this.getPeriod().equals(YearMonth.from(data.getDate()))){
			data.setStation(this.getStation());
			this.data.add(data);	
		}
	}
	
	public void addWetaherSummary(WeatherSummary summary){
		if(null == this.summaries){
			this.summaries = new ArrayList<>();
		}
		if(this.getPeriod().equals(YearMonth.from(summary.getDate()))){
			summary.setStation(this.getStation());
			this.summaries.add(summary);	
		}
		
	}
	
	public List<WeatherData> getWeatherData(){
		if(this.data == null){
			this.data = new ArrayList<>();
		}
		return Collections.unmodifiableList(this.data);
	}
	
	public List<WeatherSummary> getWeatherSummaries(){
		if(this.summaries == null){
			this.summaries = new ArrayList<>();
		}
		return Collections.unmodifiableList(this.summaries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((station == null) ? 0 : station.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WeatherFile other = (WeatherFile) obj;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		return true;
	}

}
