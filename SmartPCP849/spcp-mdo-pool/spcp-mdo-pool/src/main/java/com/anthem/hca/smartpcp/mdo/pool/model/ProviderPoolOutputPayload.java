package com.anthem.hca.smartpcp.mdo.pool.model;

import java.util.List;

import com.anthem.hca.smartpcp.common.am.vo.PCP;

public class ProviderPoolOutputPayload {
	
	private List<PCP> pcps;
	private int pcpsFromDB;
	/**
	 * @return the pcps
	 */
	public List<PCP> getPcps() {
		return pcps;
	}
	/**
	 * @param pcps the pcps to set
	 */
	public void setPcps(List<PCP> pcps) {
		this.pcps = pcps;
	}
	/**
	 * @return the pcpsFromDB
	 */
	public int getPcpsFromDB() {
		return pcpsFromDB;
	}
	/**
	 * @param pcpsFromDB the pcpsFromDB to set
	 */
	public void setPcpsFromDB(int pcpsFromDB) {
		this.pcpsFromDB = pcpsFromDB;
	}
	
	
}
