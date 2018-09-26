package com.anthem.hca.smartpcp.model;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - POJO class containing output attributes of Rules for Drools
 * 
 * 
 * @author AF71111
 */
public class Rules {

	private String market;
	private String lob;
	private String product;
	private String assignmentType;
	private String assignmentMethod;
	private boolean fallbackRequired;

	/**
	 * @return the market
	 */
	public String getMarket() {
		return market;
	}

	/**
	 * @param market
	 *            the market to set
	 */
	public void setMarket(String market) {
		this.market = market;
	}

	/**
	 * @return the lob
	 */
	public String getLob() {
		return lob;
	}

	/**
	 * @param lob
	 *            the lob to set
	 */
	public void setLob(String lob) {
		this.lob = lob;
	}

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return the assignmentType
	 */
	public String getAssignmentType() {
		return assignmentType;
	}

	/**
	 * @param assignmentType
	 *            the assignmentType to set
	 */
	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	/**
	 * @return the assignmentMethod
	 */
	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	/**
	 * @param assignmentMethod
	 *            the assignmentMethod to set
	 */
	public void setAssignmentMethod(String assignmentMethod) {
		this.assignmentMethod = assignmentMethod;
	}

	/**
	 * @return the fallbackRequired
	 */
	public boolean isFallbackRequired() {
		return fallbackRequired;
	}

	/**
	 * @param fallbackRequired
	 *            the fallbackRequired to set
	 */
	public void setFallbackRequired(boolean fallbackRequired) {
		this.fallbackRequired = fallbackRequired;
	}

}
