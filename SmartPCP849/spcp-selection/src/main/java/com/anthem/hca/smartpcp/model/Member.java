package com.anthem.hca.smartpcp.model;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.validator.IBlankGroup;
import com.anthem.hca.smartpcp.validator.IORNotBlank;
import com.anthem.hca.smartpcp.validator.IRegexGroup;
import com.anthem.hca.smartpcp.validator.ISizeGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes of Member
 * 
 * 
 * @author AF71111
 */
@IORNotBlank(orNotBlankProperties = { "memberContractCode", "memberNetworkId" })
@ApiModel(value = "Member", description = "Contains request information received by spcp-select microservice")
public class Member {
	
	@NotBlank(message = ErrorMessages.MISSING_INVOCATION_SYSTEM,groups=IBlankGroup.class)
	@Size(max = 2, message = ErrorMessages.INVALID_INVOCATION_SYSTEM,groups=ISizeGroup.class)
	@Pattern(regexp = "[0-9]*", message = ErrorMessages.INVALID_INVOCATION_SYSTEM,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains the invoking system code,max size = 2 and regular exp as [0-9]*")
	private String invocationSystem;

	@NotBlank(message = ErrorMessages.MISSING_SYSTEM_TYPE,groups=IBlankGroup.class)
	@Size(max = 1, message = ErrorMessages.INVALID_SYSTEM_TYPE,groups=ISizeGroup.class)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_SYSTEM_TYPE,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Invoking System Type (Online or Batch),max size = 1 and regular exp as [a-z-A-Z]*")
	private String systemType;

	@NotBlank(message = ErrorMessages.MISSING_REQUEST_TYPE,groups=IBlankGroup.class)
	@Size(max = 1, message = ErrorMessages.INVALID_REQUEST_TYPE,groups=ISizeGroup.class)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_REQUEST_TYPE,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "PCP Request Type (Single or List of PCP),max size = 1 and regular exp as [a-z-A-Z]*")
	private String requestType;

