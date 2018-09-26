package com.anthem.hca.smartpcp.model;

import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes of response.
 * 
 * 
 * @author AF71111
 */
public class OutputPayload {

	@ApiModelProperty(notes = "Contains the status information")
	private Status status;

	@ApiModelProperty(notes = "Contains the reporting level information")
	private Reporting reporting;

	@ApiModelProperty(notes = "Contains the member information")
	private MemberOutput member;

	@ApiModelProperty(notes = "Contains the assigned PCP information")
	private PCP provider;
	
	@ApiModelProperty(notes = "Contains the transaction ID")
	private String smartPCPTraceID;

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the reporting
	 */
	public Reporting getReporting() {
		return reporting;
	}

	/**
	 * @param reporting
	 *            the reporting to set
	 */
	public void setReporting(Reporting reporting) {
		this.reporting = reporting;
	}

	/**
	 * @return the member
	 */
	public MemberOutput getMember() {
		return member;
	}

	/**
	 * @param member
	 *            the member to set
	 */
	public void setMember(MemberOutput member) {
		this.member = member;
	}

	/**
	 * @return the provider
	 */
	public PCP getProvider() {
		return provider;
	}

	/**
	 * @param provider
	 *            the provider to set
	 */
	public void setProvider(PCP provider) {
		this.provider = provider;
	}

	/**
	 * @return the smartPCPTraceID
	 */
	public String getSmartPCPTraceID() {
		return smartPCPTraceID;
	}

	/**
	 * @param smartPCPTraceID the smartPCPTraceID to set
	 */
	public void setSmartPCPTraceID(String smartPCPTraceID) {
		this.smartPCPTraceID = smartPCPTraceID;
	}

}
