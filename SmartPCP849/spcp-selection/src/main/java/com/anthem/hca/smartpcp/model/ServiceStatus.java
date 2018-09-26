package com.anthem.hca.smartpcp.model;

import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes required for status(hard errors) in response.
 * 
 * 
 * @author AF71111
 */
public class ServiceStatus {

	@ApiModelProperty(dataType = "String", notes = "Contains the status  of the service")
	private String status;

	@ApiModelProperty(dataType = "String", notes = "Contains the error code of the service")
	private String errorCode;

	@ApiModelProperty(dataType = "String", notes = "Contains the error text of the service")
	private String errorText;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorText
	 */
	public String getErrorText() {
		return errorText;
	}

	/**
	 * @param errorText
	 *            the errorText to set
	 */
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

}
