package com.anthem.hca.smartpcp.affinity.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.constants.Constants;
import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.AffinityOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.ProviderValidationOutPayload;
import com.anthem.hca.smartpcp.affinity.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.affinity.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.affinity.rest.RestClientService;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 *  		RestClientPayloadService is used to call other rest services like Transactional micro-service, 
 *  		Drools and Provider Validation. 
 * 
 * @author AF65409 
 */
@Service
public class RestClientPayloadService {

	@Autowired
	private Tracer tracer;
	@Autowired
	private RestClientService restClientService;

	@Value("${spring.application.name}")
	private String applicationName; 

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param outputPayload
	 * @return void
	 * 
	 *    insertOperationFlow is used to create Flow Operation payload to insert into Transaction table.
	 * 
	 */
	public void insertOperationFlow(AffinityOutputPayload outputPayload) {

		TransactionFlowPayload transactionFlowPayload = new TransactionFlowPayload();
		try {
			transactionFlowPayload.setTraceId(tracer.getCurrentSpan().traceIdString());
			transactionFlowPayload.setServiceName(applicationName);
			transactionFlowPayload.setResponseCode(outputPayload.getResponseCode());
			transactionFlowPayload.setResponseMessage(outputPayload.getResponseMessage());

			if(ResponseCodes.SUCCESS.equalsIgnoreCase(outputPayload.getResponseCode())) {
				transactionFlowPayload.setOperationStatus(ErrorMessages.SUCCESS);
				transactionFlowPayload.setOperationOutput(ErrorMessages.PCP_ASSIGNED + "=" + outputPayload.getPcpId());
			} else {
				transactionFlowPayload.setOperationStatus(ErrorMessages.FAILURE);
				transactionFlowPayload.setOperationOutput(ErrorMessages.NO_PCP_ASSIGNED);
			}
			restClientService.insertOperationFlow(transactionFlowPayload);

		} catch (Exception exception) {
			logger.error(ErrorMessages.AUDIT_TX_UPDATE_FAILURE, exception);
		}
	}

	/**
	 * @param member
	 * @return JsonNode
	 * 
	 *    getProviderValidRules is used to call Drool Rule Engine micro-service and get rules for the member.
	 * 
	 */
	public JsonNode getProviderValidRules(Member member) {

		JsonNode rulesEngineOutputPayload = null;
		RulesEngineInputPayload rulesEngineInputPayload = null;
		try {
			if (member == null) {
				ObjectMapper objMapper = new ObjectMapper();
				rulesEngineOutputPayload = objMapper.createObjectNode()
						.put(Constants.RESPONSE_CODE, ResponseCodes.OTHER_EXCEPTIONS)
						.put(Constants.RESPONSE_MESSAGE, ErrorMessages.INVALID_DROOLS_RESP);
			} else {
				rulesEngineInputPayload = new RulesEngineInputPayload();
				rulesEngineInputPayload.setInvocationSystem(member.getInvocationSystem());
				rulesEngineInputPayload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
				rulesEngineInputPayload.setMemberProcessingState(member.getMemberProcessingState());
				rulesEngineInputPayload.setMemberProductType(member.getMemberProductType());
				rulesEngineInputPayload.setMemberType(member.getMemberType());
				rulesEngineInputPayload.setSystemType(member.getSystemType());

				rulesEngineOutputPayload = restClientService.getProviderValidRules(rulesEngineInputPayload);
			}

		} catch (Exception exception) {
			logger.error(ErrorMessages.INVALID_DROOLS_RESP, exception);
		} finally {
			if (rulesEngineOutputPayload == null) {
				ObjectMapper objMapper = new ObjectMapper();
				rulesEngineOutputPayload = objMapper.createObjectNode()
						.put(Constants.RESPONSE_CODE, ResponseCodes.OTHER_EXCEPTIONS)
						.put(Constants.RESPONSE_MESSAGE, ErrorMessages.INVALID_DROOLS_RESP);
			}
		}
		return rulesEngineOutputPayload;
	}

	/**
	 * @param member, providerPayloadList
	 * @return JsonNode
	 * 
	 *    		getProviderValidationOutputPayload is used to call getProviderValidRules internally which call 
	 *    		Drool Rule Engine micro-service and get rules for the member. Then it sends member, providerPayloadList 
	 *    		and rules to Provider validation micro-service to get one valid PCP.
	 * 
	 */
	public ProviderValidationOutPayload getProviderValidationOutputPayload(Member member, List<PCP> providerPayloadList) {

		ProviderValidationOutPayload providerValidationOutPayload = new ProviderValidationOutPayload();
		ProviderValidationHelper providerValidationHelper = new ProviderValidationHelper();
		PCP validPcp = null;
		JsonNode providerValidationInputPayload = null;
		ObjectMapper objMapper = new ObjectMapper();

		try {
			JsonNode rulesEngineOutputPayload = getProviderValidRules(member);
			if (ResponseCodes.SUCCESS.equalsIgnoreCase(rulesEngineOutputPayload.path(Constants.RESPONSE_CODE).asText())) {

				ObjectNode memberData = objMapper.convertValue(member, ObjectNode.class);
				ArrayNode providerData = objMapper.valueToTree(providerPayloadList);
				ObjectNode rules = objMapper.convertValue(rulesEngineOutputPayload, ObjectNode.class);
				providerValidationInputPayload = objMapper.createObjectNode();
				((ObjectNode) providerValidationInputPayload).putObject(Constants.PROVIDER_RULES).setAll(rules);
				((ObjectNode) providerValidationInputPayload).putObject(Constants.MEMBER).setAll(memberData);
				((ObjectNode) providerValidationInputPayload).putArray(Constants.PCP_INFO).addAll(providerData);

				logger.debug("Provider Payload Input {} ", providerValidationInputPayload);

				long startFetch = System.nanoTime();
				validPcp = providerValidationHelper.getPCPForAffinity(member, providerPayloadList, rulesEngineOutputPayload.findPath(Constants.PROVIDER_RULES));
				long endFetch = System.nanoTime();
				double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
				logger.debug("time for query on Provider Validation Response {} milliseconds", fetchTime);

				logger.info("PCP initial={} final={}", providerPayloadList.size(), validPcp != null ? 1 : 0);

				providerValidationOutPayload.setPcpInfo(validPcp);
				providerValidationOutPayload.setResponseCode(rulesEngineOutputPayload.path(Constants.RESPONSE_CODE).asText());
				providerValidationOutPayload.setResponseMessage(ErrorMessages.SUCCESS);
				if(null == validPcp) {
					providerValidationOutPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
					providerValidationOutPayload.setResponseMessage(ErrorMessages.NO_VALID_PCP_IDENTIFIED);
				}
			} else {
				providerValidationOutPayload.setResponseCode(rulesEngineOutputPayload.path(Constants.RESPONSE_CODE).asText());
				providerValidationOutPayload
				.setResponseMessage(rulesEngineOutputPayload.path(Constants.RESPONSE_MESSAGE).asText());
			}
		} catch (Exception exception) {

			logger.error(ErrorMessages.INVALID_VALIDATION, exception);
			providerValidationOutPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			providerValidationOutPayload.setResponseMessage(ErrorMessages.INVALID_VALIDATION);
		}
		return providerValidationOutPayload;
	}
}