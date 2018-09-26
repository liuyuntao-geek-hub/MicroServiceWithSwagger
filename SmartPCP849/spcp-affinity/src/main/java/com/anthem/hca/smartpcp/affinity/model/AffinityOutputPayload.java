package com.anthem.hca.smartpcp.affinity.model;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				AffinityOutPayload is used for Affinity output payload information.
 * 
 * *@author AF65409 
 */
public class AffinityOutputPayload {

	private String pcpId;
	private String rgnlNtwrkId;
	double drivingDistance;
	private String responseCode;
	private String responseMessage;

	/**
	 * @return the pcpId
	 */
	public String getPcpId() {
		return pcpId;
	}
	/**
	 * @param pcpId the pcpId to set
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
	 * @param rgnlNtwrkId the rgnlNtwrkId to set
	 */
	public void setRgnlNtwrkId(String rgnlNtwrkId) {
		this.rgnlNtwrkId = rgnlNtwrkId;
	}
	/**
	 * @return the drivingDistance
	 */
	public double getDrivingDistance() {
		return drivingDistance;
	}
	/**
	 * @param drivingDistance the drivingDistance to set
	 */
	public void setDrivingDistance(double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}
	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
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
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[pcpId=" + pcpId + ", rgnlNtwrkId=" + rgnlNtwrkId + ", drivingDistance="
				+ drivingDistance + ", responseCode=" + responseCode + ", responseMessage=" + responseMessage + "]";
	}

}