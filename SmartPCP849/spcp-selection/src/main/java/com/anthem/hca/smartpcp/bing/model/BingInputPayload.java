package com.anthem.hca.smartpcp.bing.model;

import java.util.List;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		BingInputPayload is used for Bing input payload information.
 * 
 * @author Khushbu Jain AF65409  
 */
public class BingInputPayload {

	private List<Origin> origins = null;
	private List<Destination> destinations = null;
	private String travelMode;
	private String distanceUnit;
	private static final long serialVersionUID = 8177645696581574821L;

	/**
	 * @return the origins
	 */
	public List<Origin> getOrigins() {
		return origins;
	}
	/**
	 * @param origins the origins to set
	 */
	public void setOrigins(List<Origin> origins) {
		this.origins = origins;
	}
	/**
	 * @return the destinations
	 */
	public List<Destination> getDestinations() {
		return destinations;
	}
	/**
	 * @param destinations the destinations to set
	 */
	public void setDestinations(List<Destination> destinations) {
		this.destinations = destinations;
	}
	/**
	 * @return the travelMode
	 */
	public String getTravelMode() {
		return travelMode;
	}
	/**
	 * @param travelMode the travelMode to set
	 */
	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}
	/**
	 * @return the distanceUnit
	 */
	public String getDistanceUnit() {
		return distanceUnit;
	}
	/**
	 * @param distanceUnit the distanceUnit to set
	 */
	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
