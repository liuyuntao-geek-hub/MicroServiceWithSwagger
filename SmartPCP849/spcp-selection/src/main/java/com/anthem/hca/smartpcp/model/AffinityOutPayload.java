package com.anthem.hca.smartpcp.model;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description -  POJO class containing output attributes Affinity.
 * 
 * 
 * @author AF71111
 */
public class AffinityOutPayload {

	private String pcpId;
	private String rgnlNtwrkId;
	private String responseCode;
	private String responseMessage;
	private Double drivingDistance;

	/**
	 * @return the pcpId
	 */
	public String getPcpId() {
		return pcpId;
	}

	/**
	 * @param pcpId
	 *            the pcpId to set
	 */
	public void setPcpId(String pcpId) {
		this.pcpId = pcpId;
	}

	/**
	 * @return the rgnlNtwrkId
	 */
	public String getRgnlNtwrkId() {
		return rgnlNtwrkId;
	}

	/**
	 * @param rgnlNtwrkId
	 *            the rgnlNtwrkId to set
	 */
	public void setRgnlNtwrkId(String rgnlNtwrkId) {
		this.rgnlNtwrkId = rgnlNtwrkId;
	}

	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
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

	/**
	 * @return the drivingDistance
	 */
	public Double getDrivingDistance() {
		return drivingDistance;
	}

	/**
	 * @param drivingDistance
	 *            the drivingDistance to set
	 */
	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}

}