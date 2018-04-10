package com.anthem.hca.smartpcp.model;

public class OutputPayload {

	private PCP pcp;
	private Integer responseCode;
	private String responseMessage;
	
	public PCP getPcp() {
		return pcp;
	}
	public void setPcp(PCP pcp) {
		this.pcp = pcp;
	}
	public Integer getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
}
