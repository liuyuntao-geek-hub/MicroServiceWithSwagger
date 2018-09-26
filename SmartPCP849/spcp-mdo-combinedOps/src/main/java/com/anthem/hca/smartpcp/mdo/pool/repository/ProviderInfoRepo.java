/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.rowmapper.ProviderInfoEtlRowMapper;

public class ProviderInfoRepo {

	@Value("${smartpcp.schema}")
	private String schema;

	@Value("${lat_lang.config.variable}")
	private double var;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	String table = MDOPoolConstants.TARGET_TABLE;
	String query = null;

	@Transactional(readOnly = true)
	public List<PCP> getPCPDtlsAsList(List<String> memberNetworkIds, Member member) {

		Double finalLatMin = member.getAddress().getLatitude() - var;
		Double finalLatMax = member.getAddress().getLatitude() + var;
		Double finalLangMin = member.getAddress().getLongitude() - var;
		Double finalLangMax = member.getAddress().getLongitude() + var;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("memberNtwrkIdList", memberNetworkIds);
		parameters.addValue("finalLatMin", finalLatMin);
		parameters.addValue("finalLatMax", finalLatMax);
		parameters.addValue("finalLangMin", finalLangMin);
		parameters.addValue("finalLangMax", finalLangMax);
		query = "SELECT PROV_PCP_ID, PCP_LAST_NM, PCP_RANKG_ID, LATD_CORDNT_NBR, LNGTD_CORDNT_NBR, RGNL_NTWK_ID, GRPG_RLTD_PADRS_EFCTV_DT, GRPG_RLTD_PADRS_TRMNTN_DT,MAX_MBR_CNT, CURNT_MBR_CNT, ACC_NEW_PATIENT_FLAG , SPCLTY_DESC, TIER_LEVEL, VBP_FLAG, PCP_LANG FROM "
				+ schema + "." + table
				+ " WHERE RGNL_NTWK_ID  in (:memberNtwrkIdList) and LATD_CORDNT_NBR > (:finalLatMin) and LATD_CORDNT_NBR < (:finalLatMax) and LNGTD_CORDNT_NBR > (:finalLangMin) and LNGTD_CORDNT_NBR < (:finalLangMax)";
		return jdbcTemplate.query(query, parameters, new ProviderInfoEtlRowMapper());

	}
}
