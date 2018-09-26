package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.io.Serializable;

public class Origin implements Serializable {

	private double latitude;
	private double longitude;
	private static final long serialVersionUID = -1015903659362170102L;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
