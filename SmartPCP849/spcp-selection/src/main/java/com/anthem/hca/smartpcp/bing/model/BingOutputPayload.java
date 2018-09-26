package com.anthem.hca.smartpcp.bing.model;

import java.util.List;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		BingOutputPayload is used for Bing output payload information.
 * 
 * @author Khushbu Jain AF65409  
 */
public class BingOutputPayload {

	private String authenticationResultCode;
	private String brandLogoUri;
	private String copyright;
	private List<ResourceSet> resourceSets = null;
	private String statusCode;
	private String statusDescription;
	private String traceId;
	private static final long serialVersionUID = -2974369692040507556L;

	/**
	 * @return the authenticationResultCode
	 */
	public String getAuthenticationResultCode() {
		return authenticationResultCode;
	}
	/**
	 * @param authenticationResultCode the authenticationResultCode to set
	 */
	public void setAuthenticationResultCode(String authenticationResultCode) {
		this.authenticationResultCode = authenticationResultCode;
	}
	/**
	 * @return the brandLogoUri
	 */
	public String getBrandLogoUri() {
		return brandLogoUri;
	}
	/**
	 * @param brandLogoUri the brandLogoUri to set
	 */
	public void setBrandLogoUri(String brandLogoUri) {
		this.brandLogoUri = brandLogoUri;
	}
	/**
	 * @return the copyright
	 */
	public String getCopyright() {
		return copyright;
	}
	/**
	 * @param copyright the copyright to set
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	/**
	 * @return the resourceSets
	 */
	public List<ResourceSet> getResourceSets() {
		return resourceSets;
	}
	/**
	 * @param resourceSets the resourceSets to set
	 */
	public void setResourceSets(List<ResourceSet> resourceSets) {
		this.resourceSets = resourceSets;
	}
	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the statusDescription
	 */
	public String getStatusDescription() {
		return statusDescription;
	}
	/**
	 * @param statusDescription the statusDescription to set
	 */
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	/**
	 * @return the traceId
	 */
	public String getTraceId() {
		return traceId;
	}
	/**
	 * @param traceId the traceId to set
	 */
	public void setTraceId(String traceId) {
		this.traceId = traceId;
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
		return "BingOutputPayload [authenticationResultCode=" + authenticationResultCode + ", brandLogoUri="
				+ brandLogoUri + ", copyright=" + copyright + ", resourceSets=" + resourceSets + ", statusCode="
				+ statusCode + ", statusDescription=" + statusDescription + ", traceId=" + traceId + "]";
	}

}
