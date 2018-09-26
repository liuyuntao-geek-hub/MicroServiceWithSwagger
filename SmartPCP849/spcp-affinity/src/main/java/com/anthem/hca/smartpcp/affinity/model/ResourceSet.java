package com.anthem.hca.smartpcp.affinity.model;

import java.io.Serializable;
import java.util.List;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ResourceSet is used for ResourceSet payload information.
 * 
 *@author AF65409 
 */

public class ResourceSet implements Serializable {

	private int estimatedTotal;
	private List<Resource> resources = null;
	private static final long serialVersionUID = 6116142633317047398L;

	/**
	 * @return the estimatedTotal
	 */
	public int getEstimatedTotal() {
		return estimatedTotal;
	}
	/**
	 * @param estimatedTotal the estimatedTotal to set
	 */
	public void setEstimatedTotal(int estimatedTotal) {
		this.estimatedTotal = estimatedTotal;
	}
	/**
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}
	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
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
		return "ResourceSet [estimatedTotal=" + estimatedTotal + ", resources=" + resources + "]";
	}

}
