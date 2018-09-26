
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.io.Serializable;
import java.util.List;

public class ResourceSet implements Serializable {

	private int estimatedTotal;
	private List<Resource> resources;
	private static final long serialVersionUID = 6116142633317047398L;

	public int getEstimatedTotal() {
		return estimatedTotal;
	}

	public void setEstimatedTotal(int estimatedTotal) {
		this.estimatedTotal = estimatedTotal;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
