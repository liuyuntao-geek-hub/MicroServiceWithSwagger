package com.anthem.hca.smartpcp.drools.io;

public class TransactionFlowPayload {

	private String traceId;
	private String serviceName;
	private String operationStatus;
	private String operationOutput;
	private String responseCode;
	private String responseMessage;

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
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
