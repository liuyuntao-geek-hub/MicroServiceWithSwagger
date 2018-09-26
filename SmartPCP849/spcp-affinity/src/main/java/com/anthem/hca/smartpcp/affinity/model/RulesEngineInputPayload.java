package com.anthem.hca.smartpcp.affinity.model;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 *			RulesEngineInputPayload is used for RulesEngineInputPayload payload information 
 * 
 *@author AF65409 
 */

public class RulesEngineInputPayload {

	private String invocationSystem;
	private String memberLineOfBusiness;
	private String memberProcessingState;
	private String memberProductType;
	private String memberType;
	private String systemType;

	/**
	 * @return the invocationSystem
	 */
	public String getInvocationSystem() {
		return invocationSystem;
	}
	/**
	 * @param invocationSystem the invocationSystem to set
	 */
	public void setInvocationSystem(String invocationSystem) {
		this.invocationSystem = invocationSystem;
	}
	/**
	 * @return the memberLineOfBusiness
	 */
	public String getMemberLineOfBusiness() {
		return memberLineOfBusiness;
	}
	/**
	 * @param memberLineOfBusiness the memberLineOfBusiness to set
	 */
	public void setMemberLineOfBusiness(String memberLineOfBusiness) {
		this.memberLineOfBusiness = memberLineOfBusiness;
	}
	/**
	 * @return the memberProcessingState
	 */
	public String getMemberProcessingState() {
		return memberProcessingState;
	}
	/**
	 * @param memberProcessingState the memberProcessingState to set
	 */
	public void setMemberProcessingState(String memberProcessingState) {
		this.memberProcessingState = memberProcessingState;
	}
	/**
	 * @return the memberProductType
	 */
	public String getMemberProductType() {
		return memberProductType;
	}
	/**
	 * @param memberProductType the memberProductType to set
	 */
	public void setMemberProductType(String memberProductType) {
		this.memberProductType = memberProductType;
	}
	/**
	 * @return the memberType
	 */
	public String getMemberType() {
		return memberType;
	}
	/**
	 * @param memberType the memberType to set
	 */
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
	/**
	 * @return the systemType
	 */
	public String getSystemType() {
		return systemType;
	}
	/**
	 * @param systemType the systemType to set
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RulesEngineInputPayload [invocationSystem=" + invocationSystem + ", memberLineOfBusiness="
				+ memberLineOfBusiness + ", memberProcessingState=" + memberProcessingState + ", memberProductType="
				+ memberProductType + ", memberType=" + memberType + ", systemType=" + systemType + "]";
	}
}

