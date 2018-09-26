/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.model;

public class Rules {

	private String market;
	private String lob;
	private String product;
	private String assignmentType;
	private String assignmentMethod;

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	public void setAssignmentMethod(String assignmentMethod) {
		this.assignmentMethod = assignmentMethod;
	}

}
