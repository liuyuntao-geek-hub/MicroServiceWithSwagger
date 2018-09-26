package com.anthem.hca.smartpcp.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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
@Configuration
@RefreshScope
public class ProviderInfoRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderInfoRepo.class);

	@Value("${provider.info.details.qry}")
	private String providerInfoQry;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param assignedPcp
	 * @param networkId
	 * @return PCP
	 */
	public PCP getAssignedPcp(String assignedPcp, String networkId) {
		
		LOGGER.debug("Query to fetch assigned PCP details {}", providerInfoQry);
		return jdbcTemplate.query(providerInfoQry, new Object[] { assignedPcp, networkId },
				new ProviderInfoRowMapper());
	}

}