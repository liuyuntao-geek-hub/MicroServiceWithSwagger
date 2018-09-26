package com.anthem.hca.smartpcp.model;

import java.util.Date;
import java.util.List;



/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		PCP is used for to create PCP Payload for Provider information. 
 * 
 * @author Khushbu Jain AF65409 
 */
public class Provider {

	private String provPcpId;
	private String pcpPmgIpa;
	private String rgnlNtwkId;
	private String firstName;
	private String middleName;
	private String lastName;
	private Address address;
	private List<String> speciality;
	private List<String> specialityDesc;
	private Date grpgRltdPadrsTrmntnDt;
	private Date grpgRltdPadrsEfctvDt;
	private Integer maxMbrCnt;
	private Integer curntMbrCnt;
	private String acoPcpReturned;
	
	private Double distance;
	private String taxId;
	private String phoneNumber;
	private  int rank;
	private String memberMcid;
	private List<String> pcpLang;
	private boolean dummyFlag;
	
	
	
	/**
	 * @return the provPcpId
	 */
	public String getProvPcpId() {
		return provPcpId;
	}



	/**
	 * @param provPcpId the provPcpId to set
	 */
	public void setProvPcpId(String provPcpId) {
		this.provPcpId = provPcpId;
	}



	/**
	 * @return the pcpPmgIpa
	 */
	public String getPcpPmgIpa() {
		return pcpPmgIpa;
	}



	/**
	 * @param pcpPmgIpa the pcpPmgIpa to set
	 */
	public void setPcpPmgIpa(String pcpPmgIpa) {
		this.pcpPmgIpa = pcpPmgIpa;
	}



	/**
	 * @return the rgnlNtwkId
	 */
	public String getRgnlNtwkId() {
		return rgnlNtwkId;
	}



	/**
	 * @param rgnlNtwkId the rgnlNtwkId to set
	 */
	public void setRgnlNtwkId(String rgnlNtwkId) {
		this.rgnlNtwkId = rgnlNtwkId;
	}



	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}



	/**
	 * @param firstName the firstName to set
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
	 * @param middleName the middleName to set
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
	 * @param lastName the lastName to set
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
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}



	/**
	 * @return the speciality
	 */
	public List<String> getSpeciality() {
		return speciality;
	}



	/**
	 * @param speciality the speciality to set
	 */
	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
	}



	/**
	 * @return the specialityDesc
	 */
	public List<String> getSpecialityDesc() {
		return specialityDesc;
	}



	/**
	 * @param specialityDesc the specialityDesc to set
	 */
	public void setSpecialityDesc(List<String> specialityDesc) {
		this.specialityDesc = specialityDesc;
	}



	/**
	 * @return the grpgRltdPadrsTrmntnDt
	 */
	public Date getGrpgRltdPadrsTrmntnDt() {
		return grpgRltdPadrsTrmntnDt;
	}



	/**
	 * @param grpgRltdPadrsTrmntnDt the grpgRltdPadrsTrmntnDt to set
	 */
	public void setGrpgRltdPadrsTrmntnDt(Date grpgRltdPadrsTrmntnDt) {
		this.grpgRltdPadrsTrmntnDt = grpgRltdPadrsTrmntnDt;
	}



	/**
	 * @return the grpgRltdPadrsEfctvDt
	 */
	public Date getGrpgRltdPadrsEfctvDt() {
		return grpgRltdPadrsEfctvDt;
	}



	/**
	 * @param grpgRltdPadrsEfctvDt the grpgRltdPadrsEfctvDt to set
	 */
	public void setGrpgRltdPadrsEfctvDt(Date grpgRltdPadrsEfctvDt) {
		this.grpgRltdPadrsEfctvDt = grpgRltdPadrsEfctvDt;
	}



	/**
	 * @return the maxMbrCnt
	 */
	public Integer getMaxMbrCnt() {
		return maxMbrCnt;
	}



	/**
	 * @param maxMbrCnt the maxMbrCnt to set
	 */
	public void setMaxMbrCnt(Integer maxMbrCnt) {
		this.maxMbrCnt = maxMbrCnt;
	}



	/**
	 * @return the curntMbrCnt
	 */
	public Integer getCurntMbrCnt() {
		return curntMbrCnt;
	}



	/**
	 * @param curntMbrCnt the curntMbrCnt to set
	 */
	public void setCurntMbrCnt(Integer curntMbrCnt) {
		this.curntMbrCnt = curntMbrCnt;
	}



	/**
	 * @return the acoPcpReturned
	 */
	public String getAcoPcpReturned() {
		return acoPcpReturned;
	}



	/**
	 * @param acoPcpReturned the acoPcpReturned to set
	 */
	public void setAcoPcpReturned(String acoPcpReturned) {
		this.acoPcpReturned = acoPcpReturned;
	}



	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}



	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
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
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}



	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}



	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}



	/**
	 * @return the memberMcid
	 */
	public String getMemberMcid() {
		return memberMcid;
	}



	/**
	 * @param memberMcid the memberMcid to set
	 */
	public void setMemberMcid(String memberMcid) {
		this.memberMcid = memberMcid;
	}



	/**
	 * @return the pcpLang
	 */
	public List<String> getPcpLang() {
		return pcpLang;
	}



	/**
	 * @param pcpLang the pcpLang to set
	 */
	public void setPcpLang(List<String> pcpLang) {
		this.pcpLang = pcpLang;
	}



	/**
	 * @return the dummyFlag
	 */
	public boolean isDummyFlag() {
		return dummyFlag;
	}



	/**
	 * @param dummyFlag the dummyFlag to set
	 */
	public void setDummyFlag(boolean dummyFlag) {
		this.dummyFlag = dummyFlag;
	}



	@Override
	public String toString() {
			return "(provPcpId=" + provPcpId + ", rank="+rank+", rgnlNtwkId=" + rgnlNtwkId + ", Distance=" + distance+")";
	}	
}