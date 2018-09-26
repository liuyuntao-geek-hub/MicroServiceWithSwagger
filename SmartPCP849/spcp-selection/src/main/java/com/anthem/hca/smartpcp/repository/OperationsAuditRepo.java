package com.anthem.hca.smartpcp.repository;

import java.sql.Timestamp;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.anthem.hca.smartpcp.constants.Constants;
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
@Transactional
public class OperationsAuditRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationsAuditRepo.class);

	@Value("${operations.audit.insert.qry}")
	private String oprtnsAuditInsertQry;
	
	@Value("${operations.audit.update.qry}")
	private String oprtnsAuditUpdateQry;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param member
	 * @param traceId
	 * @return int
	 */
	@Transactional
	public int logSPCPSelectOpr(Member member, String traceId, OperationsAuditUpdate operationsAudit) {

		String network;
		Timestamp createdTime = new Timestamp(new Date().getTime());
		operationsAudit.setCreateTime(createdTime);
		
		if (null != member.getMemberNetworkId() && !member.getMemberNetworkId().isEmpty()) {
			network = StringUtils.join(member.getMemberNetworkId(), ",");
		} else {
			network = StringUtils.join(member.getMemberContractCode(), ",");
		}
		
		LOGGER.debug("Query to insert into audit tables {}" ,oprtnsAuditInsertQry);
		return jdbcTemplate.update(oprtnsAuditInsertQry, traceId, member.getMemberFirstName(), member.getMemberLastName(),
				member.getMemberGender(), member.getMemberEid(), network,
				member.getMemberLineOfBusiness(), member.getMemberSequenceNumber(),
				Constants.PCP_ASSIGNED_REQUEST_STATUS,createdTime);
	}

	/**
	 * @param spcpSlctOprModel
	 * @return int
	 */
	@Transactional
	public int updateSPCPSelectOpr(OperationsAuditUpdate spcpSlctOprModel,String processingState) {

		LOGGER.debug("Query to update the audit tables {}" ,oprtnsAuditUpdateQry);
		return jdbcTemplate.update(oprtnsAuditUpdateQry, spcpSlctOprModel.getInvocationOrder(),
				spcpSlctOprModel.getAssignmentTypeCode(), spcpSlctOprModel.getProductType(),
				spcpSlctOprModel.getPcpIdAssignedStatus(), spcpSlctOprModel.getPcpIdAssigned(),
				spcpSlctOprModel.getMdoScore(), spcpSlctOprModel.getMdoRank(), spcpSlctOprModel.getDrivingDistance(),
				spcpSlctOprModel.getDummyFlag(), spcpSlctOprModel.getTaxId(), spcpSlctOprModel.getResponseCode(),
				spcpSlctOprModel.getResponseMessage(),processingState, spcpSlctOprModel.getTraceId(), spcpSlctOprModel.getCreateTime());
	}

}
