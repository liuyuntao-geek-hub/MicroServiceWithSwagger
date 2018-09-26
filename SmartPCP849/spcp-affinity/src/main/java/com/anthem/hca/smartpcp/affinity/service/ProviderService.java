package com.anthem.hca.smartpcp.affinity.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.BingInputPayload;
import com.anthem.hca.smartpcp.affinity.model.BingOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.Destination;
import com.anthem.hca.smartpcp.affinity.model.Origin;
import com.anthem.hca.smartpcp.affinity.model.PcpIdWithRank;
import com.anthem.hca.smartpcp.affinity.model.PcpidListOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.ProviderPayload;
import com.anthem.hca.smartpcp.affinity.model.ResourceSet;
import com.anthem.hca.smartpcp.affinity.model.Result;
import com.anthem.hca.smartpcp.affinity.repo.MemberRepo;
import com.anthem.hca.smartpcp.affinity.repo.ProviderRepo;
import com.anthem.hca.smartpcp.affinity.rest.RestClientService;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ProviderService is used to create a PCP list sorted on PIMS ranking and 
 * 			driving distance(aerial distance in case Bing fails) to be sent to Provider Validation.
 * 
 * @author AF65409 
 */
@Service
public class ProviderService {

	@Autowired
	private MemberRepo memberRepo;
	@Autowired
	private ProviderRepo providerRepo;
	@Autowired
	private RestClientService restClientService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param member
	 * @return ProviderPayload
	 * 
	 *    getProviderPayload is used to create a PCP list sorted on PIMS ranking and 
	 *    driving distance(aerial distance in case Bing fails) to be sent to Provider Validation.
	 * 
	 */
	public ProviderPayload getProviderPayload(Member member) {

		ProviderPayload providerPayload = new ProviderPayload();
		try {
			PcpidListOutputPayload pcpidListOutputPayload = getPcpidListOutputPayload(member);

			if (ResponseCodes.SUCCESS.equalsIgnoreCase(pcpidListOutputPayload.getResponseCode())) {
				logger.info("Member Affinity records fetched={}, data={}", pcpidListOutputPayload.getPcpIdWithRankList().size(),pcpidListOutputPayload.getPcpIdWithRankList());

				List<PCP> pcpInfoDtlsList = getPcpList(pcpidListOutputPayload.getPcpIdWithRankList(),
						member);

				if (null != pcpInfoDtlsList && !pcpInfoDtlsList.isEmpty()) {
					logger.info("Provider records fetched={}, data={}", pcpInfoDtlsList.size(),pcpInfoDtlsList);
					Map<String, String> pcpIdRankMap = pcpidListOutputPayload.getPcpIdWithRankList().stream()
							.collect(Collectors.toMap(PcpIdWithRank::getPcpId, PcpIdWithRank::getPcpRank));

					providerPayload = calculateDrivingDistance(pcpInfoDtlsList, member, pcpIdRankMap);

					if(ResponseCodes.SUCCESS.equalsIgnoreCase(providerPayload.getResponseCode())) {

						pcpInfoDtlsList = providerPayload.getProviderPayloadList();
						providerPayload.setProviderPayloadList(pcpInfoDtlsList);
						providerPayload.setResponseCode(ResponseCodes.SUCCESS);
						providerPayload.setResponseMessage(ErrorMessages.SUCCESS);
					}
				}else {
					providerPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
					providerPayload.setResponseMessage(ErrorMessages.NO_PCP_IN_PROVIDER_TABLE);
				}

			} else {
				providerPayload.setResponseCode(pcpidListOutputPayload.getResponseCode());
				providerPayload.setResponseMessage(pcpidListOutputPayload.getResponseMessage());
			}
		} catch (Exception exception) {

			logger.error("Internal Exception occurred while creating ProviderPayloadlist" , exception);

			providerPayload = new ProviderPayload();
			providerPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			providerPayload.setResponseMessage(exception.getClass().getSimpleName());
		}
		return providerPayload;
	}

	/**
	 * @param pcpIdWithRankList, member
	 * @return List<PCP>
	 * 
	 *    getPcpList is used to fetch Provider details from Provider_Info table for the sorted list provided for member.
	 * 
	 */
	private List<PCP> getPcpList(List<PcpIdWithRank> pcpIdWithRankList, Member member) {

		Map<String, String> pcpIdRankMap = pcpIdWithRankList.stream()
				.collect(Collectors.toMap(PcpIdWithRank::getPcpId, PcpIdWithRank::getPcpRank));
		Set<String> pcpIdSet = pcpIdRankMap.keySet();
		long startFetch = System.nanoTime();

		List<PCP> pcpInfoDtlsList = (pcpIdSet == null || pcpIdSet.isEmpty()) ? null
				: providerRepo.getPCPInfoDtlsList(member, pcpIdSet);

		long endFetch = System.nanoTime();
		double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
		logger.debug("time for query on PCP data {} milliseconds", fetchTime);
		logger.debug("List of PCPIDS with Provider details {} ", pcpInfoDtlsList);

		return pcpInfoDtlsList;
	}

