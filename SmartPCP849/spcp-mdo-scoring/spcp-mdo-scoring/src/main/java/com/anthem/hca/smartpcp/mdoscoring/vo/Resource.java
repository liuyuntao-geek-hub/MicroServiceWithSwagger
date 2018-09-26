/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.List;

public class Resource {

	private String type;
	private List<Destination> destinations;
	private String errorMessage;
	private List<Origin> origins;
	private List<Result> results;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<Destination> destinations) {
		this.destinations = destinations;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<Origin> getOrigins() {
		return origins;
	}

	public void setOrigins(List<Origin> origins) {
		this.origins = origins;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

}
