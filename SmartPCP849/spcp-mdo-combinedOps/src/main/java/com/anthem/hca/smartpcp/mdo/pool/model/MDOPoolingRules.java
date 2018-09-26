/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.model;

public class MDOPoolingRules extends Rules {

	private int poolSize;
	private int maxRadiusToPool;
	private String dummyProviderId;

	/**
	 * @return the poolSize
	 */
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * @param poolSize
	 *            the poolSize to set
	 */
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	/**
	 * @return the maxRadiusToPool
	 */
	public int getMaxRadiusToPool() {
		return maxRadiusToPool;
	}

	/**
	 * @param maxRadiusToPool
	 *            the maxRadiusToPool to set
	 */
	public void setMaxRadiusToPool(int maxRadiusToPool) {
		this.maxRadiusToPool = maxRadiusToPool;
	}

	/**
	 * @return the dummyProviderId
	 */
	public String getDummyProviderId() {
		return dummyProviderId;
	}

	/**
	 * @param dummyProviderId
	 *            the dummyProviderId to set
	 */
	public void setDummyProviderId(String dummyProviderId) {
		this.dummyProviderId = dummyProviderId;
	}

}
