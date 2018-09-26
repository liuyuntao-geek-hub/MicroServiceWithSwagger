package com.anthem.hca.smartpcp.affinity.model;

import java.io.Serializable;
import java.util.List;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				BingInputPayload is used for Bing input payload information.
 * 
 * *@author AF65409 
 */
public class BingInputPayload implements Serializable {

	private List<Origin> origins = null;
	private List<Destination> destinations = null;
	private String travelMode;
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
		return "BingInputPayload [origins=" + origins + ", destinations=" + destinations + ", travelMode=" + travelMode
				+ "]";
	}

}
