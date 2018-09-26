
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.io.Serializable;

public class Result implements Serializable {

	private int destinationIndex;
	private int originIndex;
	private int totalWalkDuration;
	private double travelDistance;
	private double travelDuration;
	private static final long serialVersionUID = 8607064246588472461L;

	public int getDestinationIndex() {
		return destinationIndex;
	}

	public void setDestinationIndex(int destinationIndex) {
		this.destinationIndex = destinationIndex;
	}

	public int getOriginIndex() {
		return originIndex;
	}

	public void setOriginIndex(int originIndex) {
		this.originIndex = originIndex;
	}

	public int getTotalWalkDuration() {
		return totalWalkDuration;
	}

	public void setTotalWalkDuration(int totalWalkDuration) {
		this.totalWalkDuration = totalWalkDuration;
	}

	public double getTravelDistance() {
		return travelDistance;
	}

	public void setTravelDistance(double travelDistance) {
		this.travelDistance = travelDistance;
	}

	public double getTravelDuration() {
		return travelDuration;
	}

	public void setTravelDuration(double travelDuration) {
		this.travelDuration = travelDuration;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
