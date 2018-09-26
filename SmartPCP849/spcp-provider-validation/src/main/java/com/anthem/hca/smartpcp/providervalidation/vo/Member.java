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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.providervalidation.constants.ErrorMessages;
import com.anthem.hca.smartpcp.providervalidation.validator.ORNotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="Member",description="Contains request information received by spcp-select microservice") 
@ORNotBlank(orNotBlankProperties= {"memberContractCode","memberNetworkId"})
public class Member {

	@ApiModelProperty(notes = "Contains the contract code information of the member for non west regions")
	private List<String> memberContractCode;

	@ApiModelProperty(notes = "Contains the network Id information of the member for west regions")
	private List<String> memberNetworkId; 
	
	@NotBlank(message = ErrorMessages.MISSING_MBR_DOB)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_DOB)
	@ApiModelProperty(required = true, notes = "Contains date of birth of the member in format YYYY-MM-dd")
	private String memberDob;
	
	@NotBlank(message = ErrorMessages.MISSING_MEMBER_GENDER)
	@Size(max = 1, message = ErrorMessages.INVALID_MEMBER_GENDER)
	@Pattern(regexp = "[M|F|m|f|O|o]", message =  ErrorMessages.INVALID_MEMBER_GENDER)
	@ApiModelProperty(dataType="String", notes = "Contains the gender of the member, it can be [M|F|m|f|O|o] and can be max of 1 character")
	private String memberGender;

	@ApiModelProperty(dataType="String", notes="Contains the rollover pcp Id in case of rollover")
	private String rollOverPcpId;

	@NotBlank(message = ErrorMessages.MISSING_MBR_EFFECTIVE_DATE)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_EFFECTIVE_DATE)
	@ApiModelProperty(required = true, notes = "Effective date of the member  in format YYYY-MM-dd")
	private String memberEffectiveDate;
	
	/**
	 * @return the memberContractCode
	 */
	public List<String> getMemberContractCode() {
		return memberContractCode;
	}

	/**
	 * @param memberContractCode the memberContractCode to set
	 */
	public void setMemberContractCode(List<String> memberContractCode) {
		this.memberContractCode = memberContractCode;
	}

	/**
	 * @return the memberNetworkId
	 */
	public List<String> getMemberNetworkId() {
		return memberNetworkId;
	}

	/**
	 * @param memberNetworkId the memberNetworkId to set
	 */
	public void setMemberNetworkId(List<String> memberNetworkId) {
		this.memberNetworkId = memberNetworkId;
	}

	/**
	 * @return the memberDob
	 */
	public String getMemberDob() {
		return memberDob;
	}

	/**
	 * @param memberDob the memberDob to set
	 */
	public void setMemberDob(String memberDob) {
		this.memberDob = memberDob;
	}

	/**
	 * @return the memberGender
	 */
	public String getMemberGender() {
		return memberGender;
	}

	/**
	 * @param memberGender the memberGender to set
	 */
	public void setMemberGender(String memberGender) {
		this.memberGender = memberGender;
	}

	
	/**
	 * @return the rollOverPcpId
	 */
	public String getRollOverPcpId() {
		return rollOverPcpId;
	}

	/**
	 * @param rollOverPcpId the rollOverPcpId to set
	 */
	public void setRollOverPcpId(String rollOverPcpId) {
		this.rollOverPcpId = rollOverPcpId;
	}

	/**
	 * @return the memberEffectiveDate
	 */
	public String getMemberEffectiveDate() {
		return memberEffectiveDate;
	}

	/**
	 * @param memberEffectiveDate the memberEffectiveDate to set
	 */
	public void setMemberEffectiveDate(String memberEffectiveDate) {
		this.memberEffectiveDate = memberEffectiveDate;
	}
	
}


