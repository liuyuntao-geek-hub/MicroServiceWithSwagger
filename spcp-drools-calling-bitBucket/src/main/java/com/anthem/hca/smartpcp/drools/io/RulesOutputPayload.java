package com.anthem.hca.smartpcp.drools.io;

public class RulesOutputPayload {

	private int responseCode;
	private String responseMessage;

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
