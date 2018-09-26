package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.io.Serializable;

public class Destination implements Serializable {

	private double latitude;
	private double longitude;
	private static final long serialVersionUID = -3899325997779155763L;

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
