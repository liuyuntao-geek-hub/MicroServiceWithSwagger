package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.io.Serializable;
import java.util.List;

public class BingInputPayload implements Serializable {

	private List<Origin> origins;
	private List<Destination> destinations;
	private String travelMode;
	private static final long serialVersionUID = 8177645696581574821L;

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

}
