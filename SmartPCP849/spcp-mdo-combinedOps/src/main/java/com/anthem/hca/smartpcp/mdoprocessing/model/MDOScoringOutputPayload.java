package com.anthem.hca.smartpcp.mdoprocessing.model;

public class MDOScoringOutputPayload {
	
	
    private String provPcpId;
    private Double drivingDistance;
    private int    mdoScore;
    private String rgnlNtwkId;
    private String responseCode;
    private String responseMessage;
    
	/**
	 * @return the provPcpId
	 */
	public String getProvPcpId() {
		return provPcpId;
	}
	/**
	 * @param provPcpId the provPcpId to set
	 */
	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}
	/**
	 * @return the drivingDistance
	 */
	public Double getDrivingDistance() {
		return drivingDistance;
	}
	/**
	 * @param drivingDistance the drivingDistance to set
	 */
	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}
	/**
	 * @return the mdoScore
	 */
	public int getMdoScore() {
		return mdoScore;
	}
	/**
	 * @param mdoScore the mdoScore to set
	 */
	public void setMdoScore(int mdoScore) {
		this.mdoScore = mdoScore;
	}
	/**
	 * @return the rgnlNtwkId
	 */
	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}
	/**
	 * @param rgnlNtwkId the rgnlNtwkId to set
	 */
	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
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

    
	
}
