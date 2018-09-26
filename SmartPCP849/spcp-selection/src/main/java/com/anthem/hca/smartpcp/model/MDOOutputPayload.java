package com.anthem.hca.smartpcp.model;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing output attributes MDO.
 * 
 * 
 * @author AF71111
 */
public class MDOOutputPayload {

	private String pcpId;
	private Integer mdoScore;
	private Double drivingDistance;
	private String pcpNtwrkId;
	private int pcpRank;
	private boolean dummyFlag;
	private String responseCode;
	private String responseMessage;

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
	 * @return the mdoScore
	 */
	public Integer getMdoScore() {
		return mdoScore;
	}

	/**
	 * @param mdoScore
	 *            the mdoScore to set
	 */
	public void setMdoScore(Integer mdoScore) {
		this.mdoScore = mdoScore;
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

	/**
	 * @return the pcpNtwrkId
	 */
	public String getPcpNtwrkId() {
		return pcpNtwrkId;
	}

	/**
	 * @param pcpNtwrkId
	 *            the pcpNtwrkId to set
	 */
	public void setPcpNtwrkId(String pcpNtwrkId) {
		this.pcpNtwrkId = pcpNtwrkId;
	}

	/**
	 * @return the dummyFlag
	 */
	public boolean isDummyFlag() {
		return dummyFlag;
	}

	/**
	 * @param dummyFlag
	 *            the dummyFlag to set
	 */
	public void setDummyFlag(boolean dummyFlag) {
		this.dummyFlag = dummyFlag;
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
	 * @return the pcpRank
	 */
	public int getPcpRank() {
		return pcpRank;
	}

	/**
	 * @param pcpRank the pcpRank to set
	 */
	public void setPcpRank(int pcpRank) {
		this.pcpRank = pcpRank;
	}
	
}
