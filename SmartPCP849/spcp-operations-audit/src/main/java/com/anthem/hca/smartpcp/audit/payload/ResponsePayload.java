package com.anthem.hca.smartpcp.audit.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				ResponsePayload is used for the response information.
 * 
 * *@author AF56159 
 */
@ApiModel(value="ResponsePayload",description="Contains response from spcp-operations-audit microservice")
public class ResponsePayload {
	
	@ApiModelProperty(required=true, dataType="String", notes="Response code for the consumer")
	private int responseCode;
	@ApiModelProperty(required=true, dataType="String", notes="Response message for the consumer")
	private String responseMessage;
	
	/**
	 * @return responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
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
	
	

}
