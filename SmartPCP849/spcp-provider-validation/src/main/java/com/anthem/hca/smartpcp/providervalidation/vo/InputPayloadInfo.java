/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="InputPayloadInfo",description="Contains Input payload  info received from Affinity and MDO microservices")
public class InputPayloadInfo {

	@NotNull(message = "Member input Should not be null")
	@Valid
	@ApiModelProperty(required=true, notes="Contains the Member payload details") 
	private Member member;

	@NotNull(message = "Rules input should not be null")
	@Valid
	@ApiModelProperty(required=true, notes="Contains the Rules payload details")
	private Rules rules;

	@ApiModelProperty(required=true, notes="Contains the PCP list details")
	private List<PCP> pcpInfo;
	

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
	}

	public List<PCP> getPcpInfo() {
		return pcpInfo;
	}

	public void setPcpInfo(List<PCP> pcpInfo) {
		this.pcpInfo = pcpInfo;
	}

	
}
