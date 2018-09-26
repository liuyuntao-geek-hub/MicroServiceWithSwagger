package com.anthem.hca.smartpcp.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.constants.ProductPlanConstants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.helper.BingHelper;
import com.anthem.hca.smartpcp.helper.PayloadHelper;
import com.anthem.hca.smartpcp.model.AffinityOutPayload;
import com.anthem.hca.smartpcp.model.MDOOutputPayload;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.SmartPCPRulesOutputPayload;
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
 * @copyright 
 * @Decription
 * @version
 * @author AF71111
 */
@Service
public class SmartPCPService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartPCPService.class);

	@Autowired
	private OperationAuditService auditService;

	@Autowired
	private RestClientService clientService;
	
	@Autowired
	private AsyncService asyncService;

	@Autowired
	private BingHelper bingHelper;

	@Autowired
	private OutputPayloadService outputPayloadHelper;

	@Autowired
	private ProductTypeRepo productTypeRepo;

	@Autowired
	private PayloadHelper payloadHelper;

	@Value("${spcp.bing.url}")
	private String bingUrl;

	@Value("${spcp.bing.key}")
	private String bingKey;

	/**
	 * @param member
	 * @return OutputPayload
	 * 
	 * Description of the function:
	 * 	Key Decision
	 *  Main  
	 * 
	 * @throws
	 * @see 
	 * 
	 */
	public OutputPayload getPCP(Member member) {
		OutputPayload outputPayload = null;
		OperationsAuditUpdate operationsAudit = null;
		int insert = 0;
		try {
			asyncService.asyncMemberUpdate(member);
		
			operationsAudit = new OperationsAuditUpdate();
			insert = auditService.insertOperationFlow(member);
			
			String lob = member.getMemberLineOfBusiness();
			member.setMemberLineOfBusiness(lob.substring(0,lob.length() >= 3 ? 3 : lob.length()));

			if (insert < 1) {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.AUDIT_TX_UPDATE_FAILURE,
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			} else {
				if (StringUtils.isBlank(member.getMemberProductType())|| !member.getMemberProductType().matches(Constants.REGEX)
						|| StringUtils.isBlank(member.getMemberProcessingState()) || !member.getMemberProcessingState().matches(Constants.REGEX)) {
					retrieveProductType(member);
				}
				
				
				outputPayload = assignPcp(member,operationsAudit);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			outputPayload = outputPayloadHelper.createErrorPayload(exception.getClass().getSimpleName(),
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		if (insert >0) {
			int update = auditService.updateOperationFlow(outputPayload, operationsAudit,
					member.getMemberProductType(), member.getMemberProcessingState());
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
	private OutputPayload callServices(Member member, String order,OperationsAuditUpdate operationsAudit) throws JsonProcessingException {

		if (Constants.AM.equalsIgnoreCase(order)) {
			return getPCPAM(member,operationsAudit);
		} else if (Constants.MA.equalsIgnoreCase(order)) {
			return getPCPMA(member,operationsAudit);
		} else if (Constants.M.equalsIgnoreCase(order)) {
			return getPCPM(member,operationsAudit);
		} else if (Constants.A.equalsIgnoreCase(order)) {
			return getPCPA(member,operationsAudit);
		} else {
			return outputPayloadHelper.createErrorPayload(ErrorMessages.INVALID_DROOLS_RESP,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	private OutputPayload getPCPAM(Member member,OperationsAuditUpdate operationsAudit) throws JsonProcessingException {
		
		LOGGER.info("Calling Affinity Service to fetch valid PCP {}", Constants.EMPTY_STRING);
		
		AffinityOutPayload responsePayload = clientService.getPCPAffinity(payloadHelper.createAffinityPayload(member));
		if (null != responsePayload && (ResponseCodes.SUCCESS.equals(responsePayload.getResponseCode()))
				&& (null != responsePayload.getPcpId()) && (!responsePayload.getPcpId().isEmpty())) {
			operationsAudit.setDrivingDistance(responsePayload.getDrivingDistance());
			
			return outputPayloadHelper.createSuccessPaylod(Constants.REPORTING_CODE_AFFINITY,
					Constants.REPORTING_TEXT_AFFINITY, member, responsePayload.getPcpId(), false,
					responsePayload.getRgnlNtwrkId(), 0);
		} else {
			LOGGER.info("No Affinity PCP found for this member {}", Constants.EMPTY_STRING);
			return getPCPM(member,operationsAudit);
		}

	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	private OutputPayload getPCPMA(Member member,OperationsAuditUpdate operationsAudit) throws JsonProcessingException {

		OutputPayload outputPayload;

		MDOOutputPayload responsePayload = clientService.getPCPMDO(payloadHelper.createMDOPayload(member));
		if (null != responsePayload && (ResponseCodes.SUCCESS.equals(responsePayload.getResponseCode()))
				&& null == responsePayload.getPcpId()) {
			LOGGER.info("No MDO PCP found for this member {}", Constants.EMPTY_STRING);
			return getPCPA(member,operationsAudit);
		} else if (null != responsePayload && (null != responsePayload.getPcpId())
				&& (!responsePayload.getPcpId().isEmpty())) {
			operationsAudit.setDrivingDistance(responsePayload.getDrivingDistance());
			outputPayload = outputPayloadHelper.createSuccessPaylod(
					responsePayload.isDummyFlag() ? Constants.REPORTING_CODE_DEFAULT : Constants.REPORTING_CODE_MDO,
					responsePayload.isDummyFlag() ? Constants.REPORTING_TEXT_DEFAULT : Constants.REPORTING_TEXT_MDO,
					member, responsePayload.getPcpId(), responsePayload.isDummyFlag(), responsePayload.getPcpNtwrkId(),
					responsePayload.getMdoScore());
		} else if (null != responsePayload) {
			outputPayload = outputPayloadHelper.createErrorPayload(responsePayload.getResponseMessage(),
					responsePayload.getResponseCode(), member, 1);
		} else {
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INTERNAL_PROCS_ERR,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		return outputPayload;
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	private OutputPayload getPCPM(Member member,OperationsAuditUpdate operationsAudit) throws JsonProcessingException {

		LOGGER.info("Calling MDO Service to fetch valid PCP {}", Constants.EMPTY_STRING);
		
		OutputPayload outputPayload;
		
		MDOOutputPayload responsePayload = clientService.getPCPMDO(payloadHelper.createMDOPayload(member));
		
		if (null != responsePayload && (ResponseCodes.SUCCESS.equals(responsePayload.getResponseCode()))) {
			if (!responsePayload.isDummyFlag()) {
				operationsAudit.setDrivingDistance(responsePayload.getDrivingDistance());
			}
			outputPayload = outputPayloadHelper.createSuccessPaylod(
					responsePayload.isDummyFlag() ? Constants.REPORTING_CODE_DEFAULT : Constants.REPORTING_CODE_MDO,
					responsePayload.isDummyFlag() ? Constants.REPORTING_TEXT_DEFAULT : Constants.REPORTING_TEXT_MDO,
					member, responsePayload.getPcpId(), responsePayload.isDummyFlag(), responsePayload.getPcpNtwrkId(),
					responsePayload.getMdoScore());
		} else if (null != responsePayload) {
			outputPayload = outputPayloadHelper.createErrorPayload(responsePayload.getResponseMessage(),
					responsePayload.getResponseCode(), member, 1);
		} else {
			outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.INTERNAL_PROCS_ERR,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		return outputPayload;
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	private OutputPayload getPCPA(Member member,OperationsAuditUpdate operationsAudit) throws JsonProcessingException {

		OutputPayload outputPayload;

		AffinityOutPayload responsePayload = clientService.getPCPAffinity(payloadHelper.createAffinityPayload(member));
		if (null != responsePayload && (ResponseCodes.SUCCESS.equals(responsePayload.getResponseCode()))) {
			operationsAudit.setDrivingDistance(responsePayload.getDrivingDistance());
			outputPayload = outputPayloadHelper.createSuccessPaylod(Constants.REPORTING_CODE_AFFINITY,
					Constants.REPORTING_TEXT_AFFINITY, member, responsePayload.getPcpId(), false,
					responsePayload.getRgnlNtwrkId(), 0);
		} else if (null != responsePayload) {
			outputPayload = outputPayloadHelper.createErrorPayload(responsePayload.getResponseMessage(),
					responsePayload.getResponseCode(), member, 1);
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
		
		if (attributes.isEmpty() && (StringUtils.isBlank(member.getMemberProductType()) || !member.getMemberProductType().matches(Constants.REGEX))) {
			String productType = productTypeRepo.getProductTypeProd(member.getMemberProduct());
			if (StringUtils.isBlank(productType)) {
				member.setMemberProductType(Constants.PRODUCT_TYPE_DEFAULT);
			} else {
				member.setMemberProductType(productType);
			}
		} else {
			if (StringUtils.isBlank(member.getMemberProductType()) || !member.getMemberProductType().matches(Constants.REGEX)) {
				member.setMemberProductType(attributes.get(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL));
			}
			if (StringUtils.isBlank(member.getMemberProcessingState()) || !member.getMemberProcessingState().matches(Constants.REGEX)) {
				member.setMemberProcessingState(attributes.get(ProductPlanConstants.PLAN_ST_CD_LABEL));
			}
		}
		
		if (StringUtils.isBlank(member.getMemberProcessingState()) || !member.getMemberProcessingState().matches(Constants.REGEX)) {
			member.setMemberProcessingState(member.getAddress().getState());
		}
		
		return member;
	}

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws IOException
	 */
	private OutputPayload assignPcp(Member member,OperationsAuditUpdate operationsAudit) throws IOException {

		OutputPayload outputPayload;

		LOGGER.debug("Forming Drools Rule Engine payload {}", Constants.EMPTY_STRING);
		
		SmartPCPRulesOutputPayload responsePayload = clientService
				.getInvocationOrder(payloadHelper.createDroolsPayload(member));
		
		if (null != responsePayload
				&& (ResponseCodes.SUCCESS.equals(Integer.toString(responsePayload.getResponseCode())))) {
		
			String bingResponse = clientService
					.getGeocodes(bingHelper.prepareDynamicURL(member.getAddress(), bingUrl, bingKey));
			
			if (ResponseCodes.SERVICE_NOT_AVAILABLE.equalsIgnoreCase(bingResponse)) {
				outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.BING_DOWN,
						ResponseCodes.SERVICE_NOT_AVAILABLE, member, 1);
				
				LOGGER.error("Error occured while invoking bing service {}", ResponseCodes.SERVICE_NOT_AVAILABLE);
			} else {
				bingHelper.retrieveGeocode(bingResponse, member);

				if (null != member.getAddress().getLatitude() && null != member.getAddress().getLongitude()) {
					LOGGER.info("Bing member geocode zip={}, lat={}, lon={}", member.getAddress().getZipCode(), member.getAddress().getLatitude(),member.getAddress().getLongitude());
					
					operationsAudit.setInvocationOrder(responsePayload.getRules().getInvocationOrder());
					
					LOGGER.info("Invocation order obtained from Drools Engine={}", responsePayload.getRules().getInvocationOrder());
					
					outputPayload = callServices(member, responsePayload.getRules().getInvocationOrder(),operationsAudit);
				} else {
					outputPayload = outputPayloadHelper.createErrorPayload(ErrorMessages.BING_SERVICE_ERROR,
							ResponseCodes.OTHER_EXCEPTIONS, member, 1);
				}
			}
		} else if (null != responsePayload) {
			outputPayload = outputPayloadHelper.createErrorPayload(responsePayload.getResponseMessage(),
					Integer.toString(responsePayload.getResponseCode()), member, 1);
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

	@Bean
	public PayloadHelper payloadHelper() {
		return new PayloadHelper();
	}

	@Bean
	public BingHelper bingHelper() {
		return new BingHelper();
	}
}
