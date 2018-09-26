package com.anthem.hca.smartpcp.affinity.model;

import java.util.List;

import com.anthem.hca.smartpcp.common.am.vo.PCP;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				ProviderPayload is used for PCPID List payload information 
 * 
 *@author AF65409 
 */

public class ProviderPayload {

	private List<PCP> providerPayloadList;
	private String responseCode;
	private String responseMessage;

	/**
	 * @return the providerPayloadList
	 */
	public List<PCP> getProviderPayloadList() {
		return providerPayloadList;
	}
	/**
	 * @param providerPayloadList the providerPayloadList to set
	 */
	public void setProviderPayloadList(List<PCP> providerPayloadList) {
		this.providerPayloadList = providerPayloadList;
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
		return "ProviderPayload [providerPayloadList=" + providerPayloadList + ", responseCode=" + responseCode
				+ ", responseMessage=" + responseMessage + "]";
	}
}