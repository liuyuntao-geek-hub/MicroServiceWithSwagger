/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * 
 * Description - Address POJO for Member object
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import javax.validation.constraints.NotNull;

import com.anthem.hca.smartpcp.mdoscoring.utility.ErrorMessages;

import io.swagger.annotations.ApiModelProperty;

public class Address {

	@NotNull(message = ErrorMessages.MISSING_LATITUDE)
	@ApiModelProperty(required = true, notes = "Contains latitude of the member")
	private Double latitude;

	@NotNull(message = ErrorMessages.MISSING_LONGITUDE)
	@ApiModelProperty(required = true, notes = "Contains longitude of the member")
	private Double longitude;
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	
}
