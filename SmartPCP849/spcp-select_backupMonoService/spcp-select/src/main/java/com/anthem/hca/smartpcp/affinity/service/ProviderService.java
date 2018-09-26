package com.anthem.hca.smartpcp.affinity.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.repo.MemberRepo;
import com.anthem.hca.smartpcp.affinity.repo.ProviderRepo;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.PcpIdWithRank;
import com.anthem.hca.smartpcp.model.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * ProviderService is used to create a Provider list sorted on PIMS ranking and
 * driving distance(aerial distance in case Bing fails) to be sent to Provider
 * Validation. It internally calls MemberRepo for list of Provider, ProviderRepo
 * for details of Provider list and RestClientService to call Bing and get
 * driving distance(aerial distance in case Bing fails).
 * 
 * @author Khushbu Jain AF65409
 */
@Service
public class ProviderService {

	@Autowired
	private MemberRepo memberRepo;
	@Autowired
	private ProviderRepo providerRepo;


	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param member
	 *            The Member JSON Body.
	 * @return ProviderPayload List of Provider with provider details and driving
	 *         distance b/w member and PCPs.
	 * @throws JsonProcessingException
	 *             Exception when creating Provider List.
	 * 
	 * 
	 *             getProviderPayload is used to create a Provider list sorted on
	 *             PIMS ranking and driving distance(aerial distance in case Bing
	 *             fails) to be sent to Provider Validation. It internally calls
	 *             MemberRepo for list of Provider, ProviderRepo for details of
	 *             Provider list and RestClientService to call Bing and get driving
	 *             distance(aerial distance in case Bing fails).
	 * 
	 */
	public List<Provider> getProviderPayload(Member member) throws JsonProcessingException {

		long startFetch = System.nanoTime();
		List<Provider> pcpInfoDtlsList = null;

		List<PcpIdWithRank> pcpIdWithRankList = memberRepo.getPCPIdWithRankList(member);
		long endFetch = System.nanoTime();
		double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
		logger.debug("time for query on Member to Provider data {} milliseconds", fetchTime);

		if (null != pcpIdWithRankList && !pcpIdWithRankList.isEmpty() && !duplicateValues(pcpIdWithRankList)) {
			logger.info("Member Affinity records fetched={}, data={}", pcpIdWithRankList.size(), pcpIdWithRankList);

			pcpInfoDtlsList = getPcpList(pcpIdWithRankList, member);

			if (null != pcpInfoDtlsList && !pcpInfoDtlsList.isEmpty()) {
				logger.info("Provider records fetched={}, data={}", pcpInfoDtlsList.size(), pcpInfoDtlsList);

				Map<String, String> pcpIdRankMap = pcpIdWithRankList.stream()
						.collect(Collectors.toMap(PcpIdWithRank::getPcpId, PcpIdWithRank::getPcpRank));

				calculateDrivingDistance(pcpInfoDtlsList, member, pcpIdRankMap);

			}
		}
		return pcpInfoDtlsList;
	}

	/**
	 * @param pcpIdWithRankList
	 *            List of Provider with details like Provider, TIN, NPI, MCID and
	 *            PIMS Ranking.
	 * @return boolean Boolean value for multiple MCID for Provider list sent.
	 * 
	 *         duplicateValues is used to check whether we got multiple MCID for
	 *         Provider list sent.
	 */
	public boolean duplicateValues(List<PcpIdWithRank> pcpIdWithRankList) { 

		HashSet<String> duplicateMcids = new HashSet<>();
		int recordCount  = pcpIdWithRankList.size();
		for(int i=0 ;i<recordCount;i++) {
			duplicateMcids.add(pcpIdWithRankList.get(i).getMcid());
		}

		return (duplicateMcids.size()>1);
	}

	/**
	 * @param pcpIdWithRankList
	 *            List of Provider from MemberRepo,
	 * @param member
	 *            Member JSON Body.
	 * @return List<Provider> List of Provider with Provider details.
	 * 
	 *         getPcpList is used to fetch Provider details from PROVIDER_INFO view
	 *         for the list of affinity Provider provided for member.
	 * 
	 */
	private List<Provider> getPcpList(List<PcpIdWithRank> pcpIdWithRankList, Member member) {

		Map<String, String> pcpIdRankMap = pcpIdWithRankList.stream()
				.collect(Collectors.toMap(PcpIdWithRank::getPcpId, PcpIdWithRank::getPcpRank));
		Set<String> pcpIdSet = pcpIdRankMap.keySet();

		long startFetch = System.nanoTime();

		List<Provider> pcpInfoDtlsList = (pcpIdSet == null || pcpIdSet.isEmpty()) ? null
				: providerRepo.getPCPInfoDtlsList(member, pcpIdSet);

		long endFetch = System.nanoTime();
		double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
		logger.debug("time for query on Provider data {} milliseconds", fetchTime);

		return pcpInfoDtlsList;
	}

