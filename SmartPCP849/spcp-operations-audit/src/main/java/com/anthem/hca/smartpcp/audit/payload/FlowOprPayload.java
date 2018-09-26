package com.anthem.hca.smartpcp.audit.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.audit.constants.ErrorMessages;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				FlowOprPayload is used for Operations flow information from other micro services.
 * 
 * *@author AF56159 
 */
@ApiModel(value="CommonRequestPayload",description="Request processing information of all microservices other than spcp-select")
public class FlowOprPayload {

	@NotBlank(message=ErrorMessages.MISSING_TRACE_ID)
	@Size(min=16,max=75,message=ErrorMessages.INVALID_TRACE_ID)
	@ApiModelProperty(required=true, dataType="String", notes="Trace Id from spcp-select microservice, length must be between 16 to 75")
	private String traceId;
	@NotBlank(message=ErrorMessages.MISSING_SERVICE_NAME)
	@Size(min=5,max=150,message=ErrorMessages.INVALID_SERVICE_NAME)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_SERVICE_NAME)
	@ApiModelProperty(required=true, dataType="String", notes="Invoker service name, length must be between 5 to 150")
	private String serviceName;
	@NotBlank(message=ErrorMessages.MISSING_OPERATION_STATUS)
	@Pattern(regexp = "SUCCESS|FAILURE",message=ErrorMessages.INVALID_OPERATION_STATUS)
	@ApiModelProperty(required=true, dataType="String", notes="Request processing status, regex=SUCCESS|FAILURE", allowableValues="[SUCCESS,FAILURE]")
	private String operationStatus;
	@Size(min=1,max=1500,message=ErrorMessages.INVALID_OPERATION_OUTPUT)
	@ApiModelProperty(required=false, dataType="String", notes="Output of request processing, length must be between 1 to 1500")
	private String operationOutput;
	@NotBlank(message=ErrorMessages.MISSING_RESPONSE_CODE)
	@Size(min=3,max=5,message=ErrorMessages.INVALID_RESPONSE_CODE)
	@ApiModelProperty(required=true, dataType="String", notes="Response code sent to consumer after request processing, length must be between 3 to 5")
	private String responseCode;
	@NotBlank(message=ErrorMessages.MISSING_RESPONSE_MESSAGE)
	@Size(min=5,max=500,message=ErrorMessages.INVALID_RESPONSE_MESSAGE)
	@ApiModelProperty(required=true, dataType="String", notes="Response message sent to consumer after request processing, length must be between 5 to 500")
	private String responseMessage;
	
	/**
	 * @return serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	/**
	 * @return operationStatus
	 */
	public String getOperationStatus() {
		return operationStatus;
	}
	/**
	 * @param operationStatus the operationStatus to set
	 */
	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}
	/**
	 * @return operationOutput
	 */
	public String getOperationOutput() {
		return operationOutput;
	}
	/**
	 * @param operationOutput the operationOutput to set
	 */
	public void setOperationOutput(String operationOutput) {
		this.operationOutput = operationOutput;
	}
	/**
	 * @return responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return responseMessage
	 */
	public String getResponseMessage() {
		return responseMessage;
	}
	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	@Override
	public String toString() {
		return "FlowOprPayload [traceId=" + traceId + ", serviceName=" + serviceName + ", operationStatus="
				+ operationStatus + ", operationOutput=" + operationOutput + ", responseCode=" + responseCode
				+ ", responseMessage=" + responseMessage + "]";
	}
	/**
	 * @return traceId
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

}
