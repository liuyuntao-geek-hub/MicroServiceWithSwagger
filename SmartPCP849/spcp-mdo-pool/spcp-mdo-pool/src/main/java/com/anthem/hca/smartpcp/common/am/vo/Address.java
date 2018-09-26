/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.common.am.vo;

import javax.validation.constraints.NotNull;
import com.anthem.hca.smartpcp.mdo.pool.constants.ErrorMessages;
import io.swagger.annotations.ApiModelProperty;

public class Address {

	@NotNull(message = ErrorMessages.MISSING_LATITUDE)
	@ApiModelProperty(required = true, notes = "Contains latitude of the member")
	private Double latitude;

	@NotNull(message =ErrorMessages.MISSING_LONGITUDE)
	@ApiModelProperty(required = true, notes = "Contains longitude of the member")
	private Double longitude;

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
