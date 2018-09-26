/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidation;
import com.anthem.hca.smartpcp.mdo.pool.rowmapper.PoolingRowMapper;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.ScoringProvider;

public class PoolingRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PoolingRepo.class);

	@Value("${smartpcp.schema}")
	private String schema;

	@Value("${lat_lang.config.variable}")
	private double geocodeConfig;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	String table = Constants.TARGET_TABLE;
	String rankingTable = Constants.RANKING_TABLE;

	public List<ScoringProvider> getPCPDtlsAsList(List<String> memberNtwrkIds, Member member, MDOPoolingProviderValidationRules rules, String excludedSpecialty,
			String includedSpecialty, String exclude) {

		LOGGER.debug("Connecting data base to fetch provider data from Target table {} ", "");

		Double finalLatMin = member.getAddress().getLatitude() - geocodeConfig;
		Double finalLatMax = member.getAddress().getLatitude() + geocodeConfig;
		Double finalLangMin = member.getAddress().getLongitude() - geocodeConfig;
		Double finalLangMax = member.getAddress().getLongitude() + geocodeConfig;
		String productState = member.getMemberProcessingState();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("memberNtwrkIdList", memberNtwrkIds);
		parameters.addValue("finalLatMin", finalLatMin);
		parameters.addValue("finalLatMax", finalLatMax);
		parameters.addValue("finalLangMin", finalLangMin);
		parameters.addValue("finalLangMax", finalLangMax);
		parameters.addValue("productState", productState);
		parameters.addValue("aerialLimit", rules.getDistance());
		parameters.addValue("memberLat", member.getAddress().getLatitude());
		parameters.addValue("memberLon", member.getAddress().getLongitude());

		StringBuilder query = new StringBuilder();
		
		query.append("select PROV_PCP_ID , PCP_LAST_NM , RGNL_NTWK_ID , GRPG_RLTD_PADRS_EFCTV_DT , ISO_3_CD , VBP_FLAG ,");
		query.append("AERIAL_DIST , PANEL_CAPACITY , case when B.MDO_RANKING is null then 0 else B.MDO_RANKING end as MDO_RANKING,SPCLTY_DESC from(");
		query.append("select top "+rules.getPoolSize() );
		query.append(" a.PROV_PCP_ID , a.PCP_LAST_NM , a.RGNL_NTWK_ID , a.GRPG_RLTD_PADRS_EFCTV_DT , a.VBP_FLAG , a.ISO_3_CD ,a.SPCLTY_DESC,");
		query.append(" a.TAX_ID , a.AERIAL_DIST , a.PANEL_CAPACITY FROM ( ");
		query.append(" SELECT PROV_PCP_ID , PCP_LAST_NM , RGNL_NTWK_ID , GRPG_RLTD_PADRS_EFCTV_DT , ISO_3_CD , TAX_ID , VBP_FLAG,SPCLTY_DESC, ");
		
		query.append("(DEGREES(ACOS(SIN(RADIANS((:memberLat)))*SIN(RADIANS(LATD_CORDNT_NBR))+COS(RADIANS((:memberLat)))*COS(RADIANS(LATD_CORDNT_NBR))*COS(RADIANS((:memberLon)-LNGTD_CORDNT_NBR))))*60*1.1515) AS AERIAL_DIST,");
		
		query.append("(cast(CURNT_MBR_CNT as decimal(10,4))/cast(MAX_MBR_CNT as decimal(10,4)))*100 AS PANEL_CAPACITY,");
		
		query.append(
				"ROW_NUMBER() over(partition by PROV_PCP_ID order by (cast(CURNT_MBR_CNT as decimal(10,4))/cast(MAX_MBR_CNT as decimal(10,4))) asc) as rnum ");
		
		query.append("FROM " + schema + "." + table);
		query.append(" where RGNL_NTWK_ID  in (:memberNtwrkIdList)");
		
		query.append(" and LATD_CORDNT_NBR > (:finalLatMin) and LATD_CORDNT_NBR < (:finalLatMax) and LNGTD_CORDNT_NBR > (:finalLangMin) and LNGTD_CORDNT_NBR < (:finalLangMax) ");
		
		if (rules.isValidationRequired(ProviderValidation.ACCEPTING_PATIENTS_VALIDATION)) {
			acceptNewPatientValidation(query);
		}

		if (StringUtils.isNotBlank(includedSpecialty)) {
			includeSpecialty(query, parameters, includedSpecialty);
		}

		if (StringUtils.isNotBlank(excludedSpecialty)) {
			excludeSpecialty(query, parameters, excludedSpecialty);
		}


		if (rules.isValidationRequired(ProviderValidation.CONTRACT_VALIDATION)) {
			contractValidation(query, parameters, member.getMemberEffectiveDate(), rules.getContractCushionPeriod());
		}

		if (rules.isValidationRequired(ProviderValidation.SPECIALTY_VALIDATION)) {
			specialtyValidation(query, rules, exclude);
		}

		if (rules.isValidationRequired(ProviderValidation.ROLLOVER_VALIDATION)) {
			rollOverValidation(query, parameters, member.getRollOverPcpId(), rules.getRolloverFlag());
		}

		query.append(") a WHERE a.rnum=1 and");
		query.append(" a.AERIAL_DIST<(:aerialLimit) ");
		if (rules.isValidationRequired(ProviderValidation.PANEL_CAPACITY_VALIDATION)) {
			panelCapacityValidation(query, parameters, rules.getPanelCapacityPercent());
		}
		query.append(" order by  a.AERIAL_DIST asc ) A ");
		query.append(" left join ");
		query.append(
				"(select TAX_ID,MDO_RANKING from ");
		query.append(schema+"."+rankingTable+" where ADRS_ST_CD in  (:productState)) B on A.TAX_ID = B.TAX_ID ");
		//query.append(" on A.TAX_ID = B.TAX_ID)C order by C.AERIAL_DIST option (FAST ");
		
//		query.append(rules.getPoolSize()+" ) ");
		LOGGER.debug("Query to be executed to fetch valid PCP {}", query);
		List<ScoringProvider> pcp = jdbcTemplate.query(query.toString(), parameters, new PoolingRowMapper(rules.getPoolSize()));
		LOGGER.info("Fetched data from DB");
		return pcp;
	}

	private void includeSpecialty(StringBuilder query, MapSqlParameterSource parameters, String includedSpecialty) {
		query.append(" AND SPCLTY_DESC  like (:includedSpecialty)");
		parameters.addValue("includedSpecialty", "%" + includedSpecialty + "%");
	}

	private void excludeSpecialty(StringBuilder query, MapSqlParameterSource parameters, String excludedSpecialty) {
		query.append(" AND SPCLTY_DESC NOT LIKE (:excludedSpecialty)");
		parameters.addValue("excludedSpecialty", "%" + excludedSpecialty + "%");
	}

	private void contractValidation(StringBuilder query, MapSqlParameterSource parameters, String effectiveDate,
			Integer cushionPeriod) {

		query.append(" AND (:memberEffectiveDate)>=GRPG_RLTD_PADRS_EFCTV_DT ");
		query.append(" AND (:memberEffectiveDate)<GRPG_RLTD_PADRS_TRMNTN_DT ");
		LocalDate memberEffectiveDate = LocalDate.parse(effectiveDate);
		query.append(" AND DATEDIFF(DAY, (:effectiveDate),GRPG_RLTD_PADRS_TRMNTN_DT ) >(:effectiveDays)");
		parameters.addValue("effectiveDate",
				Date.valueOf(memberEffectiveDate.isAfter(LocalDate.now()) ? memberEffectiveDate : LocalDate.now()));
		parameters.addValue("effectiveDays", cushionPeriod);
		parameters.addValue("memberEffectiveDate", effectiveDate);
	}

	private void specialtyValidation(StringBuilder query, MDOPoolingProviderValidationRules rules, String exclude) {

		if (null != rules.getPrimarySpecialties() && rules.getPrimarySpecialties().length > 0) {
			query.append(" AND (");
			for (int i = 0; i < rules.getPrimarySpecialties().length; i++) {
				if (i != (rules.getPrimarySpecialties().length - 1)) {
					query.append("REPLACE(SPCLTY_DESC,'"+exclude+"','') like '%" + rules.getPrimarySpecialties()[i] + "%' OR ");
				} else {
					query.append("REPLACE(SPCLTY_DESC,'"+exclude+"','') like '%" + rules.getPrimarySpecialties()[i] + "%'");
				}
			}
			query.append(") ");
		}
	}

	private void rollOverValidation(StringBuilder query, MapSqlParameterSource parameters, String rollOverPcpId,
			String rollOverFlag) {
		if (rollOverFlag.matches("Y|YES") && StringUtils.isNotBlank(rollOverPcpId)) {
			query.append(" AND PROV_PCP_ID NOT IN (:rollOverPcp)");
			parameters.addValue("rollOverPcp", rollOverPcpId);
		}
	}

	private void panelCapacityValidation(StringBuilder query, MapSqlParameterSource parameters,
			Integer panelCapacityPercent) {
		if (null != panelCapacityPercent) {
			query.append(" AND a.PANEL_CAPACITY <(:panelCapacityPercent) ");
			parameters.addValue("panelCapacityPercent", panelCapacityPercent);
		}
	}

	private void acceptNewPatientValidation(StringBuilder query) {
		query.append(" AND ACC_NEW_PATIENT_FLAG='Y' ");
	}

}
