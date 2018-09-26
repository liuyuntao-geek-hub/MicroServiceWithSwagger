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
	private String accNewPatientFlag;
	private Double drivingDistance;
	private Double aerialDistance;
	private Double distance;
	private String taxId;
	private String phoneNumber;
	private Integer tierLvl;
	private int pcpRank;
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
	 * @return the accNewPatientFlag
	 */
	public String getAccNewPatientFlag() {
		return accNewPatientFlag;
	}
	/**
	 * @param accNewPatientFlag the accNewPatientFlag to set
	 */
	public void setAccNewPatientFlag(String accNewPatientFlag) {
		this.accNewPatientFlag = accNewPatientFlag;
	}
	/**
	 * @return the drivingDistance
	 */
	public Double getDrivingDistance() {
		return drivingDistance;
	}
	/**
	 * @param drivingDistance the drivingDistance to set
	 */
	public void setDrivingDistance(Double drivingDistance) {
		this.drivingDistance = drivingDistance;
	}
	/**
	 * @return the aerialDistance
	 */
	public Double getAerialDistance() {
		return aerialDistance;
	}
	/**
	 * @param aerialDistance the aerialDistance to set
	 */
	public void setAerialDistance(Double aerialDistance) {
		this.aerialDistance = aerialDistance;
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
	 * @return the tierLvl
	 */
	public Integer getTierLvl() {
		return tierLvl;
	}
	/**
	 * @param tierLvl the tierLvl to set
	 */
	public void setTierLvl(Integer tierLvl) {
		this.tierLvl = tierLvl;
	}
	/**
	 * @return the pcpRank
	 */
	public int getPcpRank() {
		return pcpRank;
	}
	/**
	 * @param pcpRank the pcpRank to set
	 */
	public void setPcpRank(int pcpRank) {
		this.pcpRank = pcpRank;
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
	

	//private String rgnlNtwkId;

	
	
	
}