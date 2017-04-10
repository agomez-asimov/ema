package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: WindDistributionEntry
 *
 */
@IdClass(WindDistributionEntry.PrimaryKey.class)
@Entity
@Table(name="ta_ams_weather_summary_wind_distribution")
public class WindDistributionEntry implements Serializable {

	public static final class PrimaryKey implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private LocalDate date;
		
		private String station;
		
		private WindDirection direction;
		
		public PrimaryKey() {
			super();
		}

		public WindDirection getDirection() {
			return direction;
		}

		public void setDirection(WindDirection direction) {
			this.direction = direction;
		}
		

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
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
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
			if (direction != other.direction)
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

	public WindDistributionEntry() {
		super();
	}
	
	@Id
	@Column(name="date")
	private LocalDate date;
	@Id
	@Column(name="station_id")
	private Station station;
	@Id
	@Column(name="direction")
	@Enumerated
	private WindDirection direction;
	@Column(name="time")
	private LocalTime time;

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

	public WindDirection getDirection() {
		return direction;
	}

	public void setDirection(WindDirection direction) {
		this.direction = direction;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
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
		WindDistributionEntry other = (WindDistributionEntry) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (direction != other.direction)
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		return true;
	}

	

	
}
