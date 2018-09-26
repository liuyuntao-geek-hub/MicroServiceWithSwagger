package com.anthem.hca.smartpcp.affinity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * Copyright Â© 2018 Anthem, Inc.
 * 
 * AffinityService is used to prepare output payload for Affinity micro-service
 * for member information provided from SMART PCP. The O/P payload contains
 * final validated PCP with details like its regional network and driving
 * distance. This service internally calls 2 service i.e. ProviderService to get
 * the list of affinity PCP with their details and RestClientPayloadService to
 * first get rules and validated PCP.
 * 
 * @author Khushbu Jain AF65409
 */
@Service
public class AffinityService {

	@Autowired
	private ProviderService providerService;

	@Autowired
	private AffinityProviderValidationService affinityProviderValidationService;

	@Autowired
	private ProviderValidationHelper providerValidationHelper;

	/**
	 * @param member
	 *            The Member JSON Body.
	 * @return AffinityOutPayload O/p with PCP and other details.
	 * @throws JsonProcessingException
	 *             Exception when creating Affinity O/P
	 * 
	 *             getAffinityOutPayload is used to prepare output payload for
	 *             Affinity micro-service for member information provided from SMART
	 *             PCP. The O/P payload contains final validated PCP with details
	 *             like its regional network and driving distance. This service
	 *             internally calls 2 service i.e. ProviderService to get the list
	 *             of affinity PCP with their details and RestClientPayloadService
	 *             to first get rules and validated PCP.
	 * @throws DroolsParseException
	 * 
	 */
	public Provider getAffinityOutPayload(Member member) throws JsonProcessingException, DroolsParseException {

		Provider validPCP = null;

		List<Provider> providerList = providerService.getProviderPayload(member);

		if (null != providerList && !providerList.isEmpty()) {

			validPCP = getProviderValidationOutputPayload(member, providerList);
		}
		return validPCP;
	}

	/**
	 * @param member,
	 *            providerPayloadList Member JSON Body, List of Provider with
	 *            provider and distance details.
	 * @return ProviderValidationOutPayload O/P from Provider Validation MS.
	 * @throws JsonProcessingException
	 *             Exception when forming Provider Validation input.
	 * 
	 *             getProviderValidationOutputPayload call getProviderValidRules
	 *             method internally which call Drool Rule Engine micro-service and
	 *             get rules for the Providers. Then it sends member,
	 *             providerPayloadList and rules to Provider validation
	 *             micro-service to get one valid Provider.
	 * @throws DroolsParseException
	 * 
	 */
	public Provider getProviderValidationOutputPayload(Member member, List<Provider> providerPayloadList)
			throws JsonProcessingException, DroolsParseException {

		Provider validPCP = null;

		ProviderValidationRules providerValidationRules = affinityProviderValidationService.getRules(member);
		if (null != providerValidationRules) {
			validPCP = providerValidationHelper.getPCPForAffinity(member, providerPayloadList, providerValidationRules);
		}
		return validPCP;
	}

	/*
	 * @Bean public ProviderValidationHelper providerValidationHelper(){ return new
	 * ProviderValidationHelper(); }
	 */
}