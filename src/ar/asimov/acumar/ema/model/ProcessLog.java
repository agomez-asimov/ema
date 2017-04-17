package ar.asimov.acumar.ema.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: ProcessLog
 *
 */
@Entity
@Table(name="ta_ams_process_log")
@Access(AccessType.FIELD)
public class ProcessLog implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="process_id")
	private Integer id;
	@ManyToOne
	@JoinColumn(name="station_id", referencedColumnName="station_id")
	private Station station;
	@Column(name="process_start")
	private Instant start;
	@Column(name="total_processed_records")
	private Integer totalProcessedRecords;
	@Column(name="last_date_processed")
	private LocalDate lastDateProcessed;
	@Column(name="last_processed_records")
	private Integer lastProcessedRecords;
	@Column(name="process_end")
	private Instant end;
	@Column(name="abnormal_completion")
	private Boolean abnormalCompletion;
	@Column(name="abnormal_completion_cause")
	private String abnormalCompletionCause;

	
	public ProcessLog() {
		super();
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Station getStation() {
		return station;
	}


	public void setStation(Station station) {
		this.station = station;
	}


	public Instant getStart() {
		return start;
	}


	public void setStart(Instant start) {
		this.start = start;
	}


	public LocalDate getLastDateProcessed() {
		return lastDateProcessed;
	}


	public void setLastDateProcessed(LocalDate lastDateProcessed) {
		this.lastDateProcessed = lastDateProcessed;
	}


	public Integer getLastProcessedRecords() {
		return lastProcessedRecords;
	}


	public void setLastProcessedRecords(Integer lstProcessedRecords) {
		this.lastProcessedRecords = lstProcessedRecords;
	}


	public Boolean getAbnormalCompletion() {
		return abnormalCompletion;
	}


	public void setAbnormalCompletion(Boolean abnormalCompletion) {
		this.abnormalCompletion = abnormalCompletion;
	}


	public String getAbnormalCompletionCause() {
		return abnormalCompletionCause;
	}


	public void setAbnormalCompletionCause(String abnormalCompletionCause) {
		this.abnormalCompletionCause = abnormalCompletionCause;
	}


	public Instant getEnd() {
		return end;
	}


	public void setEnd(Instant end) {
		this.end = end;
	}


	public Integer getTotalProcessedRecords() {
		return totalProcessedRecords;
	}


	public void setTotalProcessedRecords(Integer totalProcessedRecords) {
		this.totalProcessedRecords = totalProcessedRecords;
	}

	
   
}
