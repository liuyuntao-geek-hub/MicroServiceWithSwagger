package com.anthem.hca.smartpcp.drools.io;

/**
 * The TransactionFlowPayload class encapsulates the Input Payload required for
 * Audit Control flow table to insert data to the same. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class TransactionFlowPayload {

	private String traceId;
	private String serviceName;
	private String operationStatus;
	private String operationOutput;
	private String responseCode;
	private String responseMessage;

	/**
	 * This method is used to get the Trace Id from the Transaction Flow Payload.
	 * 
	 * @param  None
	 * @return The Trace Id
	 */
	public String getTraceId() {
		return traceId;
	}

	/**
	 * This method is used to set the Trace Id in the Transaction Flow Payload.
	 * 
	 * @param  traceId The Trace Id
	 * @return None
	 */
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	/**
	 * This method is used to get the Service Name from the Transaction Flow Payload.
	 * 
	 * @param  None
	 * @return The Service Name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * This method is used to set the Service Name in the Transaction Flow Payload.
	 * 
	 * @param  serviceName The Service Name
	 * @return None
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * This method is used to get the Operation Status from the Transaction Flow Payload.
	 * 
	 * @param  None
	 * @return The Operation Status
	 */
	public String getOperationStatus() {
		return operationStatus;
	}

	/**
	 * This method is used to set the Operation Status in the Transaction Flow Payload.
	 * 
	 * @param  operationStatus The Operation Status
	 * @return None
	 */
	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}

	/**
	 * This method is used to get the Operation Output from the Transaction Flow Payload.
	 * 
	 * @param  None
	 * @return The Operation Output
	 */
	public String getOperationOutput() {
		return operationOutput;
	}

	/**
	 * This method is used to set the Operation Output in the Transaction Flow Payload.
	 * 
	 * @param  operationOutput The Operation Output
	 * @return None
	 */
	public void setOperationOutput(String operationOutput) {
		this.operationOutput = operationOutput;
	}

	/**
	 * This method is used to get the Response Code from the Transaction Flow Payload.
	 * 
	 * @param  None
	 * @return The Response Code
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * This method is used to set the Response Code in the Transaction Flow Payload.
	 * 
	 * @param  responseCode The Response Code
	 * @return None
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * This method is used to get the Response Message from the Transaction Flow Payload.
	 * 
	 * @param  None
	 * @return The Response Message
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * This method is used to set the Response Message in the Transaction Flow Payload.
	 * 
	 * @param  responseMessage The Response Message
	 * @return None
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

}
