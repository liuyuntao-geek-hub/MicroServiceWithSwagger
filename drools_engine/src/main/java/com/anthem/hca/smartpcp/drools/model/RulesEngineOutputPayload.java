package com.anthem.hca.smartpcp.drools.model;

/**
 * POJO class for output payload rules  
 *
 */
public class RulesEngineOutputPayload {

	private Rules rules;
	private int responseCode;
	private String responseMessage;

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String message) {
		this.responseMessage = message;
	}

}