	@Size(max = 9, message = ErrorMessages.INVALID_MBR_EID,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains Member ID,max size = 9")
	private String memberEid;

	@NotBlank(message = ErrorMessages.MISSING_MBR_TYPE,groups=IBlankGroup.class)
	@Size(max = 1, message = ErrorMessages.INVALID_MBR_TYPE,groups=ISizeGroup.class)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_MBR_TYPE,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains Member Type(New or Existing),max size = 1 and regular exp as [a-z-A-Z]*")
	private String memberType;

	@NotBlank(message = ErrorMessages.MISSING_MBR_LOB,groups=IBlankGroup.class)
	@Size(max = 8, message = ErrorMessages.INVALID_MBR_LOB,groups=ISizeGroup.class)
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = ErrorMessages.INVALID_MBR_LOB,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains Member LOB,max size = 8 and regular exp as [a-z-A-Z-0-9]*")
	private String memberLineOfBusiness;

	@Size(max = 2, message = ErrorMessages.INVALID_MBR_PROCS_STATE,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Processing state of the member,max size = 2")
	private String memberProcessingState;

	@ApiModelProperty(notes = "Contains the contract code information of the member for non west regions")
	private List<String> memberContractCode;

	@ApiModelProperty(notes = "Contains the network Id information of the member for west regions")
	private List<String> memberNetworkId;

	@NotNull(message = ErrorMessages.MISSING_ADRS)
	@Valid
	@ApiModelProperty(required = true, notes = "Contains the address information of the member")
	private Address address;

	@NotBlank(message = ErrorMessages.MISSING_MBR_DOB,groups=IBlankGroup.class)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_DOB,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains date of birth of the member in format YYYY-MM-dd")
	private String memberDob;
	
	@NotBlank(message = ErrorMessages.MISSING_MEMBER_GENDER,groups=IBlankGroup.class)
	@Size(max = 1, message = ErrorMessages.INVALID_MEMBER_GENDER,groups=ISizeGroup.class)
	@Pattern(regexp = "[M|F|m|f]", message =  ErrorMessages.INVALID_MEMBER_GENDER,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains the gender of the member,max size = 1")
	private String memberGender;

	@ApiModelProperty(required = true, notes = "Contains the gender of the member")
	private List<String> memberLanguageCode = Arrays.asList(Constants.ENGLISH);

	@Size(max = 5, message = ErrorMessages.INVALID_MBR_SOURCE_SYSTEM,groups=ISizeGroup.class)
	@ApiModelProperty( dataType = "String", notes = "Contains the source system(WGS or ISG),,max size = 5")
	private String memberSourceSystem;

	@NotBlank(message = ErrorMessages.MISSING_MBR_PRODUCT,groups=IBlankGroup.class)
	@Size(max = 10, message = ErrorMessages.INVALID_MBR_PRODUCT,groups=ISizeGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains product of member,max size = 10")
	private String memberProduct;

	@Size(max = 10, message = ErrorMessages.INVALID_PRODUCT_TYPE,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains product type of member,max size = 10")
	private String memberProductType;

	@Size(max = 3, message = ErrorMessages.INVALID_MBR_SEQUENCE_NBR,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains the sequence number of the member,max size = 3")
	private String memberSequenceNumber;

	@Size(max = 40, message = ErrorMessages.INVALID_MBR_FIRST_NAME,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains the first name of the member,max size = 40")
	private String memberFirstName;

	@Size(max = 1, message = ErrorMessages.INVALID_MBR_MIDDLE_NAME,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains the middle name of the member,max size = 1")
	private String memberMiddleName;

	@Size(max = 40, message = ErrorMessages.INVALID_MBR_LAST_NAME,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains the last name of the member,max size = 40")
	private String memberLastName;

	@Size(max = 10, message = ErrorMessages.INVALID_ROLLOVER_PCPID,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Contains the rollover pcp Id in case of rollover,max size = 10")
	private String rollOverPcpId;

	@Size(max = 1, message = ErrorMessages.INVALID_MBR_PREGNANCY_INDICATOR,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Indication if the member is pregnant or not,max size = 1")
	private String memberPregnancyIndicator;

	@NotBlank(message = ErrorMessages.MISSING_MBR_EFFECTIVE_DATE,groups=IBlankGroup.class)
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_EFFECTIVE_DATE,groups=IRegexGroup.class)
	@ApiModelProperty(required = true, dataType = "String", notes = "Effective date of the member  in format YYYY-MM-dd")
	private String memberEffectiveDate;

	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = ErrorMessages.INVALID_MBR_TERMINATION_DATE,groups=IRegexGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Termination date of the member  in format YYYY-MM-dd")
	private String memberTerminationDate;

	@Size(max = 1, message = ErrorMessages.INVALID_UPDATE_COUNTER,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "String", notes = "Indicator on whether to update the panel capacity for the assigned PCP")
	private String updateCounter;

	@Max(value = 99999999, message = ErrorMessages.INVALID_MBR_GROUP_ID,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "Integer", notes = "Contains the group ID of the member,max value = 99999999")
	private Integer memberGroupId;

	@Max(value = 9999, message = ErrorMessages.INVALID_MBR_SUB_GROUP_ID,groups=ISizeGroup.class)
	@ApiModelProperty(dataType = "Integer", notes = "Contains the subgroup ID of the member,max value = 9999")
	private Integer memberSubGroupId;

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
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType
	 *            the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the memberEid
	 */
	public String getMemberEid() {
		return memberEid;
	}

	/**
	 * @param memberEid
	 *            the memberEid to set
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
	 * @return the memberLanguageCode
	 */
	public List<String> getMemberLanguageCode() {
		return memberLanguageCode;
	}

	/**
	 * @param memberLanguageCode
	 *            the memberLanguageCode to set
	 */
	public void setMemberLanguageCode(List<String> memberLanguageCode) {
		if (null != memberLanguageCode && !memberLanguageCode.isEmpty()) {
			this.memberLanguageCode = memberLanguageCode;
		}
	}

	/**
	 * @return the memberSourceSystem
	 */
	public String getMemberSourceSystem() {
		return memberSourceSystem;
	}

	/**
	 * @param memberSourceSystem
	 *            the memberSourceSystem to set
	 */
	public void setMemberSourceSystem(String memberSourceSystem) {
		this.memberSourceSystem = memberSourceSystem;
	}

	/**
	 * @return the memberProduct
	 */
	public String getMemberProduct() {
		return memberProduct;
	}

	/**
	 * @param memberProduct
	 *            the memberProduct to set
	 */
	public void setMemberProduct(String memberProduct) {
		this.memberProduct = memberProduct;
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
	 * @return the memberSequenceNumber
	 */
	public String getMemberSequenceNumber() {
		return memberSequenceNumber;
	}

	/**
	 * @param memberSequenceNumber
	 *            the memberSequenceNumber to set
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
	 * @param memberFirstName
	 *            the memberFirstName to set
	 */
	public void setMemberFirstName(String memberFirstName) {
		this.memberFirstName = memberFirstName;
	}

	/**
	 * @return the memberMiddleName
	 */
	public String getMemberMiddleName() {
		return memberMiddleName;
	}

	/**
	 * @param memberMiddleName
	 *            the memberMiddleName to set
	 */
	public void setMemberMiddleName(String memberMiddleName) {
		this.memberMiddleName = memberMiddleName;
	}

	/**
	 * @return the memberLastName
	 */
	public String getMemberLastName() {
		return memberLastName;
	}

	/**
	 * @param memberLastName
	 *            the memberLastName to set
	 */
	public void setMemberLastName(String memberLastName) {
		this.memberLastName = memberLastName;
	}

	/**
	 * @return the rollOverPcpId
	 */
	public String getRollOverPcpId() {
		return rollOverPcpId;
	}

	/**
	 * @param rollOverPcpId
	 *            the rollOverPcpId to set
	 */
	public void setRollOverPcpId(String rollOverPcpId) {
		this.rollOverPcpId = rollOverPcpId;
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
	 * @return the memberTerminationDate
	 */
	public String getMemberTerminationDate() {
		return memberTerminationDate;
	}

	/**
	 * @param memberTerminationDate
	 *            the memberTerminationDate to set
	 */
	public void setMemberTerminationDate(String memberTerminationDate) {
		this.memberTerminationDate = memberTerminationDate;
	}

	/**
	 * @return the updateCounter
	 */
	public String getUpdateCounter() {
		return updateCounter;
	}

	/**
	 * @param updateCounter
	 *            the updateCounter to set
	 */
	public void setUpdateCounter(String updateCounter) {
		this.updateCounter = updateCounter;
	}

	/**
	 * @return the memberGroupId
	 */
	public Integer getMemberGroupId() {
		return memberGroupId;
	}

	/**
	 * @param memberGroupId
	 *            the memberGroupId to set
	 */
	public void setMemberGroupId(Integer memberGroupId) {
		this.memberGroupId = memberGroupId;
	}

	/**
	 * @return the memberSubGroupId
	 */
	public Integer getMemberSubGroupId() {
		return memberSubGroupId;
	}

	/**
	 * @param memberSubGroupId
	 *            the memberSubGroupId to set
	 */
	public void setMemberSubGroupId(Integer memberSubGroupId) {
		this.memberSubGroupId = memberSubGroupId;
	}
}
