package com.anthem.hca.smartpcp.track.audit.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="PcpAssignmentInfoPayload",description="Contains request information required to fetch PCP assignment details")
public class PcpAssignmentPayload {

	@ApiModelProperty(required = true, dataType = "String", notes = "traceId is the transaction tracking id generated from spcp-select, must have a length between 16-25 characters and regexp = [0-9]*")
	@NotBlank(message = ErrorMessages.MISSING_TRACE_ID)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_TRACE_ID)
	@Size(min=16,max = 25, message = ErrorMessages.INVALID_TRACE_ID)
	private String traceId;
	
	@NotBlank(message = ErrorMessages.MISSING_PCP_ASSIGN_DATE)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_PCP_ASSIGN_DATE)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains pcp assignment date for the member in format YYYY-MM-dd")
	private String pcpAssignmentDate;

	/**
	 * @return the traceId
	 */
	public String getTraceId() {
		return traceId;
	}

	/**
	 * @param traceId the traceId to set
	 */
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	/**
	 * @return the pcpAssignmentDate
	 */
	public String getPcpAssignmentDate() {
		return pcpAssignmentDate;
	}

	/**
	 * @param pcpAssignmentDate the pcpAssignmentDate to set
	 */
	public void setPcpAssignmentDate(String pcpAssignmentDate) {
		this.pcpAssignmentDate = pcpAssignmentDate;
	}
	
	
}
