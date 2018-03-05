package com.anthem.smartpcp.member.model;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Lob;
import java.io.Serializable;

@Entity
@Table(name="MBR", schema="SMARTPCP_DEMO")
public class MBR implements Serializable {

	private static final long serialVersionUID = -280714464843205153L;

	@Id
    @Column(name = "MBR_KEY", nullable = false)
    long key;

    @Column(name = "MBR_ACTIVE")
    boolean active;

    @Column(name = "MBR_CODE")
    String code;

    @Column(name = "MBR_DOB")
    java.sql.Date dob;

    @Column(name = "MBR_XCODE")
    java.math.BigDecimal xcode;

    @Column(name = "MBR_YCODE")
    double ycode;

    @Column(name = "MBR_ZCODE")
    java.math.BigDecimal zcode;

    @Column(name = "MBR_LAST_NAME")
    String lname;

    @Column(name = "MBR_FIRST_NAME")
    String fname;

    @Column(name = "MBR_SUBSCRIBER_ID")
    String subs;

    @Column(name = "MBR_SEQUENCE_NUMBER")
    int sequence;

    //@Lob
    @Column(name = "MBR_HEALTH_CARD_ID")
    String card;

    @Column(name = "MBR_ACTIVE_FROM")
    java.sql.Timestamp activefrm;

    @Lob
    @Column(name = "DUMMY_BLOB")
    byte[] dblob;

    //@Lob
    @Column(name = "DUMMY_CLOB")
    String dclob;

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public java.sql.Date getDob() {
		return dob;
	}

	public void setDob(java.sql.Date dob) {
		this.dob = dob;
	}

	public java.math.BigDecimal getXcode() {
		return xcode;
	}

	public void setXcode(java.math.BigDecimal xcode) {
		this.xcode = xcode;
	}

	public double getYcode() {
		return ycode;
	}

	public void setYcode(double ycode) {
		this.ycode = ycode;
	}

	public java.math.BigDecimal getZcode() {
		return zcode;
	}

	public void setZcode(java.math.BigDecimal zcode) {
		this.zcode = zcode;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getSubs() {
		return subs;
	}

	public void setSubs(String subs) {
		this.subs = subs;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public java.sql.Timestamp getActivefrm() {
		return activefrm;
	}

	public void setActivefrm(java.sql.Timestamp activefrm) {
		this.activefrm = activefrm;
	}

	public byte[] getDblob() {
		return dblob;
	}

	public void setDblob(byte[] dblob) {
		this.dblob = dblob;
	}

	public String getDclob() {
		return dclob;
	}

	public void setDclob(String dclob) {
		this.dclob = dclob;
	}

}
