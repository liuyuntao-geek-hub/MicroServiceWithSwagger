/**
 * 
 */
package com.anthem.hca.smartpcp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProviderInfoConstants;

/**
 * @author af54903 Repository class to insert provider details to provider
 *         history table.
 */
/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Repository class to insert provider details to provider history table.
 * 
 * 
 * @author AF71111
 */
@RefreshScope
public class ProviderHistRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderHistRepo.class);

	@Value("${smartpcp.schema}")
	private String schema;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String historyTable = "PROVIDER_TX_HIST";

	private String targetView = "PROVIDER_INFO";

	/**
	 * @param member
	 */
	@Async
	public void insertPCP(String traceId, String pcpId, String networkId) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO " + schema + "." + historyTable);
		query.append(" SELECT top 1 ? ,");
		query.append(ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.GRPG_RLTD_PADRS_EFCTV_DT_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.GRPG_RLTD_PADRS_TRMNTN_DT_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_ZIP_CD_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.ADRS_ZIP_CD_PLUS_4_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.LATD_CORDNT_NBR_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.LNGTD_CORDNT_NBR_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_CNTY_CD_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.PCP_RANKING_ID_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.SPCLTY_CD_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.WGS_SPCLTY_CD_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.SPCLTY_DESC_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.SPECIALTY_MNEMONIC_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.PRMRY_SPCLTY_IND_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.MAX_MBR_CNT_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.CURNT_MBR_CNT_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.CP_TYPE_CD_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ACC_NEW_PATIENT_FLAG_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.ISO_3_CODE_LABEL + Constants.COMMA
				+ ProviderInfoConstants.PCP_LANG_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.VBP_FLAG_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.HMO_TYPE_CD_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.FIRST_NAME_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.MIDDLE_NAME_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.LAST_NAME_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.ADRS_LINE_1_TXT_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_LINE_2_TXT_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.ADRS_CITY_NM_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.ADRS_ST_CD_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.ADRS_PHONE_NUMBER_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.PROV_PRTY_CD_COLUMN_LABEL + Constants.COMMA);
		query.append(ProviderInfoConstants.NPI_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.TAX_ID_COLUMN_LABEL + Constants.COMMA
				+ ProviderInfoConstants.TIER_LVL_COLUMN_LABEL + Constants.COMMA + "GETDATE()");
		query.append(" FROM " + schema + "." + targetView);
		query.append(" WHERE " + ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL + "= ? AND "
				+ ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL + " = ?");
		LOGGER.debug("Query to insert pcp details into History table {}", query);
		jdbcTemplate.update(query.toString(), traceId, pcpId, networkId);
	}
}
