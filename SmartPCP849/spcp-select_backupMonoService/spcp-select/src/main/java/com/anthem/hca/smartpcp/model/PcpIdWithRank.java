package com.anthem.hca.smartpcp.model;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		PCPIdWithRank is used for payload information for PCPId with PIMS Ranking. 
 * 
 * @author Khushbu Jain AF65409  
 */
public class PcpIdWithRank {

	private String mcid;
	private String tin;
	private String npi;
	private String pcpId;
	private String pcpRank;

	/**
	 * @return the mcid
	 */
	public String getMcid() {
		return mcid;
	}
	/**
	 * @param mcid the mcid to set
	 */
	public void setMcid(String mcid) {
		this.mcid = mcid;
	}
	/**
	 * @return the tin
	 */
	public String getTin() {
		return tin;
	}
	/**
	 * @param tin the tin to set
	 */
	public void setTin(String tin) {
		this.tin = tin;
	}
	/**
	 * @return the npi
	 */
	public String getNpi() {
		return npi;
	}
	/**
	 * @param npi the npi to set
	 */
	public void setNpi(String npi) {
		this.npi = npi;
	}
	/**
	 * @return the pcpId
	 */
	public String getPcpId() {
		return pcpId;
	}
	/**
	 * @param pcpId the pcpId to set
	 */
	public void setPcpId(String pcpId) {
		this.pcpId = pcpId;
	}
	/**
	 * @return the pcpRank
	 */
	public String getPcpRank() {
		return pcpRank;
	}
	/**
	 * @param pcpRank the pcpRank to set
	 */
	public void setPcpRank(String pcpRank) {
		this.pcpRank = pcpRank;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "(pcpId=" + pcpId + ", tin=" + tin + ", npi=" + npi + ", pcpRank="
				+ pcpRank + ")";
	}
}