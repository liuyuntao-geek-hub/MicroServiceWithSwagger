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
import com.anthem.hca.smartpcp.constants.OperationsAuditConstants;
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

	@Value("${smartpcp.schema}")
	private String schema;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String table = "OPERATIONS_AUDIT";
	
	private Timestamp createdTime;

	/**
	 * @param member
	 * @param traceId
	 * @return int
	 */
	@Transactional
	public int logSPCPSelectOpr(Member member, String traceId) {

		String network;
		createdTime = new Timestamp(new Date().getTime());
		
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO " + schema + "." + table);
		query.append("(" + OperationsAuditConstants.TRACE_ID + Constants.COMMA + OperationsAuditConstants.MBR_FRST_NM);
		query.append(Constants.COMMA + OperationsAuditConstants.MBR_LAST_NM + Constants.COMMA
				+ OperationsAuditConstants.GENDER);
		query.append(Constants.COMMA + OperationsAuditConstants.HC_SUBS_ID + Constants.COMMA
				+ OperationsAuditConstants.NETWORK_ID);
		query.append(Constants.COMMA+ OperationsAuditConstants.MBR_LINE_OF_BUSINESS);
		query.append(Constants.COMMA + OperationsAuditConstants.MBR_SQNC_NBR);
		query.append(Constants.COMMA + OperationsAuditConstants.PCP_ID_ASSIGNED_STATUS + Constants.COMMA
				+ OperationsAuditConstants.CREATED_TIME + ") ");
		query.append("VALUES(?,?,?,?,?,?,?,?,?,?)");
		

		if (null != member.getMemberNetworkId() && !member.getMemberNetworkId().isEmpty()) {
			network = StringUtils.join(member.getMemberNetworkId(), ",");
		} else {
			network = StringUtils.join(member.getMemberContractCode(), ",");
		}
		
		LOGGER.debug("Query to insert into audit tables {}" ,query);
		return jdbcTemplate.update(query.toString(), traceId, member.getMemberFirstName(), member.getMemberLastName(),
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

		StringBuilder query = new StringBuilder();
		query.append("UPDATE " + schema + "." + table + " SET ");
		query.append(OperationsAuditConstants.INVOCATION_ORDER + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(OperationsAuditConstants.ASSIGNMENT_TYPE_CODE + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(OperationsAuditConstants.MBR_PRODUCT_TYPE + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(OperationsAuditConstants.PCP_ID_ASSIGNED_STATUS + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(
				OperationsAuditConstants.PCP_ID_ASSIGNED + Constants.EQUAL + Constants.QUESTION_MARK + Constants.COMMA);
		query.append(
				OperationsAuditConstants.PCP_MDO_SCORE + Constants.EQUAL + Constants.QUESTION_MARK + Constants.COMMA);
		query.append(
				OperationsAuditConstants.PCP_MDO_RANK + Constants.EQUAL + Constants.QUESTION_MARK + Constants.COMMA);
		query.append(OperationsAuditConstants.PCP_DRIVING_DISTANCE + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(
				OperationsAuditConstants.PCP_DUMMY_FLAG + Constants.EQUAL + Constants.QUESTION_MARK + Constants.COMMA);
		query.append(OperationsAuditConstants.PCP_TAX_ID + Constants.EQUAL + Constants.QUESTION_MARK + Constants.COMMA);
		query.append(
				OperationsAuditConstants.RESPONSE_CODE + Constants.EQUAL + Constants.QUESTION_MARK + Constants.COMMA);
		query.append(OperationsAuditConstants.RESPONSE_MESSAGE + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(OperationsAuditConstants.MBR_PROCESSING_ST + Constants.EQUAL + Constants.QUESTION_MARK
				+ Constants.COMMA);
		query.append(OperationsAuditConstants.UPDATED_TIME + Constants.EQUAL + "GETDATE() WHERE ");
		query.append(OperationsAuditConstants.TRACE_ID + Constants.EQUAL + Constants.QUESTION_MARK+" AND ");
		query.append(OperationsAuditConstants.CREATED_TIME + Constants.EQUAL + Constants.QUESTION_MARK);
		LOGGER.debug("Query to update the audit tables {}" ,query);
		return jdbcTemplate.update(query.toString(), spcpSlctOprModel.getInvocationOrder(),
				spcpSlctOprModel.getAssignmentTypeCode(), spcpSlctOprModel.getProductType(),
				spcpSlctOprModel.getPcpIdAssignedStatus(), spcpSlctOprModel.getPcpIdAssigned(),
				spcpSlctOprModel.getMdoScore(), spcpSlctOprModel.getMdoRank(), spcpSlctOprModel.getDrivingDistance(),
				spcpSlctOprModel.getDummyFlag(), spcpSlctOprModel.getTaxId(), spcpSlctOprModel.getResponseCode(),
				spcpSlctOprModel.getResponseMessage(),processingState, spcpSlctOprModel.getTraceId(),createdTime);

	}

}
