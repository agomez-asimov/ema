package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ta_ams_station")
@Access(AccessType.FIELD)
public class Station implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="station_id", length=20)
	private String id;
	@Column(name="name")
	private String name;
	@Column(name="location")
	private String location;
	@Column(name="db_path")
	private String dbPath;
	@Column(name="last_processed_date")
	private LocalDate lastProcessedDate;
	@Column(name="last_processed_records")
	private Integer lastProcessedRecords;
	@Column(name="last_update")
	private Instant lastUpdate;
	@Column(name="total_processed_records")
	private Integer totalProcessedRecords;
	@Column(name="log_file")
	private String logFile;
	@Column(name="iddle_time")
	private Duration iddleTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String ubicacion) {
		this.location = ubicacion;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String baseDeDatosArchivo) {
		this.dbPath = baseDeDatosArchivo;
	}

	public LocalDate getLastProcessedDate() {
		return this.lastProcessedDate;
	}

	public void setLastProcessedDate(LocalDate lastProcessedDate) {
		this.lastProcessedDate = lastProcessedDate;
	}

	public Instant getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Instant lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Integer getLastProcessedRecords() {
		return lastProcessedRecords;
	}

	public void setLastProcessedRecords(Integer lastProcessedRecords) {
		this.lastProcessedRecords = lastProcessedRecords;
	}

	public Integer getTotalProcessedRecords() {
		return totalProcessedRecords;
	}

	public void setTotalProcessedRecords(Integer totalProcessedRecords) {
		this.totalProcessedRecords = totalProcessedRecords;
	}
	
	

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public Duration getIddleTime() {
		return iddleTime;
	}

	public void setIddleTime(Duration iddleTime) {
		this.iddleTime = iddleTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbPath == null) ? 0 : dbPath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Station other = (Station) obj;
		if (dbPath == null) {
			if (other.dbPath != null)
				return false;
		} else if (!dbPath.equals(other.dbPath))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
		
	
}
