/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.List;

public class BingInputPayload {
	private List<Origin> origins;
	private List<Destination> destinations;
	private String travelMode;
	private String distanceUnit;

	public List<Origin> getOrigins() {
		return origins;
	}

	public void setOrigins(List<Origin> origins) {
		this.origins = origins;
	}

	public List<Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<Destination> destinations) {
		this.destinations = destinations;
	}

	public String getTravelMode() {
		return travelMode;
	}

	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}

	public String getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
	
	
}
