/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.model;

public class TransactionFlowPayload {

	private String traceId;
	private String serviceName;
	private String operationStatus;
	private String operationOutput;
	private int responseCode;
	private String responseMessage;

	/**
	 * @return the traceId
	 */
	public String getTraceId() {
		return traceId;
	}

	/**
	 * @param traceId
	 *            the traceId to set
	 */
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the operationStatus
	 */
	public String getOperationStatus() {
		return operationStatus;
	}

	/**
	 * @param operationStatus
	 *            the operationStatus to set
	 */
	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}

	/**
	 * @return the operationOutput
	 */
	public String getOperationOutput() {
		return operationOutput;
	}

	/**
	 * @param operationOutput
	 *            the operationOutput to set
	 */
	public void setOperationOutput(String operationOutput) {
		this.operationOutput = operationOutput;
	}

	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the responseMessage
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * @param responseMessage
	 *            the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

}
