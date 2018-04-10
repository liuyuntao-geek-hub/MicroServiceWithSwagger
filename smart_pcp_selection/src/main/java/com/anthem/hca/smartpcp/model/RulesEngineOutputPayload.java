package com.anthem.hca.smartpcp.model;


public class RulesEngineOutputPayload {

	private Rules rules;
	private Integer responseCode;
	private String responseMessage;
	

	public Rules getRules() {
		return rules;
	}
	public void setRules(Rules rules) {
		this.rules = rules;
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