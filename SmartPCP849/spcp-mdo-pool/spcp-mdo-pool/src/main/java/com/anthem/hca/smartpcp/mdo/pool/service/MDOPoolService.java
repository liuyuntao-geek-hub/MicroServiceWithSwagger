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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.ErrorMessages;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.OutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.ProviderPoolOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.RulesInputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RefreshScope
public class MDOPoolService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MDOPoolService.class);

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

	@Value("${spring.application.name}")
	private String applicationName;

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	public OutputPayload getPool(Member member) {
		OutputPayload outputPayload = new OutputPayload();
		int pCPSizeFromDB = 0;
		try {
			LOGGER.debug("In MDOPoolService, Forming RulesInputPayload to fetch rules from Drools Microservice{}", "");
			RulesInputPayload payload = new RulesInputPayload();

			payload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
			payload.setSystemType(member.getSystemType());
			payload.setMemberType(member.getMemberType());
			payload.setMemberProductType(member.getMemberProductType());
			payload.setMemberProcessingState(member.getMemberProcessingState());

			int aerialDistanceLimit;
			String dummyPcp;
			int poolSizeLimit;
			List<PCP> sortedPoolListByAerialDistance = null;
			long prov1 = System.nanoTime();
			MDOPoolingRulesOutputPayload mdoPoolRulesPayload = clientService.getPoolInformation(payload);
			long prov2 = System.nanoTime();
			LOGGER.debug("Time taken to fetch mdo rules from drools is {} ms", (prov2 - prov1) / 1000000);
			if (null != mdoPoolRulesPayload && ResponseCodes.SUCCESS == mdoPoolRulesPayload.getResponseCode()
					&& null != mdoPoolRulesPayload.getRules()) {

				poolSizeLimit = mdoPoolRulesPayload.getRules().getPoolSize();
				aerialDistanceLimit = mdoPoolRulesPayload.getRules().getMaxRadiusToPool();
				dummyPcp = mdoPoolRulesPayload.getRules().getDummyProviderId();
				LOGGER.debug(
						"Drools micro service returned with pool size of {}  , aerial distance of {}  and dummy pcp as {}",
						poolSizeLimit, aerialDistanceLimit, dummyPcp);
				ProviderPoolOutputPayload providerPoolPayload = providerPoolService.poolBuilder(aerialDistanceLimit,
						member);
				if (null != providerPoolPayload && null != providerPoolPayload.getPcps()) {
					sortedPoolListByAerialDistance = providerPoolPayload.getPcps();
					pCPSizeFromDB = providerPoolPayload.getPcpsFromDB();
				}
				if (null != sortedPoolListByAerialDistance && sortedPoolListByAerialDistance.isEmpty()) {
					outputPayload = createDummyProvider(dummyPcp);
				} else if (null != sortedPoolListByAerialDistance) {

					outputPayload = filteredListValidation(sortedPoolListByAerialDistance, member, poolSizeLimit,
							dummyPcp);

				}
			} else if (null != mdoPoolRulesPayload) {

				outputPayload.setResponseCode(mdoPoolRulesPayload.getResponseCode());
				outputPayload.setResponseMessage(mdoPoolRulesPayload.getResponseMessage());
			} else {
				outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				outputPayload.setResponseMessage(ErrorMessages.INTERNAL_PROCS_ERR);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occured while processing {} ", exception.getMessage(), exception);
			outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			outputPayload.setResponseMessage(exception.getClass().getSimpleName());
		}
		insertOperationFlow(outputPayload, pCPSizeFromDB);
		return outputPayload;
	}

	/**
	 * @param outputPayload
	 *            method to call Transaction micro Service for inserting
	 *            transaction details
	 */
	public void insertOperationFlow(OutputPayload outputPayload, int pCPSizeFromDB) {
		try {
			TransactionFlowPayload payload = new TransactionFlowPayload();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(applicationName);
			payload.setResponseCode(outputPayload.getResponseCode());
			payload.setResponseMessage(outputPayload.getResponseMessage());

			if (ResponseCodes.SUCCESS == outputPayload.getResponseCode()) {
				payload.setOperationStatus(MDOPoolConstants.SUCCESS);
				payload.setOperationOutput(
						" Pool of " + outputPayload.getPcps().size() + " valid PCPs formed from " + pCPSizeFromDB);
			} else {
				payload.setOperationStatus(MDOPoolConstants.FAILURE);
			}
			asyncClientService.insertOperationFlow(payload);
		} catch (Exception exception) {
			LOGGER.error("MDO Pool Service Error | Occured while inserting into transaction table {} :",
					exception.getMessage(), exception);
		}
	}

	/**
	 * @param dummyNumber
	 * @return List<PCP>
	 */
	public OutputPayload createDummyProvider(String dummyNumber) {

		OutputPayload dummyOutputPayload = new OutputPayload();
		ArrayList<PCP> dummyProviderList = new ArrayList<>();
		PCP dummyPCP = new PCP();
		try {
			if (!StringUtils.isBlank(dummyNumber)) {
				dummyPCP.setProvPcpId(dummyNumber);
				dummyProviderList.add(dummyPCP);
				dummyOutputPayload.setPcps(dummyProviderList);
				dummyOutputPayload.setResponseCode(ResponseCodes.SUCCESS);
				dummyOutputPayload.setResponseMessage(MDOPoolConstants.DUMMY_MESSAGE);
				dummyOutputPayload.setDummyFlag(true);
			} else {
				dummyOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				dummyOutputPayload.setResponseMessage(ErrorMessages.INVALID_MBR_LOB);
			}
		} catch (Exception exception) {
			LOGGER.error("MDO Pool Service Error | Occured while creating dummy PCP  {} :", exception.getMessage(),
					exception);
			dummyOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			dummyOutputPayload.setResponseMessage(exception.getClass().getSimpleName());
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

		payload.setInvocationSystem(member.getInvocationSystem());
		payload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
		payload.setSystemType(member.getSystemType());
		payload.setMemberType(member.getMemberType());
		payload.setMemberProductType(member.getMemberProductType());
		payload.setMemberProcessingState(member.getMemberProcessingState());

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
	private OutputPayload validatePCP(List<PCP> sortedPool, Member member, int poolSize, String dummyPcp,
			boolean specialityFlag) throws JsonProcessingException {
		OutputPayload outPayload = new OutputPayload();
		int responseCode;
		String responseMessage;
		JsonNode povValidationRules;
		long prov3 = System.nanoTime();
		JsonNode providerValidationRules = providerValidationRules(member);
		long prov4 = System.nanoTime();
		LOGGER.debug("Time taken for getting provider validation rules {} ms", (prov4 - prov3) / 1000000);

		if (null != providerValidationRules && !sortedPool.isEmpty()) {
			responseCode = providerValidationRules.get(MDOPoolConstants.RESPONSE_CODE).asInt();
			responseMessage = providerValidationRules.get(MDOPoolConstants.RESPONSE_MESSAGE).asText();
			povValidationRules = providerValidationRules.get(MDOPoolConstants.RULES);
			if (ResponseCodes.SUCCESS == responseCode && null != povValidationRules) {
				outPayload = providerValidationService.getPCPValidation(member, povValidationRules, sortedPool,
						poolSize, dummyPcp, specialityFlag);
			} else if (null == povValidationRules) {
				outPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				outPayload.setResponseMessage(ErrorMessages.INVALID_DROOLS_RESPONSE);
			} else {
				outPayload.setResponseCode(responseCode);
				outPayload.setResponseMessage(responseMessage);
			}
		}
		if (specialityFlag && (null == outPayload.getPcps() || outPayload.getPcps().isEmpty())) {
			outPayload = createDummyProvider(dummyPcp);

		}
		return outPayload;
	}

	public OutputPayload filteredListValidation(List<PCP> sortedPoolListByAerialDistance, Member member,
			int poolSizeLimit, String dummyPcp) throws JsonProcessingException {

		OutputPayload outputPayload = new OutputPayload();
		List<PCP> filteredList = new ArrayList<>();
		/*
		 * Commenting OBGYN logic as business not required this in phase-I
		 * 
		 * if ("Y".equalsIgnoreCase(member.getMemberPregnancyIndicator()) &&
		 * "f".equalsIgnoreCase(member.getMemberGender())) { filteredList =
		 * sortedPoolListByAerialDistance.stream() .filter(pcp ->
		 * MDOPoolConstants.OBGYN_SPCLTY.equalsIgnoreCase(pcp.getSpcltyDesc()))
		 * .collect(Collectors.toList()); LOGGER.
		 * info("As memberPregnancyIndicator is Y, Validating OBGYN only providers of size: {} "
		 * , filteredList.size()); if (!filteredList.isEmpty()) { outputPayload
		 * = validatePCP(filteredList, member, poolSizeLimit, dummyPcp, false);
		 * } } else
		 */
		if (mdoPoolUtils.isAgeUnder18(member.getMemberDob())) {
			filteredList = sortedPoolListByAerialDistance.stream()
					.filter(pcp -> MDOPoolConstants.PEDIATRICS_SPCLTY.equalsIgnoreCase(pcp.getSpcltyDesc()))
					.collect(Collectors.toList());
			LOGGER.debug("As member age is under 18, Validating Pediatrics only providers of size:{} ",
					filteredList.size());
			if (!filteredList.isEmpty()) {
				outputPayload = validatePCP(filteredList, member, poolSizeLimit, dummyPcp, false);
			}
		}
		if (null == outputPayload.getPcps() || outputPayload.getPcps().isEmpty() || outputPayload.isDummyFlag()) {
			LOGGER.debug("No valid specific speciality providers, sending all specialities for validation{}", "");
			sortedPoolListByAerialDistance.removeAll(filteredList);
			outputPayload = validatePCP(sortedPoolListByAerialDistance, member, poolSizeLimit, dummyPcp, true);
		}
		if (null != sortedPoolListByAerialDistance && null != outputPayload.getPcps()) {
			LOGGER.info("initial={}, final={}", sortedPoolListByAerialDistance.size(), outputPayload.getPcps().size());
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