	/**
	 * @param pcpInfoDtlsList
	 *            List of Provider with Provider details
	 * @param member
	 *            Member JSON Body
	 * @param pcpIdRankMap
	 *            Provider with Rank Map
	 * @return ProviderPayload List of Provider with Provider details.
	 * @throws JsonProcessingException
	 *             Exception when calculating Driving Distance.
	 * 
	 *             calculateDrivingDistance is used to calculate driving distance
	 *             for the Provider list sent after getting Bing response. If Bing
	 *             fails, calculateAerialDistance is called. It internally sort the
	 *             Provider list first based on PIMS ranking then driving or aerial
	 *             distance for same PIMS ranking.
	 * 
	 */
	private List<Provider> calculateDrivingDistance(List<Provider> pcpInfoDtlsList, Member member,
			Map<String, String> pcpIdRankMap) throws JsonProcessingException {

		calculateAerialDistanceList(pcpInfoDtlsList, member, pcpIdRankMap);
		sortPcpListBasedOnDistance(pcpInfoDtlsList);

		logger.debug("Final Sorted List of PCPIds {} ", pcpInfoDtlsList);

		if (null == pcpInfoDtlsList.get(0).getAerialDistance()) {
			logger.info("Bing driving distance successful, Provider count={} , data={}",
					!pcpInfoDtlsList.isEmpty() ? pcpInfoDtlsList.size() : 0, pcpInfoDtlsList);
		} else {
			logger.info("Aerial distance successful, Provider count={} , data={}",
					!pcpInfoDtlsList.isEmpty() ? pcpInfoDtlsList.size() : 0, pcpInfoDtlsList);
		}

		return pcpInfoDtlsList;
	}

	/**
	 * @param pcpInfoDtlsList
	 *            List of Provider with Provider details with driving or aerial
	 *            distance.
	 * @return List<Provider> List of Provider with Provider details with sorted
	 *         driving or aerial distance.
	 * 
	 *         sortPcpListBasedOnDistance sort the Provider list first based on PIMS
	 *         ranking then driving or aerial distance for same PIMS ranking.
	 * 
	 */
	private List<Provider> sortPcpListBasedOnDistance(List<Provider> pcpInfoDtlsList) {

		// Sort PCPs having same Rank based on driving or aerial distance
		if (pcpInfoDtlsList.size() > 1) {

			pcpInfoDtlsList.sort((final Provider pcpA, final Provider pcpB) -> (pcpA.getPimsRank() - pcpB.getPimsRank()));

			pcpInfoDtlsList.sort((final Provider pcpA, final Provider pcpB) -> {
				if (pcpA.getPimsRank() == pcpB.getPimsRank()) {
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
	 * @param pcpInfoDtlsList,
	 *            member List of Provider with provider details,Member JSON Body.
	 * @param pcpIdRankMap
	 *            Provider with Rank Map.
	 * @return List<Provider> List of Provider with provider details and aerial
	 *         distance.
	 * 
	 *         calculateAerialDistanceList is used to calculate aerial distance for
	 *         the Provider List.
	 * 
	 */
	public List<Provider> calculateAerialDistanceList(List<Provider> pcpInfoDtlsList, Member member,
			Map<String, String> pcpIdRankMap) {

		for (Provider pcpInfoDtls : pcpInfoDtlsList) {
			calculateAerialDistance(pcpInfoDtls, member, pcpIdRankMap);
		}
		return pcpInfoDtlsList;
	}

	/**
	 * @param pcp,
	 *            member, pcpIdRankMap Provider,Member JSON Body,Provider with Rank
	 *            Map.
	 * @return Provider Provider with aerial distance.
	 * 
	 *         calculateAerialDistance is used to calculate aerial distance for one
	 *         Provider .
	 * 
	 */
	public Provider calculateAerialDistance(Provider pcp, Member member, Map<String, String> pcpIdRankMap) {

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

		pcp.setAerialDistance(aerialDistance);
		pcp.setDistance(aerialDistance);
		pcp.setPimsRank(Integer.parseInt(pcpIdRankMap.get(pcp.getProvPcpId().trim())));
		logger.info(" Calculated aerial distance ");

		return pcp;
	}
}