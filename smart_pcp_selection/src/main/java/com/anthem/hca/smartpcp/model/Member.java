package com.anthem.hca.smartpcp.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;



public class Member implements Serializable{

	@NotNull(message="Market should be present")
	private String market;
	private String lob;
	private String product;
	//@Pattern(regexp = "[N|R|n|r]")
	private String assignmentType;
	private String assignmentMethod;
	private String hcid;
	private String sorCode;
	private String subscriberId;
	private String sequenceNbr;
	private String fname;
	private String lname;
	private String gender;
	private Date dob;
	private Address address;
	@Pattern(regexp = "[0-9]+",message = "SSN should be numeric")
	private String ssn;
	private String certNbr;
	private String memberCode;
	private String productCode;

	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getLob() {
		return lob;
	}
	public void setLob(String lob) {
		this.lob = lob;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getAssignmentType() {
		return assignmentType;
	}
	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}
	public String getAssignmentMethod() {
		return assignmentMethod;
	}
	public void setAssignmentMethod(String assignmentMethod) {
		this.assignmentMethod = assignmentMethod;
	}
	public String getHcid() {
		return hcid;
	}
	public void setHcid(String hcid) {
		this.hcid = hcid;
	}
	public String getSorCode() {
		return sorCode;
	}
	public void setSorCode(String sorCode) {
		this.sorCode = sorCode;
	}
	public String getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
	public String getSequenceNbr() {
		return sequenceNbr;
	}
	public void setSequenceNbr(String sequenceNbr) {
		this.sequenceNbr = sequenceNbr;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getCertNbr() {
		return certNbr;
	}
	public void setCertNbr(String certNbr) {
		this.certNbr = certNbr;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	// copy constructor required for Default rule invocation
	/*public Member(Member that) {
		this.market = that.market;
		this.lob = that.lob;
		this.product = that.product;
		this.assignmentType = that.assignmentType;
		this.assignmentMethod = that.assignmentMethod;
		this.hcid = that.hcid;
		this.sorCode = that.sorCode;
		this.subscriberId = that.subscriberId;
		this.sequenceNbr = that.sequenceNbr;
		this.fname = that.fname;
		this.lname = that.lname;
		this.gender = that.gender;
		//this.dob = new Date(that.dob);
		this.dob = that.dob;
		this.ssn = that.ssn;
		this.certNbr = that.certNbr;
		this.memberCode = that.memberCode;
		this.productCode = that.productCode;
	}
*/
}