	/**
	 * @param pcpInfoDtlsList, member
	 * @return BingOutputPayload
	 * 
	 *    		getBingResponse is used to get Bing response in order to get travel distance and travel time 
	 *    		for the PCP list sent.
	 * 
	 */
	private BingOutputPayload getBingResponse(List<PCP> pcpInfoDtlsList, Member member) {

		BingOutputPayload bingOutputPayload = null;

		BingInputPayload bingInputPayload = new BingInputPayload();

		Origin origin = new Origin(); // Setting the Latitude & Longitude from member to Origin class
		origin.setLatitude(member.getAddress().getLatitude());
		origin.setLongitude(member.getAddress().getLongitude());

		List<Origin> originList = new ArrayList<>();
		originList.add(origin); // Setting the origin to origins List

		bingInputPayload.setOrigins(originList); // Setting the coordinates from Origins class to BingInputPayLoad

		List<Destination> destinationList = new ArrayList<>();

		for (PCP pcp : pcpInfoDtlsList) {
			Destination destination = new Destination();
			destination.setLatitude(pcp.getLatdCordntNbr()); 
			destination.setLongitude(pcp.getLngtdCordntNbr());
			destinationList.add(destination);
		} // Setting the Latitude & Longitude from each PCPInfoDtls Object to Destination class and adding it to destinations List

		bingInputPayload.setDestinations(destinationList); // Setting the List of destinations to BingInputPayLoad
		bingInputPayload.setTravelMode("driving");

		long startFetch = System.nanoTime();		
		bingOutputPayload = restClientService.getDistanceMatrix(bingInputPayload);
		long endFetch = System.nanoTime();
		double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
		logger.debug("time for query on Bing Response {} milliseconds", fetchTime);

		return bingOutputPayload;
	}

	/**
	 * @param pcpInfoDtlsList, member
	 * @return ProviderPayload
	 * 
	 *    calculateDrivingDistance is used to calculate driving distance for the PCP list sent after getting Bing response.
	 *    If Bing fails, calculateAerialDistance is called. 
	 *    It also sort the PCP list based on PIMS ranking and driving or aerial distance for same PIMS ranking. 
	 * 
	 */
	private ProviderPayload calculateDrivingDistance(List<PCP> pcpInfoDtlsList, Member member,
			Map<String, String> pcpIdRankMap) {

		BingOutputPayload bingResponse = null;
		ProviderPayload providerPayload = new ProviderPayload();
		bingResponse = getBingResponse(pcpInfoDtlsList, member);// Getting the Bing Response

		if (null != bingResponse && ResponseCodes.SUCCESS.equalsIgnoreCase(bingResponse.getStatusCode()) && null != bingResponse.getResourceSets()) {
			processBingResponse(pcpInfoDtlsList, bingResponse.getResourceSets(), member, pcpIdRankMap);
		} else {
			calculateAerialDistanceList(pcpInfoDtlsList, member, pcpIdRankMap);
		}

		// Sort PCPs having same Rank based on driving distance
		if (pcpInfoDtlsList.size() > 1) {

			pcpInfoDtlsList.sort((final PCP pcpA,final PCP pcpB) -> (Integer.parseInt(pcpA.getPcpRankgId()) -Integer.parseInt(pcpB.getPcpRankgId())));

			pcpInfoDtlsList.sort((final PCP pcpA,
					final PCP pcpB) -> {
						if(pcpA.getPcpRankgId().equalsIgnoreCase(pcpB.getPcpRankgId())) {
							return (int) ((pcpA.getDistance() - pcpB.getDistance())<0?-1:(pcpA.getDistance() - pcpB.getDistance()));
						}else {
							return 0;
						}
					});
		}

		providerPayload.setProviderPayloadList(pcpInfoDtlsList);
		providerPayload.setResponseCode(ResponseCodes.SUCCESS);

		logger.debug("Final Sorted List of PCPIds {} ", pcpInfoDtlsList);

		if (null == pcpInfoDtlsList.get(0).getAerialDistance()) {
			logger.info("Bing driving distance successful, PCP count={} , data={}", pcpInfoDtlsList != null ? pcpInfoDtlsList.size() : 0, pcpInfoDtlsList);
		} else {
			logger.info("Aerial distance successful, PCP count={} , data={}", pcpInfoDtlsList != null ? pcpInfoDtlsList.size() : 0, pcpInfoDtlsList);
		}
		return providerPayload;
	}

	/**
	 * @param pcpInfoDtlsList, distMatrixResourceSet, member
	 * @return List<PCP>
	 * 
	 *    processBingResponse is used to calculate driving distance for the PCP list sent after getting Bing response.
	 *    If Bing fails, calculateAerialDistance is called. 
	 * 
	 */
	private List<PCP> processBingResponse(List<PCP> pcpInfoDtlsList, List<ResourceSet> distMatrixResourceSet,Member member,
			Map<String, String> pcpIdRankMap) {

		List<Result> resultList; 
		resultList = distMatrixResourceSet.get(0).getResources().get(0).getResults(); 
		int count = 0;
		for (Result result : resultList) {
			PCP pcp = pcpInfoDtlsList.get(count);

			if( -1 != (int) result.getTravelDistance()){
				pcp.setDrivingDistance(Math.round(result.getTravelDistance() * 100) / 100D);
				pcp.setDistance(Math.round(result.getTravelDistance() * 100) / 100D);
			}else {
				pcp = calculateAerialDistance(pcp, member, pcpIdRankMap);
			}
			pcp.setPcpRankgId(pcpIdRankMap.get(pcp.getProvPcpId()));
			pcpInfoDtlsList.set(count, pcp);
			count++;
		}

		return pcpInfoDtlsList;
	}

