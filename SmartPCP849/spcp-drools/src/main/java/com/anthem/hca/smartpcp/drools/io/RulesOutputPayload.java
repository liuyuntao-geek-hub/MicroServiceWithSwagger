package com.anthem.hca.smartpcp.drools.io;

import io.swagger.annotations.ApiModelProperty;

/**
 * The RulesOutputPayload class encapsulates the base Output Payload object for Rules.
 * All the individual Output Payload classes extend this base class. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class RulesOutputPayload {

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Response Code of the Output Payload")
	private int responseCode;

	@ApiModelProperty(required = true, dataType = "String", notes = "Contains the Response Message of the Output Payload")
	private String responseMessage;

	/**
	 * This method is used to get the Response Code from the Output Payload.
	 * 
	 * @param  None
	 * @return The Response Code
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * This method is used to set the Response Code in the Output Payload.
	 * 
	 * @param  responseCode The Response Code
	 * @return None
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * This method is used to get the Response Message from the Output Payload.
	 * 
	 * @param  None
	 * @return The Response Message
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * This method is used to set the Response Message in the Output Payload.
	 * 
	 * @param  message The Response Message
	 * @return None
	 */
	public void setResponseMessage(String message) {
		this.responseMessage = message;
	}

}
