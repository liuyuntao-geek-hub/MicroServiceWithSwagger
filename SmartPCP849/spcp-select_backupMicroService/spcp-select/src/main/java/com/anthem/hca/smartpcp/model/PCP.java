package com.anthem.hca.smartpcp.model;

import io.swagger.annotations.ApiModelProperty;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes of pcp
 * 
 * 
 * @author AF71111
 */
public class PCP {

	@ApiModelProperty(dataType = "String", notes = "PCP ID of the assigned PCP")
	private String provPcpId;
	@ApiModelProperty(dataType = "int", notes = "Score of the assigned PCP in case of MDO")
	private int pcpScore;
	@ApiModelProperty(dataType = "int", notes = "Rank of the assigned PCP in case of MDO")
	private int pcpRank;
	@ApiModelProperty(dataType = "String", notes = "Type of the assigned PCP(PMG or IPA)")
	private String pcpPmgIpa;
	@ApiModelProperty(dataType = "String", notes = "contract code of the assigned PCP in case of non west regions")
	private String contractCode;
	@ApiModelProperty(dataType = "String", notes = "network ID of the assigned PCP in case of  west regions")
	private String networkId;
	@ApiModelProperty(dataType = "String", notes = "Contains first name of the assigned PCP")
	private String firstName;
	@ApiModelProperty(dataType = "String", notes = "Contains middle name of the assigned PCP")
	private String middleName;
	@ApiModelProperty(dataType = "String", notes = "Contains last name of the assigned PCP")
	private String lastName;
	@ApiModelProperty(notes = "Contains address information of the assigned PCP")
	private Address address;
	@ApiModelProperty(dataType = "String", notes = "Contains specialty of the assigned PCP")
	private String speciality;
	@ApiModelProperty(dataType = "String", notes = "Indicates whether assigned PCP is dummy or not")
	private String dummyPCP;
	@ApiModelProperty(dataType = "String", notes = "Contains PRTY_CD of the assigned PCP")
	private String acoPcpReturned;
	
	@ApiModelProperty(dataType = "String", notes = "Contains tax id of the assigned PCP")
	private String taxId;

	@ApiModelProperty(dataType = "String", notes = "Contains phone number of the assigned PCP")
	private String phoneNumber;

	/**
	 * @return the provPcpId
	 */
	public String getProvPcpId() {
		return provPcpId;
	}

	/**
	 * @param provPcpId
	 *            the provPcpId to set
	 */
	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}

	/**
	 * @return the pcpScore
	 */
	public int getPcpScore() {
		return pcpScore;
	}

	/**
	 * @param pcpScore
	 *            the pcpScore to set
	 */
	public void setPcpScore(int pcpScore) {
		this.pcpScore = pcpScore;
	}

	/**
	 * @return the pcpRank
	 */
	public int getPcpRank() {
		return pcpRank;
	}

	/**
	 * @param pcpRank
	 *            the pcpRank to set
	 */
	public void setPcpRank(int pcpRank) {
		this.pcpRank = pcpRank;
	}

	/**
	 * @return the pcpPmgIpa
	 */
	public String getPcpPmgIpa() {
		return pcpPmgIpa;
	}

	/**
	 * @param pcpPmgIpa
	 *            the pcpPmgIpa to set
	 */
	public void setPcpPmgIpa(String pcpPmgIpa) {
		this.pcpPmgIpa = pcpPmgIpa;
	}

	/**
	 * @return the contractCode
	 */
	public String getContractCode() {
		return contractCode;
	}

	/**
	 * @param contractCode
	 *            the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	/**
	 * @return the networkId
	 */
	public String getNetworkId() {
		return networkId;
	}

	/**
	 * @param networkId
	 *            the networkId to set
	 */
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName
	 *            the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	 * @return the speciality
	 */
	public String getSpeciality() {
		return speciality;
	}

	/**
	 * @param speciality
	 *            the speciality to set
	 */
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	/**
	 * @return the dummyPCP
	 */
	public String getDummyPCP() {
		return dummyPCP;
	}

	/**
	 * @param dummyPCP
	 *            the dummyPCP to set
	 */
	public void setDummyPCP(String dummyPCP) {
		this.dummyPCP = dummyPCP;
	}

	/**
	 * @return the acoPcpReturned
	 */
	public String getAcoPcpReturned() {
		return acoPcpReturned;
	}

	/**
	 * @param acoPcpReturned
	 *            the acoPcpReturned to set
	 */
	public void setAcoPcpReturned(String acoPcpReturned) {
		this.acoPcpReturned = acoPcpReturned;
	}

	/**
	 * @return the taxId
	 */
	public String getTaxId() {
		return taxId;
	}

	/**
	 * @param taxId the taxId to set
	 */
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
