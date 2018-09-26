package com.anthem.hca.smartpcp.repository;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;

/**
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information
 * 
 * Description - Repository class to insert details to OPERATIONS_AUDIT table.
 * 
 * 
 * @author AF71111
 */
@RefreshScope
@Repository
public class OperationsAuditRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsAuditRepo.class);

	@Value("${operations.audit.insert.qry}")
	private String oprtnsAuditInsertQry;
	

	@Autowired
	private JdbcTemplate jdbcTemplate;


	/**
	 * @param spcpSlctOprModel
	 * @return int
	 */

	public int insertSPCPSelectOpr(OperationsAuditUpdate spcpSlctOprModel, Member member) {

		LOGGER.debug("Query to insert into the audit tables {}", oprtnsAuditInsertQry);
		return jdbcTemplate.update(oprtnsAuditInsertQry, spcpSlctOprModel.getTraceId(),
				member.getMemberProcessingState(), member.getMemberProductType(), spcpSlctOprModel.getInvocationOrder(),
				spcpSlctOprModel.getAssignmentTypeCode(), spcpSlctOprModel.getPcpIdAssignedStatus(),
				spcpSlctOprModel.getPcpIdAssigned(), spcpSlctOprModel.getMdoScore(), spcpSlctOprModel.getMdoRank(),
				spcpSlctOprModel.getDrivingDistance(), spcpSlctOprModel.getDummyFlag(),
				spcpSlctOprModel.getResponseCode(), spcpSlctOprModel.getResponseMessage(), new Timestamp(new Date().getTime()));
	}
}
