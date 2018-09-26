package com.anthem.hca.smartpcp.mdoscoring.vo;

public class OutputPayloadInfo {

	private String provPcpId;
	private Double drivingDistance;
	private int mdoScore;
	private String rgnlNtwkId;
	private String responseCode;
	private String responseMessage;

	public String getProvPcpId() {
		return provPcpId;
	}

	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}

	public Double getDrivingDistance() {
		return drivingDistance;
	}

	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}

	public int getMdoScore() {
		return mdoScore;
	}

	public void setMdoScore(int mdoScore) {
		this.mdoScore = mdoScore;
	}

	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}

	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

}
