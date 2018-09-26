/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.common.am.vo;

import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModelProperty;

public class Address {

	@ApiModelProperty(notes = "Contains address1 of the member")
	private String addressLine1;

	@ApiModelProperty(notes = "Contains address2 of the member")
	private String addressLine2;

	@ApiModelProperty(notes = "Contains city of the member")
	private String city;

	@ApiModelProperty(notes = "Contains state of the member")
	private String state;

	@ApiModelProperty(notes = "Contains zipcode of the member")
	private Integer zipCode;

	@ApiModelProperty(notes = "Contains zip plus4 code of the member")
	private Integer zipFour;

	@ApiModelProperty(notes = "Contains county code of the member")
	private String countyCode;

	@NotNull(message = "member latitude should be present")
	@ApiModelProperty(required = true, notes = "Contains latitude of the member")
	private Double latitude;

	@NotNull(message = "member longitude should be present")
	@ApiModelProperty(required = true, notes = "Contains longitude of the member")
	private Double longitude;

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1 the addressLine1 to set
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
	 * @param addressLine2 the addressLine2 to set
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
	 * @param city the city to set
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
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the zipCode
	 */
	public Integer getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the zipFour
	 */
	public Integer getZipFour() {
		return zipFour;
	}

	/**
	 * @param zipFour the zipFour to set
	 */
	public void setZipFour(Integer zipFour) {
		this.zipFour = zipFour;
	}

	/**
	 * @return the countyCode
	 */
	public String getCountyCode() {
		return countyCode;
	}

	/**
	 * @param countyCode the countyCode to set
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
	 * @param latitude the latitude to set
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
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
