package com.anthem.hca.smartpcp.affinity.model;

import java.util.List;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 *
 * 				PcpidListOutputPayload is used for PCPID With PIMS Ranking List payload information 
 * 
 *@author AF65409 
 */

public class PcpidListOutputPayload {

	private List<PcpIdWithRank> pcpIdWithRankList;
	private String responseCode;
	private String responseMessage;

	/**
	 * @return the pcpIdWithRankList
	 */
	public List<PcpIdWithRank> getPcpIdWithRankList() {
		return pcpIdWithRankList;
	}
	/**
	 * @param pcpIdWithRankList the pcpIdWithRankList to set
	 */
	public void setPcpIdWithRankList(List<PcpIdWithRank> pcpIdWithRankList) {
		this.pcpIdWithRankList = pcpIdWithRankList;
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
		return "PcpidListOutputPayload [pcpIdWithRankList=" + pcpIdWithRankList + ", responseCode=" + responseCode
				+ ", responseMessage=" + responseMessage + "]";
	}

}