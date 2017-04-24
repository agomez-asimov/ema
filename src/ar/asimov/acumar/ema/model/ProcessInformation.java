package ar.asimov.acumar.ema.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity implementation class for Entity: ProcessInformation
 *
 */
@Entity

public class ProcessInformation implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public ProcessInformation() {
		super();
	}
	
	@Id
	private Long processId;
	@Column(name="processed_records")
	private Integer processedRecords;
	@ManyToOne
	@JoinColumn(name="station_id", referencedColumnName="station_id")
	private Station station;
	@Column(name="abnormal_termination")
	private Boolean abnormalTermination;
	@Column(name="abnormal_terminantion_cause")
	private String abnormalTemrminationCause;

	/**
	 * @return the processId
	 */
	public Long getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	/**
	 * @return the processedRecords
	 */
	public Integer getProcessedRecords() {
		return processedRecords;
	}
	/**
	 * @param processedRecords the processedRecords to set
	 */
	public void setProcessedRecords(Integer processedRecords) {
		this.processedRecords = processedRecords;
	}
	/**
	 * @return the station
	 */
	public Station getStation() {
		return station;
	}
	/**
	 * @param station the station to set
	 */
	public void setStation(Station station) {
		this.station = station;
	}
	/**
	 * @return the abnormalTermination
	 */
	public Boolean getAbnormalTermination() {
		return abnormalTermination;
	}
	/**
	 * @param abnormalTermination the abnormalTermination to set
	 */
	public void setAbnormalTermination(Boolean abnormalTermination) {
		this.abnormalTermination = abnormalTermination;
	}
	/**
	 * @return the abnormalTemrminationCause
	 */
	public String getAbnormalTemrminationCause() {
		return abnormalTemrminationCause;
	}
	/**
	 * @param abnormalTemrminationCause the abnormalTemrminationCause to set
	 */
	public void setAbnormalTemrminationCause(String abnormalTemrminationCause) {
		this.abnormalTemrminationCause = abnormalTemrminationCause;
	}	
   
}
