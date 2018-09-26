/**
 * 
 *  Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - Contains the address attribute
 *  
 *  @author AF71274
 * 
 * 
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.model;

import javax.validation.constraints.NotNull;

import com.anthem.hca.smartpcp.mdoprocessing.utils.ErrorMessages;

import io.swagger.annotations.ApiModelProperty;


public class Address {
	
	@NotNull(message = ErrorMessages.MISSING_LATITUDE)
	@ApiModelProperty(required=true,dataType="Double", notes="Contains latitude  of the member address")
	private Double latitude;

	
	@NotNull(message = ErrorMessages.MISSING_LONGITUDE)
	@ApiModelProperty(required=true,dataType="Double", notes="Contains longitude  of the member address")
	private Double longitude;

		/**
	 * @return latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
