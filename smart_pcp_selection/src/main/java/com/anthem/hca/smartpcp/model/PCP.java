package com.anthem.hca.smartpcp.model;

import java.util.Date;

public class PCP {

	private String tin;
	private String npi;
	private String pcpId;
	private Address address;
	private String network;
	private Date contractEffDate;
	private String speciality;
	private String language;
	
	//output paramters
	private int minAge;
	private int maxAge;
	private String acceptedGender;
	private String allowableValue;
	private float distance;
	
	
	public String getTin() {
		return tin;
	}
	public void setTin(String tin) {
		this.tin = tin;
	}
	public String getNpi() {
		return npi;
	}
	public void setNpi(String npi) {
		this.npi = npi;
	}
	public String getPcpId() {
		return pcpId;
	}
	public void setPcpId(String pcpId) {
		this.pcpId = pcpId;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public Date getContractEffDate() {
		return contractEffDate;
	}
	public void setContractEffDate(Date contractEffDate) {
		this.contractEffDate = contractEffDate;
	}
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getMinAge() {
		return minAge;
	}
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}
	public int getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
	public String getAcceptedGender() {
		return acceptedGender;
	}
	public void setAcceptedGender(String acceptedGender) {
		this.acceptedGender = acceptedGender;
	}
	public String getAllowableValue() {
		return allowableValue;
	}
	public void setAllowableValue(String allowableValue) {
		this.allowableValue = allowableValue;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	
}
