package com.anthem.hca.smartpcp.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.repository.OperationsAuditRepo;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description -  OperationAuditService is a service class to  insert and update
 *                the OPERATIONS_AUDIT table for each transaction with their status..
 * 
 * 
 * @author AF71111
 */
@Service
public class OperationAuditService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationAuditService.class);

	@Autowired
	private Tracer tracer;
	
	@Autowired
	private OperationsAuditRepo operationsAuditRepo;

	/**
	 * @param member
	 */
	public int insertOperationFlow(Member member, OperationsAuditUpdate operationsAudit) {
		
		LOGGER.debug("Inserting request into Audit table {} ",Constants.EMPTY_STRING);
		return operationsAuditRepo.logSPCPSelectOpr(member, tracer.getCurrentSpan().traceIdString(), operationsAudit);

	}


	/**
	 * @param output
	 * @param invocationOrder
	 * @param drivingDistance
	 * @param productType
	 * @return int
	 */
	public int updateOperationFlow(OutputPayload output, OperationsAuditUpdate operationsAudit,String productType,String processingState) {


		operationsAudit.setTraceId(tracer.getCurrentSpan().traceIdString());
		operationsAudit.setProductType(productType);
		
		if (null != output.getStatus() && null != output.getStatus().getService()
				&& Constants.OUTPUT_SUCCESS.equals(output.getStatus().getService().getStatus())) {
			operationsAudit.setPcpIdAssignedStatus(Constants.SUCCESS);
			operationsAudit.setPcpIdAssigned(output.getProvider().getProvPcpId());
			operationsAudit.setTaxId(output.getProvider().getTaxId());
			operationsAudit.setAssignmentTypeCode(output.getReporting().getReportingCode());
			operationsAudit.setResponseCode(ResponseCodes.SUCCESS);
			operationsAudit.setResponseMessage(Constants.PCP_ASSIGNED);
			if (Constants.REPORTING_CODE_MDO.equalsIgnoreCase(output.getReporting().getReportingCode())) {
				operationsAudit.setMdoScore(output.getProvider().getPcpScore());
				operationsAudit.setMdoRank(output.getProvider().getPcpRank());
			}
			operationsAudit.setDummyFlag(
					Constants.TRUE.equalsIgnoreCase(output.getProvider().getDummyPCP()) ? true : false);

		} else {
			operationsAudit.setPcpIdAssignedStatus(Constants.FAILURE);
			operationsAudit.setDummyFlag(false);
			if (null != output.getStatus().getService()
					&& StringUtils.isNotBlank(output.getStatus().getService().getErrorCode())) {
				operationsAudit.setResponseCode(output.getStatus().getService().getErrorCode());
				operationsAudit.setResponseMessage(output.getStatus().getService().getErrorText());
			}
		}
		LOGGER.debug("Updating the audit table record {} ",Constants.EMPTY_STRING);
		return operationsAuditRepo.updateSPCPSelectOpr(operationsAudit,processingState);

	}
	
	
	@Bean
	public OperationsAuditRepo operationsAuditRepo() {
		return new OperationsAuditRepo();
	}
}
