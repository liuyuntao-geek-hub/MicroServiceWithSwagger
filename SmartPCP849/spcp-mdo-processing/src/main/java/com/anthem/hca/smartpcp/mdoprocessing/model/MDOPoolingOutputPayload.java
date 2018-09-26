/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - Pojo class that contains the Output attribute for MDO pool service 
 * 
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;

import java.util.List;


public class MDOPoolingOutputPayload {

	private List<PCP> pcps;
	private String responseCode;
	private String responseMessage;
	private boolean dummyFlag;
	
	
	public List<PCP> getPcps() {
		return pcps;
	}
	public void setPcps(List<PCP> pcps) {
		this.pcps = pcps;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public boolean isDummyFlag() {
		return dummyFlag;
	}
	public void setDummyFlag(boolean dummyFlag) {
		this.dummyFlag = dummyFlag;
	}
	
	
	
}
