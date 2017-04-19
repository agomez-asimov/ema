package ar.asimov.acumar.ema.services;
import java.io.Serializable;
import java.time.LocalDate;

import ar.asimov.acumar.ema.model.Station;

public class ProcessInformation implements Serializable{
	 	
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer totalProcessed;
	private LocalDate lastProcessedDate;
	private Integer lastProcessedRecords;
	private Station station;
	
	public ProcessInformation() {
		this.totalProcessed = 0;
	}
	
	public Integer getTotalProcessed() {
		return totalProcessed;
	}
	public void setTotalProcessed(Integer totalProcessed) {
		this.totalProcessed = totalProcessed;
	}
	public LocalDate getLastProcessedDate() {
		return lastProcessedDate;
	}
	public void setLastProcessedDate(LocalDate lastProcessedDate) {
		this.lastProcessedDate = lastProcessedDate;
	}
	public Integer getLastProcessedRecords() {
		return lastProcessedRecords;
	}
	public void setLastProcessedRecords(Integer lastProcessedRecords) {
		this.lastProcessedRecords = lastProcessedRecords;
	}
	
	
	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastProcessedDate == null) ? 0 : lastProcessedDate.hashCode());
		result = prime * result + ((lastProcessedRecords == null) ? 0 : lastProcessedRecords.hashCode());
		result = prime * result + ((totalProcessed == null) ? 0 : totalProcessed.hashCode());
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
		ProcessInformation other = (ProcessInformation) obj;
		if (lastProcessedDate == null) {
			if (other.lastProcessedDate != null)
				return false;
		} else if (!lastProcessedDate.equals(other.lastProcessedDate))
			return false;
		if (lastProcessedRecords == null) {
			if (other.lastProcessedRecords != null)
				return false;
		} else if (!lastProcessedRecords.equals(other.lastProcessedRecords))
			return false;
		if (totalProcessed == null) {
			if (other.totalProcessed != null)
				return false;
		} else if (!totalProcessed.equals(other.totalProcessed))
			return false;
		return true;
	}
	
}