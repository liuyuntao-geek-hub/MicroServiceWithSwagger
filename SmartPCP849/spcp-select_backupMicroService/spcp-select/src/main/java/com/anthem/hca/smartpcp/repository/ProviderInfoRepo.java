package com.anthem.hca.smartpcp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProviderInfoConstants;
import com.anthem.hca.smartpcp.model.PCP;
import com.anthem.hca.smartpcp.rowmapper.ProviderInfoRowMapper;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Repository class to fetch pcp details from Database.
 * 
 * 
 * @author AF71111
 */
@RefreshScope
public class ProviderInfoRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderInfoRepo.class);

	@Value("${smartpcp.schema}")
	private String schema;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String view = "PROVIDER_INFO";

	/**
	 * @param assignedPcp
	 * @param networkId
	 * @return PCP
	 */
	@Transactional(readOnly = true)
	public PCP getAssignedPcp(String assignedPcp, String networkId) {
		StringBuilder query = new StringBuilder();

		query.append("SELECT " + ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.HMO_TYPE_CD_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.FIRST_NAME_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.MIDDLE_NAME_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.LAST_NAME_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.ADRS_LINE_1_TXT_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_LINE_2_TXT_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.ADRS_CITY_NM_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_ST_CD_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.ADRS_ZIP_CD_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_ZIP_CD_PLUS_4_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.ADRS_PHONE_NUMBER_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.SPECIALTY_MNEMONIC_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.PROV_PRTY_CD_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.PCP_RANKING_ID_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.LATD_CORDNT_NBR_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.LNGTD_CORDNT_NBR_COLUMN_LABEL);
		query.append(Constants.COMMA + ProviderInfoConstants.TAX_ID_COLUMN_LABEL);
		query.append(" FROM " + schema + "." + view);
		query.append(" WHERE " + ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL + "= ? AND "
				+ ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL + " = ?");
		LOGGER.debug("Query to fetch assigned PCP details {}", query);
		return jdbcTemplate.query(query.toString(), new Object[] { assignedPcp, networkId },
				new ProviderInfoRowMapper());
	}

}