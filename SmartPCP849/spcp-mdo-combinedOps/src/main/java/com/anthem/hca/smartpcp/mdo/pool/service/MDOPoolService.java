/**
* Copyright © 2018 Anthem, Inc.
* 
*  MDOPoolService class handles all the logic needed to build the pcp pool. It
* connects to Drools Engine and gets the pool size and aerial distance limit.
* Prepares the pool of pcp's based on the above inputs and sends it to
* ProviderValidation. The valid pcp's will be returned from Provider Validation
* and the same will be returned back as response.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.RulesInputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.service.AsyncClientService;
import com.anthem.hca.smartpcp.mdoprocessing.service.RestClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RefreshScope
public class MDOPoolService {

	private static final Logger logger = LoggerFactory.getLogger(MDOPoolService.class);

	@Autowired
	private RestClientService clientService;

	@Autowired
	public AsyncClientService asyncClientService;

	@Autowired
	private ProviderPoolService providerPoolService;

	@Autowired
	private ProviderValidationService providerValidationService;

	@Autowired
	private MDOPoolUtils mdoPoolUtils;

	@Autowired
	private Tracer tracer;

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	public MDOPoolingOutputPayload getPool(Member member) {
		MDOPoolingOutputPayload outputPayload = new MDOPoolingOutputPayload();
		try {
			logger.info("In MDOPoolService, Forming RulesInputPayload to fetch rules from Drools Microservice{}", "");
			RulesInputPayload payload = new RulesInputPayload();
			payload.setMember(member);
			int aerialDistanceLimit;
			String dummyPcp;
			int poolSizeLimit;
			long prov1 = System.nanoTime();
			MDOPoolingRulesOutputPayload mdoPoolRulesPayload = clientService.getPoolInformation(payload);
			long prov2 = System.nanoTime();
			logger.info("Time taken to fetch mdo rules from drools is {} ms",(prov2 - prov1) / 1000000);
			if (null != mdoPoolRulesPayload && ResponseCodes.SUCCESS.equalsIgnoreCase(mdoPoolRulesPayload.getResponseCode())) {

				poolSizeLimit = mdoPoolRulesPayload.getRules().getPoolSize();
				aerialDistanceLimit = mdoPoolRulesPayload.getRules().getMaxRadiusToPool();
				dummyPcp = mdoPoolRulesPayload.getRules().getDummyProviderId();
				if (null != mdoPoolRulesPayload.getRules() && MDOPoolConstants.ZERO != poolSizeLimit
						&& MDOPoolConstants.ZERO != aerialDistanceLimit) {
					logger.info(
							"Drools micro service returned with pool size of {}  , aerial distance of {}  and dummy pcp as {}",
							poolSizeLimit, aerialDistanceLimit, dummyPcp);
					List<PCP> sortedPoolListByAerialDistance = providerPoolService.poolBuilder(aerialDistanceLimit,
							member);
					if (null != sortedPoolListByAerialDistance && sortedPoolListByAerialDistance.isEmpty()) {
						outputPayload = createDummyProvider(dummyPcp);
						outputPayload.setResponseMessage(MDOPoolConstants.POOLBUILDER_DUMMY_MESSAGE);

					} else if (null != sortedPoolListByAerialDistance) {

						outputPayload = filteredListValidation(sortedPoolListByAerialDistance, member, poolSizeLimit,
								dummyPcp);

					}
				}
			} else if (null != mdoPoolRulesPayload) {

				outputPayload.setResponseCode(mdoPoolRulesPayload.getResponseCode());
				outputPayload.setResponseMessage(mdoPoolRulesPayload.getResponseMessage());
			} else {
				outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				outputPayload.setResponseMessage(MDOPoolConstants.ERROR);
			}
		} catch (Exception exception) {
			outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			outputPayload.setResponseMessage(exception.getMessage());
		}
		insertOperationFlow(outputPayload);
		return outputPayload;
	}

	/**
	 * @param outputPayload
	 *            method to call Transaction micro Service for inserting
	 *            transaction details
	 */
	public void insertOperationFlow(MDOPoolingOutputPayload outputPayload) {
		try {
			TransactionFlowPayload payload = new TransactionFlowPayload();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(MDOPoolConstants.SERVICE_NAME);
			payload.setOperationStatus(MDOPoolConstants.SUCCESS);
			payload.setResponseCode(Integer.parseInt(outputPayload.getResponseCode()));
			payload.setResponseMessage(outputPayload.getResponseMessage());

			if (ResponseCodes.SUCCESS == outputPayload.getResponseCode()) {
				payload.setOperationStatus(MDOPoolConstants.SUCCESS);
				payload.setOperationOutput("Pool of PCP formed " + outputPayload.getPcps().size());
			} else {
				payload.setOperationStatus(MDOPoolConstants.FAILURE);
			}
			asyncClientService.insertOperationFlow(payload);
		} catch (Exception exception) {
			logger.error("Error occured while inserting into transaction table {} ", exception.getMessage());
		}
	}

	/**
	 * @param dummyNumber
	 * @return List<PCP>
	 */
	public MDOPoolingOutputPayload createDummyProvider(String dummyNumber) {

		MDOPoolingOutputPayload dummyOutputPayload = new MDOPoolingOutputPayload();
		ArrayList<PCP> dummyProviderList = new ArrayList<>();
		PCP dummyPCP = new PCP();
		try {
			if (!StringUtils.isBlank(dummyNumber)) {
				dummyPCP.setProvPcpId(dummyNumber);
				dummyProviderList.add(dummyPCP);
				dummyOutputPayload.setPcps(dummyProviderList);
				dummyOutputPayload.setResponseCode(ResponseCodes.SUCCESS);
				dummyOutputPayload.setDummyFlag(true);
			} else {
				dummyOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				dummyOutputPayload.setResponseMessage(MDOPoolConstants.INVALID_DUMMY_MESSAGE);
			}
		} catch (Exception exception) {
			logger.error("Error occured while creating dummy PCP {} ", exception.getMessage());
		}

		return dummyOutputPayload;
	}

	/**
	 * @param member
	 * @return JsonNode
	 * @throws JsonProcessingException
	 */
	public JsonNode providerValidationRules(Member member) throws JsonProcessingException {

		RulesInputPayload payload = new RulesInputPayload();
		payload.setMember(member);
		return clientService.getProviderValidationRules(payload);
	}

	/**
	 * @param sortedPool
	 * @param member
	 * @param poolSize
	 * @param dummyPcp
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	private MDOPoolingOutputPayload validatePCP(List<PCP> sortedPool, Member member, int poolSize, String dummyPcp)
			throws JsonProcessingException {
		MDOPoolingOutputPayload outPayload = new MDOPoolingOutputPayload();
		String responseCode;
		String responseMessage;
		JsonNode povValidationRules;
		long prov3 = System.nanoTime();
		JsonNode providerValidationRules = providerValidationRules(member);
		long prov4 = System.nanoTime();
		logger.info("Time taken for getting provider validation rules {} ms",(prov4 - prov3) / 1000000);
		
		if (null != providerValidationRules && !sortedPool.isEmpty()) {

			responseCode = providerValidationRules.get(MDOPoolConstants.RESPONSE_CODE).asText();
			responseMessage = providerValidationRules.get(MDOPoolConstants.RESPONSE_MESSAGE).asText();
			povValidationRules = providerValidationRules.get(MDOPoolConstants.RULES);
			if (ResponseCodes.SUCCESS.equalsIgnoreCase(responseCode) && null != povValidationRules) {
				outPayload = providerValidationService.getPCPValidation(member, povValidationRules, sortedPool,
						poolSize, dummyPcp);
			} else if (null == povValidationRules) {
				outPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				outPayload.setResponseMessage(MDOPoolConstants.INVALID_RULES);
			} else {
				outPayload.setResponseCode(responseCode);
				outPayload.setResponseMessage(responseMessage);
			}
		}
		if (null == outPayload.getPcps() || outPayload.getPcps().isEmpty()) {
			outPayload = createDummyProvider(dummyPcp);
			outPayload.setResponseMessage(MDOPoolConstants.PROVIDER_DUMMY_MESSAGE);

		}
		return outPayload;
	}

	public MDOPoolingOutputPayload filteredListValidation(List<PCP> sortedPoolListByAerialDistance, Member member,
			int poolSizeLimit, String dummyPcp) throws JsonProcessingException {

		MDOPoolingOutputPayload outputPayload = new MDOPoolingOutputPayload();
		List<PCP> filteredList = new ArrayList<>();
		if ("Y".equalsIgnoreCase(member.getMemberPregnancyIndicator())
				&& "f".equalsIgnoreCase(member.getMemberGender())) {
			 filteredList = sortedPoolListByAerialDistance.stream()
					.filter(pcp -> MDOPoolConstants.OBGYN_SPCLTY.equalsIgnoreCase(pcp.getSpcltyDesc()))
					.collect(Collectors.toList());
			logger.info("As memberPregnancyIndicator is Y, Validating OBGYN only providers of size: {} ",
					filteredList.size());
			if (!filteredList.isEmpty()) {
				outputPayload = validatePCP(filteredList, member, poolSizeLimit, dummyPcp);
			}
		} else if (mdoPoolUtils.isAgeUnder18(member.getMemberDob())) {
			 filteredList = sortedPoolListByAerialDistance.stream()
					.filter(pcp -> MDOPoolConstants.PEDIATRICS_SPCLTY.equalsIgnoreCase(pcp.getSpcltyDesc()))
					.collect(Collectors.toList());
			logger.info("As member age is under 18, Validating Pediatrics only providers of size:{} ",
					filteredList.size());
			if (!filteredList.isEmpty()) {
				outputPayload = validatePCP(filteredList, member, poolSizeLimit, dummyPcp);
			}
		}
		if (null == outputPayload.getPcps() || outputPayload.getPcps().isEmpty() || outputPayload.isDummyFlag()) {
			logger.info("No valid specific speciality providers, sending all specialities for validation{}", "");
			sortedPoolListByAerialDistance.removeAll(filteredList);
			outputPayload = validatePCP(sortedPoolListByAerialDistance, member, poolSizeLimit, dummyPcp);
		}
		return outputPayload;
	}

	@Bean
	public HttpHeaders headers() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}