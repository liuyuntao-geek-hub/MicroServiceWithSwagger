package com.anthem.hca.smartpcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.MemberOutput;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.PCP;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.model.Reporting;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.model.ServiceStatus;
import com.anthem.hca.smartpcp.model.Status;
import com.anthem.hca.smartpcp.model.TransactionStatus;
import com.anthem.hca.smartpcp.repository.ProviderInfoRepo;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description -  Service class to create the required ouput payload in both error and
 *                success scenarios
 * 
 * 
 * @author AF71111
 */
@Service
public class OutputPayloadService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutputPayloadService.class);

	
	@Autowired
	private AsyncService asyncService;
	
	@Autowired
	private ProviderInfoRepo providerInfoRepo;
	
	@Autowired
	private Tracer tracer;

	
	/**
	 * @param tranErrorText
	 * @param tranErrorCode
	 * @param member
	 * @param totalErrors
	 * @return OutputPayload
	 */
	public OutputPayload createErrorPayload(String tranErrorText, String tranErrorCode, Member member, int totalErrors) {

		OutputPayload output = new OutputPayload();
		Status status = new Status();
		TransactionStatus transactionStatus = new TransactionStatus();
		ServiceStatus serviceStatus = new ServiceStatus();
		Reporting reporting = new Reporting();
		if(ResponseCodes.SERVICE_NOT_AVAILABLE.equals(tranErrorCode)){
			transactionStatus.setStatus(Constants.OUTPUT_UNSUCCESSFUL);
			transactionStatus.setErrorCode(tranErrorCode);
			transactionStatus.setErrorText((tranErrorText.length()>37)?tranErrorText.substring(0, 37):tranErrorText);
		}else{
			transactionStatus.setStatus(Constants.OUTPUT_SUCCESS);
		}
		serviceStatus.setStatus(Constants.OUTPUT_UNSUCCESSFUL);
		serviceStatus.setErrorCode(tranErrorCode);
		serviceStatus.setErrorText((tranErrorText.length()>37)?tranErrorText.substring(0, 37):tranErrorText);
		status.setTotalErrors(totalErrors);
		status.setTransaction(transactionStatus);
		status.setService(serviceStatus);
		if (null != member) {
			MemberOutput memberOutput = new MemberOutput();
			BeanUtils.copyProperties(member, memberOutput);
			if (null != member.getAddress()) {
				Address address = new Address();
				BeanUtils.copyProperties(member.getAddress(), address);
				memberOutput.setAddress(address);
			}
			output.setMember(memberOutput);
		}
		output.setSmartPCPTraceID(tracer.getCurrentSpan().traceIdString());
		output.setStatus(status);
		output.setReporting(reporting);

		return output;

	}

	
	/**
	 * @param reportingCode
	 * @param member
	 * @param pcpId
	 * @param dummyFlag
	 * @param networkId
	 * @param pcpScore
	 * @return OutputPayload
	 */
	public OutputPayload createSuccessPaylodAffinity(Member member,Provider validPCP) {

		OutputPayload output = new OutputPayload();
		Address address = new Address();
		MemberOutput memberOutput = new MemberOutput();
		Status status = new Status();
		TransactionStatus transactionStatus = new TransactionStatus();
		Reporting reporting = new Reporting();
		ServiceStatus serviceStatus = new ServiceStatus();
		
		transactionStatus.setStatus(Constants.OUTPUT_SUCCESS);
		serviceStatus.setStatus(Constants.OUTPUT_SUCCESS);
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		reporting.setReportingText1(Constants.REPORTING_TEXT_AFFINITY);
		status.setTransaction(transactionStatus);
		status.setService(serviceStatus);
		BeanUtils.copyProperties(member, memberOutput);
		BeanUtils.copyProperties(member.getAddress(), address);
		
		memberOutput.setAddress(address);
		output.setMember(memberOutput);
		output.setStatus(status);
		output.setReporting(reporting);
		

		PCP assignedPCP = new PCP();
		BeanUtils.copyProperties(validPCP, assignedPCP);
		assignedPCP.setAddress(validPCP.getAddress());
		if (validPCP.getRgnlNtwkId().length() == 4) {
			assignedPCP.setContractCode(validPCP.getRgnlNtwkId());
		} else {
			assignedPCP.setNetworkId(validPCP.getRgnlNtwkId());
		}
		assignedPCP.setDummyPCP(Constants.FALSE);
		output.setProvider(assignedPCP);
		asyncService.asyncUpdate(assignedPCP.getProvPcpId(), validPCP.getRgnlNtwkId());
		LOGGER.info("PCP Assigned Affinity provPcpId={}, network={}", validPCP.getProvPcpId(), validPCP.getRgnlNtwkId());
	
		
		output.setSmartPCPTraceID(tracer.getCurrentSpan().traceIdString());
		return output;
	}
	
	/**
	 * @param reportingCode
	 * @param member
	 * @param pcpId
	 * @param dummyFlag
	 * @param networkId
	 * @param pcpScore
	 * @return OutputPayload
	 */
	public OutputPayload createSuccessPaylodMDO(Member member, boolean dummyFlag,ScoringProvider validPCP) {

		OutputPayload output = new OutputPayload();
		Address address = new Address();
		MemberOutput memberOutput = new MemberOutput();
		Status status = new Status();
		TransactionStatus transactionStatus = new TransactionStatus();
		Reporting reporting = new Reporting();
		ServiceStatus serviceStatus = new ServiceStatus();
		
		transactionStatus.setStatus(Constants.OUTPUT_SUCCESS);
		serviceStatus.setStatus(Constants.OUTPUT_SUCCESS);
		reporting.setReportingCode(dummyFlag? Constants.REPORTING_CODE_DEFAULT : Constants.REPORTING_CODE_MDO);
		reporting.setReportingText1(dummyFlag ? Constants.REPORTING_TEXT_DEFAULT : Constants.REPORTING_TEXT_MDO);
		status.setTransaction(transactionStatus);
		status.setService(serviceStatus);
		BeanUtils.copyProperties(member, memberOutput);
		BeanUtils.copyProperties(member.getAddress(), address);
		
		memberOutput.setAddress(address);
		output.setMember(memberOutput);
		output.setStatus(status);
		output.setReporting(reporting);
		
		if (dummyFlag) {
			PCP dummyPCP = new PCP();
			dummyPCP.setProvPcpId(validPCP.getProvPcpId());
			dummyPCP.setPcpPmgIpa(Constants.PCP_TYPE_I);
			dummyPCP.setDummyPCP(Constants.TRUE);
			output.setProvider(dummyPCP);
		} else {
			PCP assignedPCP = providerInfoRepo.getAssignedPcp(validPCP.getProvPcpId(), validPCP.getRgnlNtwkId());
			assignedPCP.setPcpRank(validPCP.getRank());
			assignedPCP.setPcpScore(validPCP.getPcpScore());
			output.setProvider(assignedPCP);
			asyncService.asyncUpdate(assignedPCP.getProvPcpId(), validPCP.getRgnlNtwkId());
			LOGGER.info("PCP Assigned MDO provPcpId={}, network={}, dummy={}", validPCP.getProvPcpId(), validPCP.getRgnlNtwkId(), dummyFlag);
		}
		
		output.setSmartPCPTraceID(tracer.getCurrentSpan().traceIdString());
		return output;
	}

}
