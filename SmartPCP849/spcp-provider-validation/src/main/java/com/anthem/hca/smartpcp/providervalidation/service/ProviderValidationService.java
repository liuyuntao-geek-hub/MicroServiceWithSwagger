/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.providervalidation.constants.ErrorMessages;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.anthem.hca.smartpcp.providervalidation.utility.ResponseCodes;
import com.anthem.hca.smartpcp.providervalidation.vo.AffinityOutputPayloadInfo;
import com.anthem.hca.smartpcp.providervalidation.vo.Constant;
import com.anthem.hca.smartpcp.providervalidation.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.providervalidation.vo.MDOOutputPayloadInfo;
import com.anthem.hca.smartpcp.providervalidation.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.vo.TransactionPayloadInfo;

@Service
public class ProviderValidationService {

	@Autowired
	private ProviderValidationHelper providerHelper;
	@Autowired
	private RestClientService asyncClientService;
	@Autowired
	private Tracer tracer;
	@Value("${spring.application.name}")
	private String serviceName;
	private static final Logger lOGGER = LoggerFactory.getLogger(ProviderValidationService.class);

	/**
	 * Validate if the inputpayload has member details,PCP details and Rules
	 * details and calls Helper method to get PCP.
	 * ***
	 * @param inputPayloadInfos
	 * @return AffinityOutputPayloadInfo that contains a single PCP or NULL
	 */
	public AffinityOutputPayloadInfo getFinalPCPAffinity(InputPayloadInfo inputPayloadInfos) {
		AffinityOutputPayloadInfo outputPayload = null;
		PCP pcpInformation = null;

		try {

			lOGGER.debug(
					"Checking not null for input payload:member information, pcp information, rule set information {}","");
			if (null != inputPayloadInfos.getMember() && null != inputPayloadInfos.getPcpInfo()
					&& null != inputPayloadInfos.getRules()) {

				LocalDate now = LocalDate.now();
				LocalDate dob = LocalDate.parse(inputPayloadInfos.getMember().getMemberDob());
				if (dob.isAfter(now)) {
					outputPayload = new AffinityOutputPayloadInfo();
					outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
					outputPayload.setResponseMessage(ErrorMessages.INVALID_MBR_DOB);
				} else {
					lOGGER.debug("Calling functions from helper {}", "");
					pcpInformation = providerHelper.getPCPForAffinity(inputPayloadInfos.getMember(),
					inputPayloadInfos.getPcpInfo(), inputPayloadInfos.getRules());
					lOGGER.debug("setting value in output payload {}", "");
					outputPayload = new AffinityOutputPayloadInfo();
					outputPayload.setPcpInfo(pcpInformation);
					outputPayload.setResponseCode(ResponseCodes.SUCCESS);
					outputPayload.setResponseMessage(Constant.SUCCESS);
				}
			}
		} catch (Exception exception) {
			lOGGER.error("ProviderValidationService - getFinalPCPAffinity :: ", exception);
			outputPayload = new AffinityOutputPayloadInfo();
			outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			outputPayload.setResponseMessage(exception.getClass().getSimpleName());
		}
		insertOperationFlowAffinity(outputPayload);
		return outputPayload;
	}

