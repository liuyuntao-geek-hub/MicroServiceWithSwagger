package com.anthem.hca.smartpcp.track.audit.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="GetScoringExcelPayload",description="Contains request information to fetch PCP scoring details")
public class GetScoringExcelPayload {

	@ApiModelProperty(required = true, dataType = "String", notes = "traceId is the transaction tracking id generated from spcp-select, must have a length between 16-25 characters and regexp = [0-9]*")
	@NotBlank(message = ErrorMessages.MISSING_TRACE_ID)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_TRACE_ID)
	@Size(min=16,max = 25, message = ErrorMessages.INVALID_TRACE_ID)
	private String traceId;

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	
}
