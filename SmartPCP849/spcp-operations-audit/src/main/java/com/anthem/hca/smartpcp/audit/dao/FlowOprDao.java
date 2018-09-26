package com.anthem.hca.smartpcp.audit.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anthem.hca.smartpcp.audit.model.FlowOprModel;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				FlowOprDao is used to insert a row to database from the data in flowOprModel object
 * 
 * @author AF56159 
 */

@Transactional
@Service
public class FlowOprDao {
	
	@Value("${smartpcp.schema}")
	private String schema;
	
	@Value("${spcp.operations.audit.insert.flowopr.query}")
	private String insertFlowOprQuery;
	

	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * method to INSERT a row in to database from the flowOprModel data attributes
	 */
	public void logFlowOprtn(FlowOprModel flowOprModel) {
		String sql = insertFlowOprQuery.replace("SCHEMA.", schema + ".");
		jdbcTemplate.update(sql, flowOprModel.getTraceId(), flowOprModel.getServiceName(),
				flowOprModel.getOperationStatus(), flowOprModel.getOperationOutput(), flowOprModel.getResponseCode(),
				flowOprModel.getResponseMessage());
	}

}
