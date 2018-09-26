/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.common.am.vo;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.anthem.hca.smartpcp.mdo.pool.validator.ORNotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ORNotBlank(orNotBlankProperties = { "memberContractCode", "memberNetworkId" })
@ApiModel(value = "Member", description = "Contains member request information received by spcp-mdo-pool microservice")
public class Member {

	@NotBlank(message = "invocationSystem should not be null or empty")
	@Pattern(regexp = "[0-9]*", message = "invocationSystem has invalid characters")
	@ApiModelProperty(required = true, notes = "Contains the invoking system code")
	private String invocationSystem;

	@NotBlank(message = "SystemType should not be null or empty")
	@Pattern(regexp = "[a-z-A-Z]*", message = "SystemType has invalid characters")
	@ApiModelProperty(required = true, notes = "Invoking System Type (Online or Batch)")
	private String systemType;

	@ApiModelProperty(required = true, notes = "PCP Request Type (Single or List of PCP)")
	private String requestType;

	@ApiModelProperty(notes = "Contains Member ID")
	private String memberEid;

	@NotBlank(message = "memberType should not be null or empty")
	@Pattern(regexp = "[a-z-A-Z]*", message = "memberType has invalid characters")
	@ApiModelProperty(required = true, notes = "Contains Member Type(New or Existing)")
	private String memberType;

	@NotBlank(message = "memberLineOfBusiness should not be null or empty")
	@Pattern(regexp = "[a-z-A-Z-0-9]*", message = "memberLineOfBusiness has invalid characters")
	@ApiModelProperty(required = true, notes = "Contains Member LOB")
	private String memberLineOfBusiness;

	@NotBlank(message = "memberProcessingState should not be null or empty")
	@Pattern(regexp = "[a-z-A-Z]*", message = "memberProcessingState has invalid characters")
	@ApiModelProperty(required = true, notes = "State to which member belongs to")
	private String memberProcessingState;

	@ApiModelProperty(notes = "Contains the contract code information of the member for non west regions")
	private List<@NotBlank(message = "memberContractCode elements cannot be null or empty") @Pattern(regexp = "[a-z-A-Z-0-9]*", message = "memberContractCode elements have invalid characters") String> memberContractCode;

	@ApiModelProperty(notes = "Contains the network Id information of the member for west regions")
	private List<@NotBlank(message = "memberNetworkId elements cannot be null or empty") @Pattern(regexp = "[a-z-A-Z-0-9]*", message = "memberNetworkId elements have invalid characters") String> memberNetworkId;

	@NotNull(message = "address should be present")
	@Valid
	@ApiModelProperty(required = true, notes = "Contains the address information of the member")
	private Address address;

	@NotBlank(message = "memberDob should be present")
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = "memberDob should be in YYYY-MM-dd format")
	@ApiModelProperty(required = true, notes = "Contains date of birth of the member in format YYYY-MM-dd")
	private String memberDob;

	@NotBlank(message = "memberGender should not be null or empty")
	@Pattern(regexp = "[M|F|m|f|O|o]", message = "memberGender has invalid characters")
	@ApiModelProperty(required = true, notes = "Contains the gender of the member")
	private String memberGender;

	@ApiModelProperty(notes = "Contains list of member languages")
	private List<String> memberLanguageCode;

	@ApiModelProperty(notes = "Contains the source system(WGS or ISG)")
	private String memberSourceSystem;

	@ApiModelProperty(notes = "Contains product of member")
	private String memberProduct;

	@NotBlank(message = "memberProductType should not be null or empty")
	@ApiModelProperty(required = true, notes = "Contains product type of member")
	private String memberProductType;

	@ApiModelProperty(notes = "Contains the sequence number of the member")
	private String memberSequenceNumber;

	@ApiModelProperty(required = true, notes = "Contains the first name of the member")
	private String memberFirstName;

	@ApiModelProperty(required = true, notes = "Contains the middle name of the member")
	private String memberMiddleName;

	@ApiModelProperty(required = true, notes = "Contains the last name of the member")
	private String memberLastName;

	@ApiModelProperty(dataType = "String", notes = "Contains the rollover pcp Id in case of rollover")
	private String rollOverPcpId;

	@NotBlank(message = "memberPregnancyIndicator should not be null or empty")
	@Pattern(regexp = "[Y|N|y|n]", message = "memberPregnancyIndicator has invalid characters")
	@ApiModelProperty(required = true, notes = "Indication if the member is pregnant or not")
	private String memberPregnancyIndicator;

	@NotBlank(message = "memberEffectiveDate should be present")
	@Pattern(regexp = "[1-9]\\d\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = "memberEffectiveDate should be in YYYY-MM-dd format")
	@ApiModelProperty(required = true, notes = "Effective date of the member  in format YYYY-MM-dd")
	private String memberEffectiveDate;

	@ApiModelProperty(notes = "Termination date of the member  in format YYYY-MM-dd")
	private String memberTerminationDate;

	@ApiModelProperty(notes = "Indicator on whether to update the panel capacity for the assigned PCP")
	private String updateCounter;

	@ApiModelProperty(notes = "Contains the group ID of the member")
	private Integer memberGroupId;

	@ApiModelProperty(notes = "Contains the subgroup ID of the member")
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
		if ("O".equalsIgnoreCase(memberGender)) {
			memberGender = "M";
		}
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
		this.memberLanguageCode = memberLanguageCode;
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
