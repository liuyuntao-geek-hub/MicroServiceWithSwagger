package com.anthem.hca.smartpcp.common.am.vo;

public class Address {

	private String memberStreet;
	private String memberStreet2;
	private String memberCity;
	private String memberState;
	private Integer memberZipCode;
	private Integer memberZipFour;
	private String memberCountyCode;
	private double latitude;
	private double longitude;

	/**
	 * @return the memberStreet
	 */
	public String getMemberStreet() {
		return memberStreet;
	}

	/**
	 * @param memberStreet
	 *            the memberStreet to set
	 */
	public void setMemberStreet(String memberStreet) {
		this.memberStreet = memberStreet;
	}

	/**
	 * @return the memberStreet2
	 */
	public String getMemberStreet2() {
		return memberStreet2;
	}

	/**
	 * @param memberStreet2
	 *            the memberStreet2 to set
	 */
	public void setMemberStreet2(String memberStreet2) {
		this.memberStreet2 = memberStreet2;
	}

	/**
	 * @return the memberCity
	 */
	public String getMemberCity() {
		return memberCity;
	}

	/**
	 * @param memberCity
	 *            the memberCity to set
	 */
	public void setMemberCity(String memberCity) {
		this.memberCity = memberCity;
	}

	/**
	 * @return the memberState
	 */
	public String getMemberState() {
		return memberState;
	}

	/**
	 * @param memberState
	 *            the memberState to set
	 */
	public void setMemberState(String memberState) {
		this.memberState = memberState;
	}

	/**
	 * @return the memberZipCode
	 */
	public Integer getMemberZipCode() {
		return memberZipCode;
	}

	/**
	 * @param memberZipCode
	 *            the memberZipCode to set
	 */
	public void setMemberZipCode(Integer memberZipCode) {
		this.memberZipCode = memberZipCode;
	}

	/**
	 * @return the memberZipFour
	 */
	public Integer getMemberZipFour() {
		return memberZipFour;
	}

	/**
	 * @param memberZipFour
	 *            the memberZipFour to set
	 */
	public void setMemberZipFour(Integer memberZipFour) {
		this.memberZipFour = memberZipFour;
	}

	/**
	 * @return the memberCountyCode
	 */
	public String getMemberCountyCode() {
		return memberCountyCode;
	}

	/**
	 * @param memberCountyCode
	 *            the memberCountyCode to set
	 */
	public void setMemberCountyCode(String memberCountyCode) {
		this.memberCountyCode = memberCountyCode;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
