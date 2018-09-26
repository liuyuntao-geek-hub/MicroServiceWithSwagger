package com.anthem.hca.smartpcp.affinity.model;

import java.io.Serializable;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				Destination is used for destination payload information.
 * 
 * *@author AF65409
 */
public class Destination implements Serializable {

	private double latitude;
	private double longitude;
	private static final long serialVersionUID = -3899325997779155763L;

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
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
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Destination [latitude=" + latitude + ", longitude=" + longitude + "]";
	}


}
