package com.anthem.hca.smartpcp.drools.model;

import javax.validation.constraints.NotNull;

public class Member {

	@NotNull(message = "memberLineOfBusiness should be present")
	private String memberLineOfBusiness;

	@NotNull(message = "memberSourceSystem should be present")
	private String memberSourceSystem;

	@NotNull(message = "memberProcessingState should be present")
	private String memberProcessingState;

	@NotNull(message = "memberISGProductGroup should be present")
	private String memberISGProductGroup;

	@NotNull(message = "memberWGSGroup should be present")
	private String memberWGSGroup;

	@NotNull(message = "memberType should be present")
	private String memberType;

	@NotNull(message = "systemType should be present")
	private String systemType;	

	public String getMemberLineOfBusiness() {
		return memberLineOfBusiness;
	}

	public void setMemberLineOfBusiness(String memberLineOfBusiness) {
		this.memberLineOfBusiness = memberLineOfBusiness.trim();
	}

	public String getMemberSourceSystem() {
		return memberSourceSystem;
	}

	public void setMemberSourceSystem(String memberSourceSystem) {
		this.memberSourceSystem = memberSourceSystem.trim();
	}

	public String getMemberProcessingState() {
		return memberProcessingState;
	}

	public void setMemberProcessingState(String memberProcessingState) {
		this.memberProcessingState = memberProcessingState.trim();
	}

	public String getMemberISGProductGroup() {
		return memberISGProductGroup;
	}

	public void setMemberISGProductGroup(String memberISGProductGroup) {
		this.memberISGProductGroup = memberISGProductGroup.trim();
	}

	public String getMemberWGSGroup() {
		return memberWGSGroup;
	}

	public void setMemberWGSGroup(String memberWGSGroup) {
		this.memberWGSGroup = memberWGSGroup.trim();
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType.trim();
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType.trim();
	}

}
