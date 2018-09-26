package com.anthem.hca.smartpcp.mdoprocessing.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PanelCapacityRepo {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int updateCurntMbrCnt(String pcpId, String networkId, double updtdCurntCnt) {
		String updtQuery = "Update DV_PDPSPCP_XM.PROVIDER_INFO set CURNT_MBR_CNT=? where PROV_PCP_ID=? and RGNL_NTWK_ID=?";

		return jdbcTemplate.update(updtQuery, updtdCurntCnt, pcpId, networkId);

	}
}
