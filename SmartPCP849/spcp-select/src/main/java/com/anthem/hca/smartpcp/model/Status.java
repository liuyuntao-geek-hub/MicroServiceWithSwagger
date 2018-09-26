package com.anthem.hca.smartpcp.model;

import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes required for transaction status in response.
 * 
 * 
 * @author AF71111
 */
public class Status {

	@ApiModelProperty(notes = "Contains the transaction level status")
	private TransactionStatus transaction;

	@ApiModelProperty(notes = "Contains the service level status")
	private ServiceStatus service;

	@ApiModelProperty(notes = "Conatins the total number of errors")
	private int totalErrors;

	/**
	 * @return the transaction
	 */
	public TransactionStatus getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction
	 *            the transaction to set
	 */
	public void setTransaction(TransactionStatus transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the service
	 */
	public ServiceStatus getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(ServiceStatus service) {
		this.service = service;
	}

	/**
	 * @return the totalErrors
	 */
	public int getTotalErrors() {
		return totalErrors;
	}

	/**
	 * @param totalErrors
	 *            the totalErrors to set
	 */
	public void setTotalErrors(int totalErrors) {
		this.totalErrors = totalErrors;
	}

}
