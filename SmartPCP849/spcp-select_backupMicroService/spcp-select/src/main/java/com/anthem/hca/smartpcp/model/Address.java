package com.anthem.hca.smartpcp.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.constants.ErrorMessages;

import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing attributes of Address
 * 
 * 
 * @author AF71111
 */
public class Address {

	@NotBlank(message = ErrorMessages.MISSING_ADRS_LINE1)
	@Size(max = 30, message = ErrorMessages.INVALID_ADRS_LINE1)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains address line2 value,max size = 30")
	private String addressLine1;

	@Size(max = 30, message = ErrorMessages.INVALID_ADRS_LINE2)
	@ApiModelProperty(dataType = "String", notes = "Contains address line2 value,max size = 30")
	private String addressLine2;

	@NotBlank(message = ErrorMessages.MISSING_CITY)
	@Size(max = 13, message = ErrorMessages.INVALID_CITY)
	@Pattern(regexp = "[[a-z-A-Z]\\s]*", message = ErrorMessages.INVALID_CITY)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains city ,max size = 13 and regular exp as [[a-z-A-Z]\\s]*")
	private String city;

	@NotBlank(message = ErrorMessages.MISSING_STATE)
	@Size(max = 2, message = ErrorMessages.INVALID_STATE)
	@Pattern(regexp = "[a-z-A-Z]*", message = ErrorMessages.INVALID_STATE)
	@ApiModelProperty(required = true, dataType = "String", notes = "Contains state,max size = 2 and regular exp as [a-z-A-Z]*")
	private String state;

	
	@NotBlank(message = ErrorMessages.MISSING_ZIPCODE)
	@Pattern(regexp = "^(?!0{5})[0-9]{5}$", message = ErrorMessages.INVALID_ZIPCODE)
	@ApiModelProperty(required = true, notes = "Contains zipcode,Should be 5 digit number")
	private String zipCode;

	@Size(max = 4, message = ErrorMessages.INVALID_ZIPFOUR)
	@ApiModelProperty(notes = "Contains zip plus4,max size = 4")
	private String zipFour;

	@Size(max = 3, message = ErrorMessages.INVALID_COUNTY_CODE)
	@ApiModelProperty(dataType = "String", notes = "Contains county code,max size = 3")
	private String countyCode;

	@ApiModelProperty(notes = "Contains latitude")
	private Double latitude;

	@ApiModelProperty(notes = "Contains longitude")
	private Double longitude;

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1
	 *            the addressLine1 to set
	 */
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2
	 *            the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode
	 *            the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the zipFour
	 */
	public String getZipFour() {
		return zipFour;
	}

	/**
	 * @param zipFour
	 *            the zipFour to set
	 */
	public void setZipFour(String zipFour) {
		this.zipFour = zipFour;
	}

	/**
	 * @return the countyCode
	 */
	public String getCountyCode() {
		return countyCode;
	}

	/**
	 * @param countyCode
	 *            the countyCode to set
	 */
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
