package com.anthem.hca.smartpcp.drools.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

public class Member implements Serializable {

	private static final long serialVersionUID = -6795195531617991805L;
	
	//attributes for member payload
	@NotNull(message="market should not be null")
	private String market;
	@NotNull(message="lob should not be null")
	private String lob;
	@NotNull(message="product should not be null")
	private String product;
	@NotNull(message="asssignment type should not be null")
	private String assignmentType;
	@NotNull(message="assignment method should not be null")
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

}
