/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - POJO class that contains the  attribute for Member
 * 
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.mdoprocessing.utils.ErrorMessages;
import com.anthem.hca.smartpcp.mdoprocessing.validator.ORNotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



@ORNotBlank(orNotBlankProperties = { "memberContractCode", "memberNetworkId" })
@ApiModel(value = "Member", description = "Contains member request information received by spcp-mdo-pool microservice")
public class Member {
	
	@NotBlank(message = ErrorMessages.MISSING_INVOCATION_SYSTEM)
	@Size(max = 2, message = ErrorMessages.INVALID_INVOCATION_SYSTEM)
	@Pattern(regexp = "[0-9]*", message = ErrorMessages.INVALID_INVOCATION_SYSTEM)
	@ApiModelProperty(required = true, notes = "Contains the invoking system, length max of 2 characters, regex = [0-9]*")
	private String invocationSystem;

	@NotBlank(message = ErrorMessages.MISSING_SYSTEM_TYPE)
	@Size(max = 1, message = ErrorMessages.INVALID_SYSTEM_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_SYSTEM_TYPE)
	@ApiModelProperty(required = true, notes = "Invoking System Type Online or Batch, length max of 1 character, regex = [a-z-A-Z]*")
	private String systemType;

	@NotBlank(message = ErrorMessages.MISSING_MBR_TYPE)
	@Size(max = 1, message = ErrorMessages.INVALID_MBR_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_TYPE)
	@ApiModelProperty(required = true, notes = "Contains Member Type New or Existing, length max of 1 character, regex = [a-z-A-Z]*")
	private String memberType;

	@NotBlank(message = ErrorMessages.MISSING_MBR_LOB)
	@Size(max = 8, message = ErrorMessages.INVALID_MBR_LOB)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_MBR_LOB)
	@ApiModelProperty(required = true, notes = "Contains Member LOB,length max of 8 characters, regex = [a-z-A-Z-0-9]*")
	private String memberLineOfBusiness;

	@NotBlank(message = ErrorMessages.MISSING_MBR_PROCS_STATE)
	@Size(max = 2, message = ErrorMessages.INVALID_MBR_PROCS_STATE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_PROCS_STATE)
	@ApiModelProperty(required = true, notes = "State to which member belongs to, length max of 2 characters, regex = [a-z-A-Z]*")
	private String memberProcessingState;

	@ApiModelProperty(notes = "Contains the contract code information of the member for non west regions")
	private List<String> memberContractCode;

	@ApiModelProperty(notes = "Contains the network Id information of the member for west regions")
	private List<String> memberNetworkId;

	@NotNull(message = ErrorMessages.MISSING_ADRS)
	@Valid
	@ApiModelProperty(required = true, notes = "Contains the address of the member, must contain latitude and longitude for MDO pool")
	private Address address;

	@NotBlank(message = ErrorMessages.MISSING_MBR_DOB)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_DOB)
	@ApiModelProperty(required = true, notes = "Contains date of birth of the member in format YYYY-MM-dd")
	private String memberDob;

	@NotBlank(message = ErrorMessages.MISSING_MEMBER_GENDER)
	@Size(max = 1, message = ErrorMessages.INVALID_MEMBER_GENDER)
	@Pattern(regexp = "[M|F|m|f]", message =  ErrorMessages.INVALID_MEMBER_GENDER)
	@ApiModelProperty( notes = "Contains the gender of the member, it can be [M|F|m|f|O|o] and can be max of 1 character")
	private String memberGender;
	
	@ApiModelProperty(required = true, notes = "Contains the Language code of member")
	private List<String> memberLanguageCode ;

	@NotBlank(message = ErrorMessages.MISSING_PRODUCT_TYPE)
	@Size(max = 10,  message = ErrorMessages.INVALID_PRODUCT_TYPE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_PRODUCT_TYPE)
	@ApiModelProperty(required = true, notes = "Contains product type of member and length must be max of 10 characters")
	private String memberProductType;

	@Size(max = 1, message = ErrorMessages.INVALID_MBR_PREGNANCY_INDICATOR)
	@ApiModelProperty(notes = "Indication if the member is pregnant(Y) or not(N) and must be max of 1 character")
	private String memberPregnancyIndicator;

	@NotBlank(message = ErrorMessages.MISSING_MBR_EFFECTIVE_DATE)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_EFFECTIVE_DATE)
	@ApiModelProperty(required = true, notes = "Effective date of the member  in format YYYY-MM-dd")
	private String memberEffectiveDate;
	
	@Size(max = 10, message = ErrorMessages.INVALID_ROLLOVER_PCPID)
	@ApiModelProperty(dataType = "String", notes = "Contains the rollover pcp Id in case of rollover")
	private String rollOverPcpId;
	
	/**
	 * @return the invocationSystem
	 */
	public String getInvocationSystem() {
		return invocationSystem;
	}

	/**
	 * @param invocationSystem
	 *            the invocationSystem to set
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
	 * @param systemType
	 *            the systemType to set
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	/**
	 * @return the memberType
	 */
	public String getMemberType() {
		return memberType;
	}

	/**
	 * @param memberType
	 *            the memberType to set
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
	 * @param memberLineOfBusiness
	 *            the memberLineOfBusiness to set
	 */
	public void setMemberLineOfBusiness(String memberLineOfBusiness) {
		this.memberLineOfBusiness = memberLineOfBusiness;
	}
	
	/**
	 * @return the memberLanguageCode
	 */
	public List<String> getMemberLanguageCode() {
		return memberLanguageCode;
	}

	/**
	 * @param memberLanguageCode the memberLanguageCode to set
	 */
	public void setMemberLanguageCode(List<String> memberLanguageCode) {
		this.memberLanguageCode = memberLanguageCode;
	}

	/**
	 * @return the memberProcessingState
	 */
	public String getMemberProcessingState() {
		return memberProcessingState;
	}

	/**
	 * @param memberProcessingState
	 *            the memberProcessingState to set
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
	 * @param memberContractCode
	 *            the memberContractCode to set
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
	 * @param memberNetworkId
	 *            the memberNetworkId to set
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
	 * @param address
	 *            the address to set
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
	 * @param memberDob
	 *            the memberDob to set
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
	 * @param memberGender
	 *            the memberGender to set
	 */
	public void setMemberGender(String memberGender) {
		this.memberGender = memberGender;
	}

	/**
	 * @return the memberProductType
	 */
	public String getMemberProductType() {
		return memberProductType;
	}

	/**
	 * @param memberProductType
	 *            the memberProductType to set
	 */
	public void setMemberProductType(String memberProductType) {
		this.memberProductType = memberProductType;
	}

	/**
	 * @return the memberPregnancyIndicator
	 */
	public String getMemberPregnancyIndicator() {
		return memberPregnancyIndicator;
	}

	/**
	 * @param memberPregnancyIndicator
	 *            the memberPregnancyIndicator to set
	 */
	public void setMemberPregnancyIndicator(String memberPregnancyIndicator) {
		this.memberPregnancyIndicator = memberPregnancyIndicator;
	}

	/**
	 * @return the memberEffectiveDate
	 */
	public String getMemberEffectiveDate() {
		return memberEffectiveDate;
	}

	/**
	 * @param memberEffectiveDate
	 *            the memberEffectiveDate to set
	 */
	public void setMemberEffectiveDate(String memberEffectiveDate) {
		this.memberEffectiveDate = memberEffectiveDate;
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
	
}