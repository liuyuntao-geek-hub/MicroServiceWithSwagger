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
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
public class ProviderHistRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderHistRepo.class);

	@Value("${provider.hist.insert.qry}")
	private String providerHistInsrtQry;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param member
	 */
	@Async
	public void insertPCP(String traceId, String pcpId, String networkId) {
		
		LOGGER.debug("Query to insert pcp details into History table {}", providerHistInsrtQry);
		jdbcTemplate.update(providerHistInsrtQry, traceId, pcpId, networkId);
	}
}
