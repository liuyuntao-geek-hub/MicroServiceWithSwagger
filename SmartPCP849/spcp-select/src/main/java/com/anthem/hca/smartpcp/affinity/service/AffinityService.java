package com.anthem.hca.smartpcp.affinity.service;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.repo.MemberRepo;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * Copyright Â© 2018 Anthem, Inc.
 * 
 * AffinityService is used to prepare output payload for Affinity micro-service
 * for member information provided from SMART PCP. The O/P payload contains
 * final validated PCP with details like its regional network and driving
 * distance. This service internally calls affinityProviderValidationService to
 * first get rules and validated PCP.
 * 
 * @author Khushbu Jain AF65409
 */
/**
 * @author AF74173
 *
 */
@Service
public class AffinityService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private AffinityProviderValidationService affinityProviderValidationService;

	@Autowired
	private ProviderValidationHelper providerValidationHelper;

	@Autowired
	private OperationAuditFlowService operationAuditFlowService;

	/**
	 * @param member
	 *            The Member JSON Body.
	 * @return AffinityOutPayload O/p with PCP and other details.
	 * @throws JsonProcessingException
	 *             Exception when creating Affinity O/P
	 * 
	 *             getAffinityOutPayload is used to prepare output payload for
	 *             Affinity micro-service for member information provided from
	 *             SMART PCP. The O/P payload contains final validated PCP with
	 *             details like its regional network and driving distance. This
	 *             service internally calls affinityProviderValidationService to
	 *             first get rules and validated PCP.
	 * @throws DroolsParseException
	 * 
	 */
	public Provider getAffinityOutPayload(Member member) throws JsonProcessingException, DroolsParseException {

		Provider validPCP = null;
		List<Provider> providerList = null;
		boolean mcidFlag = false;
		if (StringUtils.isNotBlank(member.getMemberFirstName()) && StringUtils.isNotBlank(member.getMemberEid())
				&& StringUtils.isNotBlank(member.getMemberSequenceNumber())) {
			providerList = memberRepo.getPCPsForAffinity(member);
			if (null != providerList && !providerList.isEmpty()) {
				mcidFlag = multipleMCIDCheck(providerList);
				if (!mcidFlag) {
					calculateAerialDistance(providerList, member);
					logger.info("Provider records fetched={}, data={}", providerList.size(), providerList);
					validPCP = getProviderValidationOutputPayload(member, providerList);

				}
			}
		}
		operationAuditFlowService.insertOperationFlowAffinity(providerList, validPCP, mcidFlag);

		return validPCP;
	}

	/**
	 * @param member,
	 *            providerPayloadList Member JSON Body, List of Provider with
	 *            provider and distance details.
	 * @return Provider Output after validation.
	 * @throws JsonProcessingException
	 *             Exception when forming Provider Validation input.
	 * 
	 *             getProviderValidationOutputPayload call for
	 *             providerValidation Rules internally from Drool Rule Engine
	 *             micro-service and get rules for the Providers. Then it sends
	 *             member, providerPayloadList and rules to Provider validation
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

	/**
	 * @param pcp,
	 *            member, pcpIdRankMap Provider,Member JSON Body,Provider with
	 *            Rank Map.
	 * @return Provider Provider with aerial distance.
	 * 
	 *         calculateAerialDistance is used to calculate aerial distance for
	 *         one Provider .
	 * 
	 */
	public List<Provider> calculateAerialDistance(List<Provider> providersList, Member member) {

		for (Provider pcp : providersList) {
			double memberLat = member.getAddress().getLatitude();
			double memberLon = member.getAddress().getLongitude();
			double providerLat = 0;
			double providerLon = 0;

			providerLat = pcp.getAddress().getLatitude();
			providerLon = pcp.getAddress().getLongitude();

			double theta = memberLon - providerLon;
			double aerialDistance = Math.sin(Math.toRadians(memberLat)) * Math.sin(Math.toRadians(providerLat))
					+ Math.cos(Math.toRadians(memberLat)) * Math.cos(Math.toRadians(providerLat))
							* Math.cos(Math.toRadians(theta));
			aerialDistance = Math.acos(aerialDistance);
			aerialDistance = Math.toDegrees(aerialDistance);
			aerialDistance = aerialDistance * 60 * 1.1515;

			pcp.setDistance(aerialDistance);
		}

		sortPcpListBasedOnDistance(providersList);
		logger.debug("Final Sorted List of PCPIds {} ", providersList);
		return providersList;
	}

	/**
	 * @param pcpInfoDtlsList
	 *            List of Provider with Provider details with driving or aerial
	 *            distance.
	 * @return List<Provider> List of Provider with Provider details with sorted
	 *         driving or aerial distance.
	 * 
	 *         sortPcpListBasedOnDistance sort the Provider list first based on
	 *         PIMS ranking then driving or aerial distance for same PIMS
	 *         ranking.
	 * 
	 */
	private List<Provider> sortPcpListBasedOnDistance(List<Provider> pcpInfoDtlsList) {

		// Sort PCPs having same Rank based on driving or aerial distance
		if (pcpInfoDtlsList.size() > 1) {

			pcpInfoDtlsList.sort((final Provider pcpA, final Provider pcpB) -> (pcpA.getRank() - pcpB.getRank()));

			pcpInfoDtlsList.sort((final Provider pcpA, final Provider pcpB) -> {
				if (pcpA.getRank() == pcpB.getRank()) {
					return (int) ((pcpA.getDistance() - pcpB.getDistance()) < 0 ? -1
							: (pcpA.getDistance() - pcpB.getDistance()));
				} else {
					return 0;
				}
			});
		}

		return pcpInfoDtlsList;
	}

	/**
	 * @param pcpInfoDtlsList
	 * @return boolean value duplicateValues is used to check whether we got
	 *         multiple MCID for Provider list sent.
	 */
	public boolean multipleMCIDCheck(List<Provider> pcpInfoDtlsList) {

		boolean mcidFlag = false;
		HashSet<String> duplicateMcids = new HashSet<>();

		int recordCount = pcpInfoDtlsList.size();
		for (int i = 0; i < recordCount; i++) {
			duplicateMcids.add(pcpInfoDtlsList.get(i).getMemberMcid());
		}
		if (duplicateMcids.size() > 1) {
			mcidFlag = true;
		}
		return mcidFlag;
	}

}