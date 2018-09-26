package com.anthem.hca.smartpcp.bing.model;

import java.util.List;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		Resource is used for Resource payload information 
 * 
 * @author Khushbu Jain AF65409
 */

public class Resource { 

	private String type;
	private List<Destination> destinations = null;
	private String errorMessage;
	private List<Origin> origins = null;
	private List<Result> results = null;
	private static final long serialVersionUID = -4302432105381829626L;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
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
	 * @return the results
	 */
	public List<Result> getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(List<Result> results) {
		this.results = results;
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
		return "Resource [type=" + type + ", destinations=" + destinations + ", errorMessage=" + errorMessage
				+ ", origins=" + origins + ", results=" + results + "]";
	}

}
