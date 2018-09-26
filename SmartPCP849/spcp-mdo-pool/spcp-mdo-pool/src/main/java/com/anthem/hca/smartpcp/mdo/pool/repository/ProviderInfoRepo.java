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
	String rankingTable = MDOPoolConstants.RANKING_TABLE;
	String query = null;

	@Transactional(readOnly = true)
	public List<PCP> getPCPDtlsAsList(List<String> memberNetworkIds, Member member) {

		Double finalLatMin = member.getAddress().getLatitude() - var;
		Double finalLatMax = member.getAddress().getLatitude() + var;
		Double finalLangMin = member.getAddress().getLongitude() - var;
		Double finalLangMax = member.getAddress().getLongitude() + var;
		String productState = member.getMemberProcessingState();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("memberNtwrkIdList", memberNetworkIds);
		parameters.addValue("finalLatMin", finalLatMin);
		parameters.addValue("finalLatMax", finalLatMax);
		parameters.addValue("finalLangMin", finalLangMin);
		parameters.addValue("finalLangMax", finalLangMax);
		parameters.addValue("productState", productState);

		query = "SELECT  a.PROV_PCP_ID, a.PCP_LAST_NM, case when b.MDO_RANKING is null then 0 else b.MDO_RANKING end as MDO_RANKING, a.LATD_CORDNT_NBR, a.LNGTD_CORDNT_NBR, a.RGNL_NTWK_ID, a.GRPG_RLTD_PADRS_EFCTV_DT, a.GRPG_RLTD_PADRS_TRMNTN_DT,a.MAX_MBR_CNT, a.CURNT_MBR_CNT, "
				+ "a.ACC_NEW_PATIENT_FLAG , a.SPCLTY_DESC, a.TIER_LEVEL, a.VBP_FLAG, a.ISO_3_CD FROM ( select * from  (select *, ROW_NUMBER() over(partition by PROV_PCP_ID order by (cast(CURNT_MBR_CNT as decimal(10,4))/cast(MAX_MBR_CNT as decimal(10,4))) asc) as rnum from "
				+ schema + "." + table
				+ " where RGNL_NTWK_ID  in (:memberNtwrkIdList) and LATD_CORDNT_NBR > (:finalLatMin) and LATD_CORDNT_NBR < (:finalLatMax) and LNGTD_CORDNT_NBR > (:finalLangMin) and LNGTD_CORDNT_NBR < (:finalLangMax)) c where c.rnum=1 ) a left join (select TAX_ID,MDO_RANKING from "
				+ schema + "." + rankingTable + " where ADRS_ST_CD in  (:productState)) b on a.TAX_ID = b.TAX_ID ";
		return jdbcTemplate.query(query, parameters, new ProviderInfoEtlRowMapper());

	}
}
