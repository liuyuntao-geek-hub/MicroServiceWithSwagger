/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;


public class Result {

	private int destinationIndex;
	private int originIndex;
	private int totalWalkDuration;
	private double travelDistance;
	private double travelDuration;

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


}
