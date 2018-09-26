package com.anthem.hca.smartpcp.repository;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.annotation.Transactional;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ControlTableConstants;

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
@Transactional
public class PanelCapacityRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanelCapacityRepo.class);

	@Value("${panel.capacity.contrl.qry}")
	private String controlQuery;
	
	@Value("${panel.capacity.update.table1.qry}")
	private String updateQry1;
	
	@Value("${panel.capacity.update.table2.qry}")
	private String updateQry2;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String view = "PROVIDER_INFO";
	
	private String tableA = "PROVIDER_INFO1";
	
	/**
	 * @param pcpId
	 * @param networkId
	 * @param updtdCurntCnt
	 * @param spltyCd
	 * @return updated row count
	 */

	public void updatePanelCapacity(String pcpId, String networkId) {
		
		try{
			LOGGER.debug("Updating panel capacity for the assigned pcpid: {} ", pcpId);
		
		String provTable = jdbcTemplate.query(controlQuery, new Object[] { Constants.TRUE, view},
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet resultSet) throws SQLException {
						return resultSet.next() ? resultSet.getString(ControlTableConstants.TABLE_NM_COLUMN_LABEL)
								: Constants.EMPTY_STRING;
					}
				});
		if(StringUtils.isNotBlank(provTable)){
			String updateQuery;
		if(tableA.equalsIgnoreCase(provTable)){
			updateQuery = updateQry1;
		}else{
			updateQuery = updateQry2;
		}
		LOGGER.debug("Query to update panel capacity {}", updateQuery);
		jdbcTemplate.update(updateQuery, pcpId, networkId);
		}else{
			LOGGER.error("No table found with Active status in Control table with view name {}",view);
		}
		
		}catch(Exception exception){
			LOGGER.error("Exception occured while updating panel capacity {}",exception.getMessage(),exception);
		}
	}
	
	
}
