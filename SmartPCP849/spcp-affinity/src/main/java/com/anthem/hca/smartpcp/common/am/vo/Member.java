package com.anthem.hca.smartpcp.common.am.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.validator.OrNotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 *				Member is used for Member payload information. 
 * 
 * *@author AF65409
 */
@OrNotBlank(orNotBlankProperties= {"memberContractCode","memberNetworkId"})
@ApiModel(value="Member",description="Contains request information received by spcp-select microservice") 
public class Member {

	@NotBlank(message = ErrorMessages.MISSING_INVOCATION_SYSTEM)
	@Size(max = 2, message = ErrorMessages.INVALID_INVOCATION_SYSTEM )
	@Pattern(regexp = "[0-9]*", message = ErrorMessages.INVALID_INVOCATION_SYSTEM)
	@ApiModelProperty(required = true, dataType = "String", notes = "invocationSystem contains the invoking system code, must have a length of 2 characters and regexp = [0-9]*")
	private String invocationSystem;

	@NotBlank(message = ErrorMessages.MISSING_SYSTEM_TYPE)
	@Size(max = 1, message = ErrorMessages.INVALID_SYSTEM_TYPE )
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_SYSTEM_TYPE)
	@ApiModelProperty(required = true, dataType = "String", notes = "systemType contains the invoking system type(Online or Batch), must have a length of 1 characters and regexp = [a-z-A-Z]*")
	private String systemType;

	@NotBlank(message = ErrorMessages.MISSING_MBR_EID)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_MBR_EID)
	@Size(max = 9, message = ErrorMessages.INVALID_MBR_EID)
	@ApiModelProperty(required = false, dataType = "String", notes = "memberEid contains the member EID, must have a length of max 9 characters and regexp = [a-z-A-Z-0-9]*")
	private String memberEid;

	@NotBlank(message = ErrorMessages.MISSING_MBR_TYPE)
	@Size(max = 1, message = ErrorMessages.INVALID_MBR_TYPE )
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_TYPE)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberType contains the member type(New or Existing), must have a length of 1 character and regexp = [a-z-A-Z]*")
	private String memberType;

	@NotBlank(message = ErrorMessages.MISSING_MBR_LOB)
	@Size(max = 8, message = ErrorMessages.INVALID_MBR_LOB )
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_MBR_LOB)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberLineOfBusiness contains the member LOB, must have a length of 8 characters and regexp = [a-z-A-Z-0-9]*")
	private String memberLineOfBusiness;

	@NotBlank(message = ErrorMessages.MISSING_MBR_PROCS_STATE)
	@Size(max = 2, message = ErrorMessages.INVALID_MBR_PROCS_STATE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_PROCS_STATE)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberProcessingState contains the state to which member belongs to, must have a length of 2 characters and regexp = [a-z-A-Z]*")
	private String memberProcessingState;

	@ApiModelProperty(notes = "memberContractCode contains the contract code information of the member for non west regions")
	private List<String> memberContractCode;

	@ApiModelProperty(notes = "memberNetworkId contains the network Id information of the member for west regions")
	private List<String> memberNetworkId;

	@NotNull(message = ErrorMessages.MISSING_ADRS)
	@Valid
	@ApiModelProperty(required = true, notes = "address contains the address information of the member")
	private Address address;

	@NotBlank(message = ErrorMessages.MISSING_DOB)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_DOB)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberDob contains date of birth of the member in format YYYY-MM-dd and regexp = [1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])")
	private String memberDob;

	@NotBlank(message = ErrorMessages.MISSING_GENDER)
	@Pattern(regexp = "[M|F|m|f]", message = ErrorMessages.INVALID_GENDER)
	@Size(max = 1, message = ErrorMessages.INVALID_GENDER)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberGender contains the gender of the member, must have a length of 1 character and regexp = [M|F]")
	private String memberGender;

	@NotBlank(message = ErrorMessages.MISSING_MBR_SEQUENCE_NBR)
	@Pattern(regexp = "(?!0+$)[0-9]*", message = ErrorMessages.INVALID_MBR_SEQUENCE_NBR)
	@Size(max = 3,  message = ErrorMessages.INVALID_MBR_SEQUENCE_NBR)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberSequenceNumber contains the sequence number of the member, must have a length of 3 characters and regexp = [0-9]*")
	private String memberSequenceNumber;

	@NotBlank(message = ErrorMessages.MISSING_MBR_FIRST_NAME)
	@Size(max = 40, message = ErrorMessages.INVALID_MBR_FIRST_NAME)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberFirstName contains the first name of the member and must have a length of 40 characters")
	private String memberFirstName;

	@Size(max = 10, message = ErrorMessages.INVALID_ROLLOVER_PCPID)
	@ApiModelProperty(dataType = "String", notes = "rollOverPcpId contains the rollover pcp Id in case of rollover and must have a length of 10 characters")
	private String rollOverPcpId;

	@NotBlank(message = ErrorMessages.MISSING_MBR_EFFECTIVE_DATE)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_EFFECTIVE_DATE)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberEffectiveDate contains the effective date of the member  in format YYYY-MM-dd and regexp = [1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])")
	private String memberEffectiveDate;

	@NotBlank(message = ErrorMessages.MISSING_PRODUCT_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_PRODUCT_TYPE)
	@Size(max = 10,  message = ErrorMessages.INVALID_PRODUCT_TYPE)
	@ApiModelProperty(required = true, dataType = "String", notes = "memberProductType contains the product type of member and must have a length of 10 characters")
	private String memberProductType;

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

	/**
	 * @return the memberEid
	 */
	public String getMemberEid() {
		return memberEid;
	}

	/**
	 * @param memberEid the memberEid to set
	 */
	public void setMemberEid(String memberEid) {
		this.memberEid = memberEid;
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
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
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
	 * @return the memberSequenceNumber
	 */
	public String getMemberSequenceNumber() {
		return memberSequenceNumber;
	}

	/**
	 * @param memberSequenceNumber the memberSequenceNumber to set
	 */
	public void setMemberSequenceNumber(String memberSequenceNumber) {
		this.memberSequenceNumber = memberSequenceNumber;
	}
	/**
	 * @return the memberFirstName
	 */
	public String getMemberFirstName() {
		return memberFirstName;
	}

	/**
	 * @param memberFirstName the memberFirstName to set
	 */
	public void setMemberFirstName(String memberFirstName) {
		this.memberFirstName = memberFirstName;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[invocationSystem=" + invocationSystem + ", systemType=" + systemType + ", memberEid="
				+ memberEid + ", memberType=" + memberType + ", memberLineOfBusiness=" + memberLineOfBusiness
				+ ", memberProcessingState=" + memberProcessingState + ", memberContractCode=" + memberContractCode
				+ ", memberNetworkId=" + memberNetworkId + ", address=" + address + ", memberDob=" + memberDob
				+ ", memberGender=" + memberGender + ", memberSequenceNumber=" + memberSequenceNumber
				+ ", memberFirstName=" + memberFirstName + ", rollOverPcpId=" + rollOverPcpId + ", memberEffectiveDate="
				+ memberEffectiveDate + ", memberProductType=" + memberProductType + "]";
	}

}
