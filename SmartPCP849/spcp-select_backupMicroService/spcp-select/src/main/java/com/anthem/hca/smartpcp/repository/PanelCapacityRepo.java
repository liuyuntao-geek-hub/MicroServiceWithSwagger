package com.anthem.hca.smartpcp.repository;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.annotation.Transactional;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ControlTableConstants;
import com.anthem.hca.smartpcp.constants.ProviderInfoConstants;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Repository class to update panel capacity for the assigned pcp.
 * 
 * 
 * @author AF71111
 */
public class PanelCapacityRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanelCapacityRepo.class);

	@Value("${smartpcp.schema}")
	private String schema;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	
	private String controlTable = "SPCP_CNTRL";
	
	private String view = "PROVIDER_INFO";
	
	private String tableA = "PROVIDER_INFO1";
	
	private String tableB = "PROVIDER_INFO2";

	/**
	 * @param pcpId
	 * @param networkId
	 * @param updtdCurntCnt
	 * @param spltyCd
	 * @return updated row count
	 */

	@Transactional(readOnly = false)
	public void updatePanelCapacity(String pcpId, String networkId) {
		
		try{
			LOGGER.debug("Updating panel capacity for the assigned pcpid: {} ", pcpId);
		
		StringBuilder controlQuery = new StringBuilder();
		controlQuery.append("SELECT "+ControlTableConstants.TABLE_NM_COLUMN_LABEL+" FROM ");
		controlQuery.append(schema+"."+controlTable+" WHERE "+ControlTableConstants.STATUS_COLUMN_LABEL+Constants.EQUAL+Constants.QUESTION_MARK );
		controlQuery.append(" AND "+ControlTableConstants.VIEW_NM_COLUMN_LABEL+Constants.EQUAL+Constants.QUESTION_MARK);
		
		String provTable = jdbcTemplate.query(controlQuery.toString(), new Object[] { Constants.TRUE, view},
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException {
						return resultSet.next() ? resultSet.getString(ControlTableConstants.TABLE_NM_COLUMN_LABEL)
								: Constants.EMPTY_STRING;
					}
				});
		if(StringUtils.isNotBlank(provTable)){
		StringBuilder updateQuery = new StringBuilder();
		if(tableA.equalsIgnoreCase(provTable)){
			updateQuery.append("UPDATE " + schema + "." + tableA + " SET ");
		}else{
			updateQuery.append("UPDATE " + schema + "." + tableB + " SET ");
		}
		updateQuery.append(ProviderInfoConstants.CURNT_MBR_CNT_COLUMN_LABEL + Constants.EQUAL
				+ ProviderInfoConstants.CURNT_MBR_CNT_COLUMN_LABEL + "+ 1 ");
		updateQuery.append(" WHERE " + ProviderInfoConstants.PROV_PCP_ID_COLUMN_LABEL + Constants.EQUAL+Constants.QUESTION_MARK+" AND "
				+ ProviderInfoConstants.RGNL_NTWK_ID_COLUMN_LABEL + Constants.EQUAL+Constants.QUESTION_MARK);
		LOGGER.debug("Query to update panel capacity {}", updateQuery);
		jdbcTemplate.update(updateQuery.toString(), pcpId, networkId);
		}else{
			LOGGER.error("No table found with Active status in Control table with view name {}",view);
		}
		
		}catch(Exception exception){
			LOGGER.error("Exception occured while updating panel capacity {}",exception.getMessage(),exception);
		}
	}
	
	
}
