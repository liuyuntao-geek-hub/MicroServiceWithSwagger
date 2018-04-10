package com.anthem.hca.smartpcp.drools.model;


import java.sql.Timestamp;

/**
 * POJO class for flow audit table 
 *
 */
public class TransactionFlowPayload {
	private String transactionId;
	private String serviceName;
	private String operationStatus;
	private String operationOutput;
	private String responseCode;
	private String responseMessage;
	private Timestamp serviceRequestTimestamp;
	
	public Timestamp getServiceRequestTimestamp() {
		return serviceRequestTimestamp;
	}
	public void setServiceRequestTimestamp(Timestamp serviceRequestTimestamp) {
		this.serviceRequestTimestamp = serviceRequestTimestamp;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getOperationStatus() {
		return operationStatus;
	}
	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}
	public String getOperationOutput() {
		return operationOutput;
	}
	public void setOperationOutput(String operationOutput) {
		this.operationOutput = operationOutput;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
