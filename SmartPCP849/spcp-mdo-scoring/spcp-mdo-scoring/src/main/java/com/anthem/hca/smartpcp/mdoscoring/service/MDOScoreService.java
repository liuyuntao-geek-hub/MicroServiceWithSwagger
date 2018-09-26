/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - MDOScoreService is used for filtering invalid PCP
 *  	then validated PCP list is passed for further processing .
 * 
 * @author AF70896
 * 
 * 
 *
 */
package com.anthem.hca.smartpcp.mdoscoring.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.mdoscoring.helper.MDOScoreHelper;
import com.anthem.hca.smartpcp.mdoscoring.utility.Constant;
import com.anthem.hca.smartpcp.mdoscoring.utility.ErrorMessages;
import com.anthem.hca.smartpcp.mdoscoring.utility.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPTrackAudit;
import com.anthem.hca.smartpcp.mdoscoring.vo.ScoringParseExecption;
import com.anthem.hca.smartpcp.mdoscoring.vo.TransactionPayloadInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MDOScoreService {

	private static final Logger logger = LoggerFactory.getLogger(MDOScoreService.class);

	@Autowired
	private RestClientService asyncClientService;

	@Autowired
	private MDOScoreHelper mdoHelper;

	@Autowired
	private Tracer tracer;

	@Value("${spring.application.name}")
	private String applicationName;
	
	@Value("${spcp.pcp.track.audit}")
	private boolean pcpTrackAudit;

	public OutputPayloadInfo getFinalPCP(InputPayloadInfo inputPayloadInfos, PCPAssignmentFlow pcpAssignmentFlow) {
		OutputPayloadInfo outputPayload = new OutputPayloadInfo();
		try {
			List<PCP> validatedProviderList = inputPayloadInfos.getPcp().stream().filter(pcp -> {
				boolean flag = false;
				if (null != pcp.getLatdCordntNbr() && null != pcp.getLngtdCordntNbr()
						&& !StringUtils.isBlank(pcp.getProvPcpId()) && !StringUtils.isBlank(pcp.getRgnlNtwkId())
						&& null != pcp.getAerialDistance() && null != pcp.getMaxMbrCnt()) {
					flag = true;
				}
				return flag;
			}).collect(Collectors.toList());
			logger.debug("Number of providers ={}after filtering",validatedProviderList.size());
			if (!validatedProviderList.isEmpty()) {

				PCP finalPcp = mdoHelper.getHighestScorePCP(validatedProviderList, inputPayloadInfos.getRules(),
						inputPayloadInfos.getMember(), pcpAssignmentFlow);
				outputPayload.setDrivingDistance(finalPcp.getDrivingDistance());
				outputPayload.setMdoScore(finalPcp.getMdoScore());
				outputPayload.setProvPcpId(finalPcp.getProvPcpId());
				outputPayload.setPcpRankgId(finalPcp.getPcpRankgId());
				outputPayload.setRgnlNtwkId(finalPcp.getRgnlNtwkId());
				outputPayload.setResponseCode(ResponseCodes.SUCCESS);
				outputPayload.setResponseMessage(Constant.SUCESS_VALUE);
				if(pcpTrackAudit){
					pcpAssignmentFlow.setTraceId(tracer.getCurrentSpan().traceIdString());
					pcpAssignmentFlow.setCreatedTime(new Timestamp(System.currentTimeMillis()));
					persistPCPAssignmentFlow(pcpAssignmentFlow);
				}
			} else {
				throw new ScoringParseExecption(ErrorMessages.INVALID_PCP);
			}
		} catch (Exception exception) {
			logger.error("Exception Occured: {} {}", exception.getMessage(), exception);
			outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			if (ErrorMessages.INVALID_PCP.equals(exception.getMessage())) {
				outputPayload.setResponseMessage(exception.getMessage());
			} else {
				outputPayload.setResponseMessage(exception.getClass().getSimpleName());
			}
		}
		insertOperationFlow(outputPayload);
		return outputPayload;
	}
	
	private void persistPCPAssignmentFlow(PCPAssignmentFlow pcpAssignmentFlow) {
		try {
			PCPTrackAudit pcpAuditInfo = new PCPTrackAudit();
			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = mapperObj.writeValueAsString(pcpAssignmentFlow);
			pcpAuditInfo.setTraceId(tracer.getCurrentSpan().traceIdString());
			pcpAuditInfo.setProviderData(jsonStr);
			asyncClientService.persistPCPAssignmentFlow(pcpAuditInfo);
		} catch (JsonProcessingException e) {
			logger.error("Error occured while saving PCP details", e);
		}
		
	}
	
	private void insertOperationFlow(OutputPayloadInfo outputPayload) {
		try {
			TransactionPayloadInfo payload = new TransactionPayloadInfo();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(applicationName);
			payload.setResponseCode(outputPayload.getResponseCode());
			payload.setResponseMessage(outputPayload.getResponseMessage());

			if (Constant.SUCCESS_CODE.equals(outputPayload.getResponseCode())) {
				payload.setOperationStatus(Constant.SUCESS_VALUE);
				payload.setOperationOutput("Best PCP id is " + outputPayload.getProvPcpId()
						+ " and scores of provider is " + outputPayload.getMdoScore());

			} else {
				payload.setOperationStatus(Constant.FAILURE_VALUE);
			}
			asyncClientService.insertOperationFlow(payload);
		} catch (Exception exception) {
			logger.error("Error occured while inserting into transaction table {}", exception.getMessage());
		}
	}

}
