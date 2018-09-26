package com.anthem.hca.smartpcp.service;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.service.AffinityService;
import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.constants.ProductPlanConstants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.service.SmartPCPRulesService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.mdo.pool.service.MDOPoolService;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.repository.GeocodeRepo;
import com.anthem.hca.smartpcp.repository.ProductTypeRepo;
import com.anthem.hca.smartpcp.util.DateUtils;
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
@RefreshScope
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

	@Autowired
	private MDOPoolService mdoService;

	/**
	 * @param member
	 * @return OutputPayload
	 */
	public OutputPayload getPCP(Member member, Timestamp dateTIme) {
		OutputPayload outputPayload = null;
		OperationsAuditUpdate operationsAudit = null;

		try {
			asyncService.asyncMemberUpdate(member, dateTIme);

			operationsAudit = new OperationsAuditUpdate();
			
			if (!DateUtils.checkFuture(member.getMemberDob())) {
				
			

			if (StringUtils.isBlank(member.getMemberProductType())
					|| !member.getMemberProductType().matches(Constants.REGEX)
					|| StringUtils.isBlank(member.getMemberProcessingState())
					|| !member.getMemberProcessingState().matches(Constants.REGEX)) {
				retrieveProductType(member);
			}

			String lob = member.getMemberLineOfBusiness();
			member.setMemberLineOfBusiness(lob.substring(0, lob.length() >= 3 ? 3 : lob.length()));

			outputPayload = assignPcp(member, operationsAudit);
		}else{
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INVALID_MBR_DOB,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}

		} catch (Exception exception) {
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			if (ErrorMessages.INVALID_MBR_LOB.equalsIgnoreCase(exception.getMessage())) {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INVALID_MBR_LOB,
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			} else {
				outputPayload = outputPayloadHelper.createErrorPayload(exception.getClass().getSimpleName(),
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			}
		}
		int update = auditService.updateOperationFlow(outputPayload, operationsAudit, member);
		if (update < 1) {
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.AUDIT_TX_UPDATE_FAILURE,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		LOGGER.debug("Exiting smart pcp {}", Constants.EMPTY_STRING);		
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
		}else {
			return outputPayloadHelper.createErrorPayload(ErrorMessages.MARKET_NOT_CONFIGURED,
					ResponseCodes.NO_VALID_PCP, member, 1);
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

			return outputPayloadHelper.createSuccessPaylodAffinity(member, validPCP);
		} else {
			LOGGER.info("No Affinity PCP found for this member {}", Constants.EMPTY_STRING);

			return getPCPM(member, operationsAudit);

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

			return outputPayloadHelper.createSuccessPaylodAffinity(member, validPCP);
		} else {
			LOGGER.info("No Affinity PCP found for this member {}", Constants.EMPTY_STRING);
			return outputPayloadHelper.createErrorPayload(ErrorMessages.NO_VALID_PCP, ResponseCodes.NO_VALID_PCP,
					member, 1);
		}
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws Exception
	 */
	private OutputPayload getPCPM(Member member, OperationsAuditUpdate operationsAudit) throws Exception {

		LOGGER.info("Calling MDO Service to fetch valid PCP {}", Constants.EMPTY_STRING);

		OutputPayload outputPayload;

		ScoringProvider pcp = mdoService.getValidPCP(member);

		if (null != pcp) {
			if (!pcp.isDummyFlag()) {
				operationsAudit.setDrivingDistance(pcp.getDistance());
			}
			outputPayload = outputPayloadHelper.createSuccessPaylodMDO(member, pcp.isDummyFlag(), pcp);
		} else {
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INTERNAL_PROCS_ERR,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		return outputPayload;
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

		if (null != smartPCPrules) {
			long startFetch = System.nanoTime();

			if (null != geocodeRepo.getGeocodeMap() && !geocodeRepo.getGeocodeMap().isEmpty()) {
				Address address = geocodeRepo.getGeocodeMap().get(member.getAddress().getZipCode());
				if (null != address) {
					Address memberAddress = member.getAddress();
					memberAddress.setLatitude(address.getLatitude());
					memberAddress.setLongitude(address.getLongitude());
				}
			}

			long endFetch = System.nanoTime();
			LOGGER.debug("time for query on  GeoCode {} milliseconds", (endFetch - startFetch) / 1000000.0);

			if (null != member.getAddress().getLatitude() && null != member.getAddress().getLongitude()) {
				LOGGER.info("Member geocode zip={}, lat={}, lon={}", member.getAddress().getZipCode(),
						member.getAddress().getLatitude(), member.getAddress().getLongitude());

				operationsAudit.setInvocationOrder(smartPCPrules.getInvocationOrder());

				LOGGER.info("Invocation order obtained from Drools Engine={}", smartPCPrules.getInvocationOrder());

				outputPayload = callServices(member, smartPCPrules.getInvocationOrder(), operationsAudit);
			} else {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INVALID_ZIPCODE,
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			}
		} else {
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INTERNAL_PROCS_ERR,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		return outputPayload;
	}
}
