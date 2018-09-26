package com.anthem.hca.smartpcp.common.am.vo;

import javax.validation.constraints.NotNull;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;

import io.swagger.annotations.ApiModelProperty;

/** 
 * 
 * Copyright © 2018 Anthem, Inc. 
 * 
 *			Address is used for Member Address payload information.
 * 
 * *@author AF65409 
 */
public class Address {

	@NotNull(message = ErrorMessages.MISSING_LATITUDE)
	@ApiModelProperty(required=true, notes="latitude contains the latitude of the member")
	private Double latitude;

	@NotNull(message = ErrorMessages.MISSING_LONGITUDE)
	@ApiModelProperty(required=true, notes="longitude contains the longitude of the member")
	private Double longitude;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Address2 [latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
