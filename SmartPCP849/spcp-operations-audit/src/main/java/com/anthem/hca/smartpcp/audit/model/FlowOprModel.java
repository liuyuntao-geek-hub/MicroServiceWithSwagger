package com.anthem.hca.smartpcp.audit.model;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 *
 * 				FlowOprPayload is used for Flow Operation Payload for Transaction table.
 * 
 * @author AF56159 
 */

public class FlowOprModel {

	private String traceId;
	private String serviceName;
	private String operationStatus;
	private String operationOutput;
	private String responseCode;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operationOutput == null) ? 0 : operationOutput.hashCode());
		result = prime * result + ((operationStatus == null) ? 0 : operationStatus.hashCode());
		result = prime * result + ((responseCode == null) ? 0 : responseCode.hashCode());
		result = prime * result + ((responseMessage == null) ? 0 : responseMessage.hashCode());
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		result = prime * result + ((traceId == null) ? 0 : traceId.hashCode());
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
		FlowOprModel other = (FlowOprModel) obj;
		if (operationOutput == null) {
			if (other.operationOutput != null)
				return false;
		} else if (!operationOutput.equals(other.operationOutput))
			return false;
		
		return getResult(other);
	}
	
	public boolean getResult(FlowOprModel other){
		if (operationStatus == null) {
			if (other.operationStatus != null)
				return false;
		} else if (!operationStatus.equals(other.operationStatus))
			return false;
		if (responseCode == null) {
			if (other.responseCode != null)
				return false;
		} else if (!responseCode.equals(other.responseCode))
			return false;
		if (responseMessage == null) {
			if (other.responseMessage != null)
				return false;
		} else if (!responseMessage.equals(other.responseMessage))
			return false;
		
		return getSubResult(other);
	}
	
	public boolean getSubResult(FlowOprModel other){
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		if (traceId == null) {
			if (other.traceId != null)
				return false;
		} else if (!traceId.equals(other.traceId))
			return false;
		return true;
	}
}
