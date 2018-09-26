package com.anthem.hca.smartpcp.affinity.model;

import com.anthem.hca.smartpcp.common.am.vo.PCP;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ProviderValidationOutPayload is used for PCP Info payload information 
 * 
 *@author AF65409
 */

public class ProviderValidationOutPayload {

	private PCP pcpInfo;
	private String responseCode;
	private String responseMessage;

	/**
	 * @return the pcpInfo
	 */
	public PCP getPcpInfo() {
		return pcpInfo;
	}
	/**
	 * @param pcpInfo the pcpInfo to set
	 */
	public void setPcpInfo(PCP pcpInfo) {
		this.pcpInfo = pcpInfo;
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
		return "ProviderValidationOutPayload [pcpInfo=" + pcpInfo + ", responseCode=" + responseCode
				+ ", responseMessage=" + responseMessage + "]";
	}

}