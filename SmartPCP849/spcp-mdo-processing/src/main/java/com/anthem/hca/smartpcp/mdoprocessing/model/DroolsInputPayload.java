/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class which contains input attributes to call Drools
 * 
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;

public class DroolsInputPayload{
	
	private String memberLineOfBusiness;
	
	private String memberProcessingState;
	
	private String memberProductType;

	private String memberType;
	
	private String systemType;

	public String getMemberLineOfBusiness() {
		return memberLineOfBusiness;
	}

	public void setMemberLineOfBusiness(String memberLineOfBusiness) {
		this.memberLineOfBusiness = memberLineOfBusiness;
	}

	public String getMemberProcessingState() {
		return memberProcessingState;
	}

	public void setMemberProcessingState(String memberProcessingState) {
		this.memberProcessingState = memberProcessingState;
	}

	public String getMemberProductType() {
		return memberProductType;
	}

	public void setMemberProductType(String memberProductType) {
		this.memberProductType = memberProductType;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	
}