	/**
	 * Validate if the inputpayload has member details,PCP details and Rules
	 * details and calls Helper method to get PCP.
	 * 
	 * @param inputPayloadInfos
	 * @return MDOOutputPayloadInfo that contains a List<PCP> or empty List
	 */
	public MDOOutputPayloadInfo getFinalPCPMDO(InputPayloadInfo inputPayloadInfos) {
		MDOOutputPayloadInfo outputPayload = null;
		List<PCP> pcpInformation = null;

		try {

			lOGGER.debug(
					"Checking not null for input payload:member information, pcp information, rule set information {}","");
			if (null != inputPayloadInfos.getMember() && null != inputPayloadInfos.getPcpInfo()
					&& null != inputPayloadInfos.getRules()) {

				LocalDate now = LocalDate.now();
				LocalDate dob = LocalDate.parse(inputPayloadInfos.getMember().getMemberDob());
				if (dob.isAfter(now)) {
					outputPayload = new MDOOutputPayloadInfo();
					outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
					outputPayload.setResponseMessage(ErrorMessages.INVALID_MBR_DOB);
				}

				else {
					lOGGER.debug("Calling functions from helper {}", "");

					pcpInformation = providerHelper.getPCPListMDO(inputPayloadInfos.getMember(),
							inputPayloadInfos.getPcpInfo(), inputPayloadInfos.getRules());
					lOGGER.debug("setting value in output payload {}", "");
					outputPayload = new MDOOutputPayloadInfo();
					outputPayload.setPcpInfo(pcpInformation);
					outputPayload.setResponseCode(ResponseCodes.SUCCESS);
					outputPayload.setResponseMessage(Constant.SUCCESS);
				}
			}
		} catch (Exception exception) {
			lOGGER.error("ProviderValidationService - getFinalPCPMDO :: ", exception);
			outputPayload = new MDOOutputPayloadInfo();
			outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			outputPayload.setResponseMessage(exception.getClass().getSimpleName());
		}
		lOGGER.info("ProviderValidationService - getFinalPCPMDO :: final valid pcp list {}", pcpInformation == null ? 0 : pcpInformation.size());
		insertOperationFlowMDO(outputPayload);
		return outputPayload;
	}

	/**
	 * Inserts Affinity Provider Validations Output status and Logs to
	 * Transaction table and Rules details and calls Helper method to get PCP.
	 * 
	 * @param AffinityOutputPayloadInfo
	 */
	public void insertOperationFlowAffinity(AffinityOutputPayloadInfo outputPayload) {
		try {
			TransactionPayloadInfo payload = new TransactionPayloadInfo();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(serviceName);
			payload.setResponseCode(outputPayload.getResponseCode());
			payload.setResponseMessage(outputPayload.getResponseMessage());
			if (ResponseCodes.SUCCESS.equals(outputPayload.getResponseCode())) {
				payload.setOperationStatus(Constant.SUCCESS);
				if(null!=outputPayload.getPcpInfo())
				{
				payload.setOperationOutput(Constant.OPERATION_OUTPUT+outputPayload.getPcpInfo());
				}
				else
				{
					payload.setOperationOutput(Constant.OPERATION_OUTPUT_EMPTY_PCP);	
				}

			} else {
				payload.setOperationStatus(Constant.FAILURE);
				payload.setOperationOutput(Constant.FAILURE);
			}
			payload.setOperationOutput("Provider Validation successfull");
			asyncClientService.insertOperationFlow(payload);
		} catch (Exception exception) {
			lOGGER.error("Error occured while inserting into transaction table {}", exception);
		}
	}

	/**
	 * Inserts MDO Provider Validations Output status and Logs to Transaction
	 * table and Rules details and calls Helper method to get PCP.
	 * 
	 * @param AffinityOutputPayloadInfo
	 */
	public void insertOperationFlowMDO(MDOOutputPayloadInfo outputPayload) {
		try {
			TransactionPayloadInfo payload = new TransactionPayloadInfo();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(serviceName);
			payload.setResponseCode(outputPayload.getResponseCode());
			payload.setResponseMessage(outputPayload.getResponseMessage());
			if (ResponseCodes.SUCCESS.equals(outputPayload.getResponseCode())) {
				payload.setOperationStatus(Constant.SUCCESS);
				if(null!=outputPayload.getPcpInfo() && !outputPayload.getPcpInfo().isEmpty()){
				payload.setOperationOutput(Constant.OPERATION_OUTPUT+outputPayload.getPcpInfo().size());
				}
				else
				{
					payload.setOperationOutput(Constant.OPERATION_OUTPUT_EMPTY_PCP);	
				}

			} else {
				payload.setOperationStatus(Constant.FAILURE);
				payload.setOperationOutput(Constant.FAILURE);
				
			}
			asyncClientService.insertOperationFlow(payload);
		} catch (Exception exception) {
			lOGGER.error("Error occured while inserting into transaction table {}", exception);
		}
	}

	@Bean
	public ProviderValidationHelper getProviderValidationHelper() {
		return new ProviderValidationHelper();
	}
	
	

}