	/**
	 * @param member
	 * @return PcpidListOutputPayload
	 * 
	 *    getPcpidListOutputPayload is used to fetch PCP list sorted on PIMs ranking for the member payload.  
	 * 
	 */
	private PcpidListOutputPayload getPcpidListOutputPayload(Member member) {
		PcpidListOutputPayload pcpidListOutputPayload = new PcpidListOutputPayload();
		try {
			long startFetch = System.nanoTime();

			List<PcpIdWithRank> pcpIdWithRankList = memberRepo.getPCPIdWithRankList(member);

			long endFetch = System.nanoTime();
			double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
			logger.debug("time for query on Member data {} milliseconds", fetchTime);

			logger.debug("Sorted List of PCPIDS with PIMS Ranking {} ", pcpIdWithRankList);

			if (pcpIdWithRankList == null) {
				pcpidListOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				pcpidListOutputPayload.setResponseMessage(ErrorMessages.NO_PCP_IN_PROVIDER_TABLE);
			}
			else {
				if (!pcpIdWithRankList.isEmpty()) {
					if (!duplicateValues(pcpIdWithRankList)) {
						pcpidListOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
						pcpidListOutputPayload.setResponseMessage(ErrorMessages.MULTIPLE_MCID_IN_MEMBER_TABLE);
					}
					else {
						pcpidListOutputPayload.setResponseCode(ResponseCodes.SUCCESS);
						pcpidListOutputPayload.setPcpIdWithRankList(pcpIdWithRankList);
					}

				} else {
					pcpidListOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
					pcpidListOutputPayload.setResponseMessage(ErrorMessages.NO_PCP_IN_MEMBER_TABLE);
				}				
			}

		} catch (Exception exception) {

			logger.error("Error occurred while fetching PCP list from Member table {} ", exception);
			pcpidListOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			pcpidListOutputPayload.setResponseMessage(exception.getClass().getSimpleName());
		}
		return pcpidListOutputPayload;
	}

	/**
	 * @param pcpInfoDtlsList, member, pcpIdRankMap
	 * @return PcpidListOutputPayload
	 * 
	 *    calculateAerialDistanceList is used to calculate aerial distance for the PCP List.
	 * 
	 */
	public List<PCP> calculateAerialDistanceList(List<PCP> pcpInfoDtlsList, Member member, Map<String, String> pcpIdRankMap){

		for (PCP pcpInfoDtls : pcpInfoDtlsList) {
			calculateAerialDistance(pcpInfoDtls, member, pcpIdRankMap);
		}
		return pcpInfoDtlsList;
	}

	/**
	 * @param pcp, member, pcpIdRankMap
	 * @return PCP
	 * 
	 *    calculateAerialDistance is used to calculate aerial distance for one PCP .
	 * 
	 */
	public PCP calculateAerialDistance(PCP pcp, Member member, Map<String, String> pcpIdRankMap){

		double memberLat = member.getAddress().getLatitude();
		double memberLon = member.getAddress().getLongitude();
		double providerLat = 0;
		double providerLon = 0;

		providerLat = pcp.getLatdCordntNbr();
		providerLon = pcp.getLngtdCordntNbr();

		double theta = memberLon - providerLon;
		double aerialDistance = Math.sin(Math.toRadians(memberLat)) * Math.sin(Math.toRadians(providerLat))
				+ Math.cos(Math.toRadians(memberLat)) * Math.cos(Math.toRadians(providerLat))
				* Math.cos(Math.toRadians(theta));
		aerialDistance = Math.acos(aerialDistance);
		aerialDistance = Math.toDegrees(aerialDistance);
		aerialDistance = aerialDistance * 60 * 1.1515;

		pcp.setAerialDistance(aerialDistance);
		pcp.setDistance(aerialDistance);
		pcp.setPcpRankgId(pcpIdRankMap.get(pcp.getProvPcpId().trim()));

		return pcp;
	}

	/**
	 * @param pcpIdWithRankList
	 * @return boolean
	 * 
	 * 			duplicateValues is used to check whether we got multiple MCID for PCP list.
	 */
	public boolean duplicateValues(List<PcpIdWithRank> pcpIdWithRankList) { 

		List<String> duplicateMcids = new ArrayList<>();
		for(int i=0 ;i< pcpIdWithRankList.size();i++) {
			duplicateMcids.add(pcpIdWithRankList.get(i).getMcid());
		}
		HashSet<String> uniqueMcids = new HashSet<>(duplicateMcids);

		if(duplicateMcids.size()==1 || uniqueMcids.size()==1) {
			return true;
		} 
		return false;
	}
}