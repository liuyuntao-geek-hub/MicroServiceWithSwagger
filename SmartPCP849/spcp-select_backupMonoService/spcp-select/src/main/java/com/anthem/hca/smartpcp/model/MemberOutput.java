/**
 * 
 */
package com.anthem.hca.smartpcp.model;

import io.swagger.annotations.ApiModelProperty;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes of Member for output
 * 
 * 
 * @author AF71111
 */
public class MemberOutput {

	@ApiModelProperty(dataType = "Integer", notes = "Contains the group ID of the member")
	private Integer memberGroupId;

	@ApiModelProperty(dataType = "Integer", notes = "Contains the subgroup ID of the member")
	private Integer memberSubGroupId;

	@ApiModelProperty(dataType = "String", notes = "Contains Member ID")
	private String memberEid;

	@ApiModelProperty(dataType = "String", notes = "Contains the sequence number of the member")
	private String memberSequenceNumber;

	@ApiModelProperty(dataType = "MemberAddressOutput", notes = "Contains the address of the member")
	private Address address;

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

}
