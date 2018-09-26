package com.anthem.hca.smartpcp.track.audit.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 *				PCPScoringDataPayload is used for getting details required to fetch PCP scoring details 
 * 
 * *@author AF54902
 */
@ApiModel(value="PCPScoringDataPayload",description="Contains request information received by spcp-mdo-scoring microservice to persist PCP scoring details")
public class PCPScoringDataPayload {
	@ApiModelProperty(required = true, dataType = "String", notes = "traceId is the transaction tracking id generated from spcp-select, must have a length between 16-25 characters and regexp = [0-9]*")
	@NotBlank(message = ErrorMessages.MISSING_TRACE_ID)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_TRACE_ID)
	@Size(min=16,max = 25, message = ErrorMessages.INVALID_TRACE_ID)
	private String traceId;
	@ApiModelProperty(required = true, dataType = "String", notes = "providerData contains the PCP scoring data in JSON format")
	@NotBlank(message = ErrorMessages.MISSING_PROVIDER_DATA)
	private String providerData;
	
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	public String getProviderData() {
		return providerData;
	}
	public void setProviderData(String providerData) {
		this.providerData = providerData;
	}	

}
