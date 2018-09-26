package com.anthem.hca.smartpcp.bing.model;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		Result is used for Result payload information. 
 * 
 * @author Khushbu Jain AF65409 
 */

public class Result {

	private int destinationIndex;
	private int originIndex;
	private int totalWalkDuration;
	private double travelDistance;
	private double travelDuration;
	private static final long serialVersionUID = 8607064246588472461L;

	/**
	 * @return the destinationIndex
	 */
	public int getDestinationIndex() {
		return destinationIndex;
	}
	/**
	 * @param destinationIndex the destinationIndex to set
	 */
	public void setDestinationIndex(int destinationIndex) {
		this.destinationIndex = destinationIndex;
	}
	/**
	 * @return the originIndex
	 */
	public int getOriginIndex() {
		return originIndex;
	}
	/**
	 * @param originIndex the originIndex to set
	 */
	public void setOriginIndex(int originIndex) {
		this.originIndex = originIndex;
	}
	/**
	 * @return the totalWalkDuration
	 */
	public int getTotalWalkDuration() {
		return totalWalkDuration;
	}
	/**
	 * @param totalWalkDuration the totalWalkDuration to set
	 */
	public void setTotalWalkDuration(int totalWalkDuration) {
		this.totalWalkDuration = totalWalkDuration;
	}
	/**
	 * @return the travelDistance
	 */
	public double getTravelDistance() {
		return travelDistance;
	}
	/**
	 * @param travelDistance the travelDistance to set
	 */
	public void setTravelDistance(double travelDistance) {
		this.travelDistance = travelDistance;
	}
	/**
	 * @return the travelDuration
	 */
	public double getTravelDuration() {
		return travelDuration;
	}
	/**
	 * @param travelDuration the travelDuration to set
	 */
	public void setTravelDuration(double travelDuration) {
		this.travelDuration = travelDuration;
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
		return "Result [destinationIndex=" + destinationIndex + ", originIndex=" + originIndex + ", totalWalkDuration="
				+ totalWalkDuration + ", travelDistance=" + travelDistance + ", travelDuration=" + travelDuration + "]";
	}

}
