/**
 * * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - POJO class that contains the  attribute for Operation Audit.
 *  
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;



public class TransactionFlowPayload {

	
	private String traceId;
	private String serviceName;
	private String operationStatus;
	private String operationOutput;
	private String responseCode;
	private String responseMessage;
	

	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName.trim();
	}
	public String getOperationStatus() {
		return operationStatus;
	}
	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus.trim();
	}
	public String getOperationOutput() {
		return operationOutput;
	}
	public void setOperationOutput(String operationOutput) {
		this.operationOutput = operationOutput.trim();
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode.trim();
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage.trim();
	}
	@Override
	public String toString() {
		return "FlowOprPayload [traceId=" + traceId + ", serviceName=" + serviceName + ", operationStatus="
				+ operationStatus + ", operationOutput=" + operationOutput + ", responseCode=" + responseCode
				+ ", responseMessage=" + responseMessage + "]";
	}
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId.trim();
	}

}
