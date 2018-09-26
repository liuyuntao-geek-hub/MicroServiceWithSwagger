package com.anthem.hca.smartpcp.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.service.AffinityService;
import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.constants.ProductPlanConstants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.service.SmartPCPRulesService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.repository.GeocodeRepo;
import com.anthem.hca.smartpcp.repository.ProductTypeRepo;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information
 * 
 * Description - Smart PCP Service connects to Drools to fetch the invocation
 * order of calling the services. The member address is used to calculate the
 * geocodes using Bing. Based on the invocation order retrieved from Drools, the
 * corresponding MDO and Affinity services are called to fetch the assigned pcp
 * based on their respective logics. The obtained response is returned to the
 * Autopic.
 * 
 * 
 * @author AF71111
 */
@Service
public class SmartPCPService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartPCPService.class);

	@Autowired
	private OperationAuditService auditService;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private GeocodeRepo geocodeRepo;

	@Autowired
	private SmartPCPRulesService smartPCPRulesService;

	@Autowired
	private OutputPayloadService outputPayloadHelper;

	@Autowired
	private ProductTypeRepo productTypeRepo;

	@Autowired
	private AffinityService affinityService;

	@Value("${spcp.bing.locations.url}")
	private String bingLocationsUrl;

	@Value("${spcp.bing.key}")
	private String bingKey;

	/**
	 * @param member
	 * @return OutputPayload
	 */
	public OutputPayload getPCP(Member member) {
		OutputPayload outputPayload = null;
		OperationsAuditUpdate operationsAudit = null;
		int insert = 0;
		try {
			asyncService.asyncMemberUpdate(member);

			operationsAudit = new OperationsAuditUpdate();
			insert = auditService.insertOperationFlow(member, operationsAudit);

			if (insert < 1) {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.AUDIT_TX_UPDATE_FAILURE,
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			} else {
				if (StringUtils.isBlank(member.getMemberProductType())
						|| !member.getMemberProductType().matches(Constants.REGEX)
						|| StringUtils.isBlank(member.getMemberProcessingState())
						|| !member.getMemberProcessingState().matches(Constants.REGEX)) {
					retrieveProductType(member);
				}

				String lob = member.getMemberLineOfBusiness();
				member.setMemberLineOfBusiness(lob.substring(0, lob.length() >= 3 ? 3 : lob.length()));

				outputPayload = assignPcp(member, operationsAudit);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			outputPayload = outputPayloadHelper.createErrorPayload(exception.getClass().getSimpleName(),
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		if (insert > 0) {
			int update = auditService.updateOperationFlow(outputPayload, operationsAudit, member.getMemberProductType(),
					member.getMemberProcessingState());
			if (update < 1) {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.AUDIT_TX_UPDATE_FAILURE,
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			}
		}

		return outputPayload;
	}

	/**
	 * @param member
	 * @param order
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	private OutputPayload callServices(Member member, String order, OperationsAuditUpdate operationsAudit)
			throws Exception {

		if (Constants.AM.equalsIgnoreCase(order)) {
			return getPCPAM(member, operationsAudit);
		} else if (Constants.A.equalsIgnoreCase(order)) {
			return getPCPA(member, operationsAudit);
		} else {
			return outputPayloadHelper.createErrorPayload(ErrorMessages.INVALID_DROOLS_RESP,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 * @throws DroolsParseException
	 */
	private OutputPayload getPCPAM(Member member, OperationsAuditUpdate operationsAudit) throws Exception {

		LOGGER.info("Calling Affinity Service to fetch valid PCP {}", Constants.EMPTY_STRING);

		Provider validPCP = affinityService.getAffinityOutPayload(member);

		if (null != validPCP) {
			operationsAudit.setDrivingDistance(validPCP.getDistance());

			return outputPayloadHelper.createSuccessPaylod(Constants.REPORTING_CODE_AFFINITY,
					Constants.REPORTING_TEXT_AFFINITY, member, false, validPCP);
		} else {
			LOGGER.info("No Affinity PCP found for this member {}", Constants.EMPTY_STRING);
			return outputPayloadHelper.createErrorPayload("800", ResponseCodes.OTHER_EXCEPTIONS, member, 1);

		}

	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 * @throws DroolsParseException
	 */
	private OutputPayload getPCPA(Member member, OperationsAuditUpdate operationsAudit) throws Exception {

		Provider validPCP = affinityService.getAffinityOutPayload(member);

		if (null != validPCP) {
			operationsAudit.setDrivingDistance(validPCP.getDistance());

			return outputPayloadHelper.createSuccessPaylod(Constants.REPORTING_CODE_AFFINITY,
					Constants.REPORTING_TEXT_AFFINITY, member, false, validPCP);
		} else {
			LOGGER.info("No Affinity PCP found for this member {}", Constants.EMPTY_STRING);
			return outputPayloadHelper.createErrorPayload("800", ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
	}

	/**
	 * @param product
	 * @param memberEffectiveDate
	 * @return String
	 */
	private Member retrieveProductType(Member member) {

		Map<String, String> attributes = productTypeRepo.getProductTypePlan(member.getMemberProduct(),
				member.getMemberEffectiveDate());

		if (attributes.isEmpty() && (StringUtils.isBlank(member.getMemberProductType())
				|| !member.getMemberProductType().matches(Constants.REGEX))) {
			String productType = productTypeRepo.getProductTypeProd(member.getMemberProduct());
			if (StringUtils.isBlank(productType)) {
				member.setMemberProductType(Constants.PRODUCT_TYPE_DEFAULT);
			} else {
				member.setMemberProductType(productType);
			}
		} else {
			if (StringUtils.isBlank(member.getMemberProductType())
					|| !member.getMemberProductType().matches(Constants.REGEX)) {
				member.setMemberProductType(attributes.get(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL));
			}
			if (StringUtils.isBlank(member.getMemberProcessingState())
					|| !member.getMemberProcessingState().matches(Constants.REGEX)) {
				member.setMemberProcessingState(attributes.get(ProductPlanConstants.PLAN_ST_CD_LABEL));
			}
		}

		if (StringUtils.isBlank(member.getMemberProcessingState())
				|| !member.getMemberProcessingState().matches(Constants.REGEX)) {
			member.setMemberProcessingState(member.getAddress().getState());
		}

		return member;
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws Exception
	 */
	private OutputPayload assignPcp(Member member, OperationsAuditUpdate operationsAudit) throws Exception {

		OutputPayload outputPayload;

		SmartPCPRules smartPCPrules = smartPCPRulesService.getRules(member);

		if (null != smartPCPrules && StringUtils.isNotBlank(smartPCPrules.getInvocationOrder())) {
			long startFetch = System.nanoTime();

			Address add = geocodeRepo.getGeocodeMap().get(member.getAddress().getZipCode());
			if (null != add) {
				Address memberAddress = member.getAddress();
				memberAddress.setLatitude(add.getLatitude());
				memberAddress.setLongitude(add.getLongitude());
			}
			long endFetch = System.nanoTime();
			LOGGER.debug("time for query on  GeoCode {} milliseconds", (endFetch - startFetch) / 1000000.0);

			if (null != member.getAddress().getLatitude() && null != member.getAddress().getLongitude()) {
				LOGGER.info("Bing member geocode zip={}, lat={}, lon={}", member.getAddress().getZipCode(),
						member.getAddress().getLatitude(), member.getAddress().getLongitude());

				operationsAudit.setInvocationOrder(smartPCPrules.getInvocationOrder());

				LOGGER.info("Invocation order obtained from Drools Engine={}", smartPCPrules.getInvocationOrder());

				outputPayload = callServices(member, smartPCPrules.getInvocationOrder(), operationsAudit);
			} else {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.BING_SERVICE_ERROR,
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			}
		} else {
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INTERNAL_PROCS_ERR,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		return outputPayload;
	}

	@Bean
	public ProductTypeRepo productTypeRepo() {
		return new ProductTypeRepo();
	}

}
