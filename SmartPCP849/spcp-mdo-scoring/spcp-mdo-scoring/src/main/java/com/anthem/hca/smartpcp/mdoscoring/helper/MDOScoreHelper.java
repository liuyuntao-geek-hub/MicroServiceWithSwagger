/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - MDOScoreHelper used for calculating scores, calling bing service
 *  			calling tie breaker logic. At the end it will give PCP with highest score
 * 
 * @author AF70896
 * 
 * 
 *
 */
package com.anthem.hca.smartpcp.mdoscoring.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.mdoscoring.service.BingRestClientService;
import com.anthem.hca.smartpcp.mdoscoring.utility.Constant;
import com.anthem.hca.smartpcp.mdoscoring.utility.MDOScoringCalculation;
import com.anthem.hca.smartpcp.mdoscoring.utility.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.utility.TieBreakerUtil;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingInputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingOutputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.Destination;
import com.anthem.hca.smartpcp.mdoscoring.vo.Member;
import com.anthem.hca.smartpcp.mdoscoring.vo.Origin;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class MDOScoreHelper {

	private static final Logger logger = LoggerFactory.getLogger(MDOScoreHelper.class);

	@Autowired
	private BingRestClientService bingRestClientService;

	protected static final List<String> ENG_LANG_CONSTANT = Arrays.asList("ENG");

	public PCP getHighestScorePCP(List<PCP> pcpList, Rules rulesInfo, Member member, PCPAssignmentFlow pcpAssignmentFlow)
			throws JsonProcessingException, ParseException {
		List<PCP> pcpInfo = getAllPCPScore(pcpList, rulesInfo, member, pcpAssignmentFlow);
		long mdoScoreBegin1 = System.nanoTime();
		List<PCP> pcpWithDrivingDistancePoints = getDrivingDistancePoints(pcpInfo, rulesInfo, member, pcpAssignmentFlow);
		long mdoScoreEnd1 = System.nanoTime();
		long timeTaken1 = (mdoScoreEnd1 - mdoScoreBegin1) / 1000000;
		logger.debug("Time Taken for hitting bing service and assigning driving distance scores is {}{} ", timeTaken1,
				"ms");
		PCP finalPCP = getBestPCP(pcpWithDrivingDistancePoints, pcpAssignmentFlow);
		pcpAssignmentFlow.setSelectedPcp(finalPCP);
		return finalPCP;

	}

	public PCP getBestPCP(List<PCP> pcpWithDrivingDistancePoints, PCPAssignmentFlow pcpAssignmentFlow) {
		TieBreakerUtil tieBreaker = new TieBreakerUtil();
		PCP bestPCP;

		Collections.sort(pcpWithDrivingDistancePoints, PCP.totalscore);
		int highestScore = pcpWithDrivingDistancePoints.get(0).getMdoScore();
		List<PCP> highestScorePCPList = new ArrayList<>();
		List<PCP> nearestPCPList;
		List<PCP> vbpPCPList;

		for (PCP pcp : pcpWithDrivingDistancePoints) {
			if (highestScore == pcp.getMdoScore()) {
				highestScorePCPList.add(pcp);
			}
		}
		if (1 < highestScorePCPList.size()) {
			nearestPCPList = tieBreaker.getNearestPCP(highestScorePCPList, pcpAssignmentFlow);

			if (1 < nearestPCPList.size()) {
				vbpPCPList = tieBreaker.getVbpPcp(nearestPCPList, pcpAssignmentFlow);
				bestPCP = vbpLogicValidation(vbpPCPList, nearestPCPList, pcpAssignmentFlow);

			}

			else {
				bestPCP = nearestPCPList.get(0);
			}
		}

		else {
			bestPCP = highestScorePCPList.get(0);
		}

		return bestPCP;
	}

	public PCP vbpLogicValidation(List<PCP> vbpPCPList, List<PCP> nearestPCPList, PCPAssignmentFlow pcpAssignmentFlow) {
		PCP bestPCP;
		List<PCP> leastAssignedPCPList;
		List<PCP> alphabeticallySortedPCPList;
		TieBreakerUtil tieBreaker = new TieBreakerUtil();

		if (1 < vbpPCPList.size()) {
			leastAssignedPCPList = tieBreaker.getleastAssignedPCP(vbpPCPList, pcpAssignmentFlow);

			if (1 < leastAssignedPCPList.size()) {
				alphabeticallySortedPCPList = tieBreaker.getAlphabeticallySortedPCPList(leastAssignedPCPList, pcpAssignmentFlow);
				bestPCP = alphabeticallySortedPCPList.get(0);
			}

			else {
				bestPCP = leastAssignedPCPList.get(0);
			}
		}

		else if (vbpPCPList.isEmpty()) {
			leastAssignedPCPList = tieBreaker.getleastAssignedPCP(nearestPCPList, pcpAssignmentFlow);

			if (1 < leastAssignedPCPList.size()) {
				alphabeticallySortedPCPList = tieBreaker.getAlphabeticallySortedPCPList(leastAssignedPCPList, pcpAssignmentFlow);
				bestPCP = alphabeticallySortedPCPList.get(0);
			}

			else {
				bestPCP = leastAssignedPCPList.get(0);
			}
		} else {
			bestPCP = vbpPCPList.get(0);
		}

		return bestPCP;
	}

	public List<PCP> getDrivingDistancePoints(List<PCP> pcpInfo, Rules rulesInfo, Member member, PCPAssignmentFlow pcpAssignmentFlow)
			throws JsonProcessingException {
		MDOScoringCalculation mdoScoreCalculator = new MDOScoringCalculation();
		List<PCP> filterdPCP;
		BingInputPayload bingInput = new BingInputPayload();
		List<Origin> origins = new ArrayList<>();
		Origin memberCrdnt = new Origin();
		memberCrdnt.setLatitude(member.getAddress().getLatitude());
		memberCrdnt.setLongitude(member.getAddress().getLongitude());
		origins.add(memberCrdnt);
		bingInput.setOrigins(origins);

		List<Destination> destinations = new ArrayList<>();
		Destination pcpCrdnt;
		BingOutputPayload bingResponse;
		for (PCP pcp : pcpInfo) {
			pcpCrdnt = new Destination();
			pcpCrdnt.setLatitude(pcp.getLatdCordntNbr());
			pcpCrdnt.setLongitude(pcp.getLngtdCordntNbr());
			destinations.add(pcpCrdnt);
		}
		bingInput.setDestinations(destinations);
		bingInput.setTravelMode(Constant.DRIVING_MODE);
		bingInput.setDistanceUnit("mi");
		bingResponse = new BingOutputPayload();
		try {
			long mdoScoreBegin1 = System.nanoTime();
			bingResponse = bingRestClientService.getDistanceMatrix(bingInput);
			long mdoScoreEnd1 = System.nanoTime();
			long timeTaken1 = (mdoScoreEnd1 - mdoScoreBegin1) / 1000000;
			logger.debug("Time Taken for hitting bing service is {}{} ", timeTaken1, "ms");
		} catch (Exception e) {
			bingResponse.setStatusCode(Integer.parseInt(ResponseCodes.OTHER_EXCEPTIONS));

		}
		if (Integer.parseInt(Constant.SUCCESS_CODE) == (bingResponse.getStatusCode())) {
			List<ResourceSet> distMatrixResourceSet = bingResponse.getResourceSets();

			try {
				filterdPCP = mdoScoreCalculator.drivingDistanceTotalScore(distMatrixResourceSet, pcpInfo, rulesInfo);
				if (filterdPCP.isEmpty()) {
					filterdPCP = drivingDistanceFallbackMethod(pcpInfo);
				}
			} catch (Exception e) {
				filterdPCP = drivingDistanceFallbackMethod(pcpInfo);
			}
		}

		else {
			filterdPCP = drivingDistanceFallbackMethod(pcpInfo);
		}
		return filterdPCP;
	}

	public List<PCP> drivingDistanceFallbackMethod(List<PCP> pcpInfo) {

		List<PCP> validatedProviderList = pcpInfo.stream().filter(pcp -> {
			boolean flag = false;
			if (null != pcp.getAerialDistance()) {
				flag = true;
			}
			return flag;
		}).collect(Collectors.toList());
		Collections.sort(validatedProviderList, PCP.aerialDistanceComprtr);
		logger.debug("Setting Driving distance equals{}to Aerial distance", " ");
		for (PCP pcp : validatedProviderList) {
			pcp.setDrivingDistance((double) pcp.getAerialDistance());
		}

		return validatedProviderList;
	}
	
	public List<PCP> getAllPCPScore(List<PCP> pcpList, Rules rulesInfo, Member member, PCPAssignmentFlow pcpAssignmentFlow) throws ParseException {

		MDOScoringCalculation mdoScoreCal = new MDOScoringCalculation();
		List<PCP> updatedInfo = new ArrayList<>();
		for (PCP pcpInfo : pcpList) {
			int totScore = 0;
			if (null != member.getMemberLanguageCode() && (!ENG_LANG_CONSTANT.equals(member.getMemberLanguageCode()))
					&& null != pcpInfo.getPcpLang()) {
				totScore = totScore + mdoScoreCal.langTotalScore(pcpInfo, member, rulesInfo);
			}
			totScore = totScore + mdoScoreCal.bonusTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.specialtyTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.vbpTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.aerialTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.rankTotalScore(pcpInfo, rulesInfo);
			pcpInfo.setMdoScore(totScore);
			updatedInfo.add(pcpInfo);
		}
		Collections.sort(updatedInfo, PCP.totalscore);
		pcpAssignmentFlow.setScoredSortedPoolPcps(updatedInfo);
		int value = 50;
		if (value >= updatedInfo.size()) {
			value = updatedInfo.size();
		}
		ArrayList<PCP> pcpSortedInfo = new ArrayList<>();
		pcpSortedInfo.addAll(updatedInfo.subList(0, value));
		List<PCP> pcpInfos = pcpSortedInfo.stream().map(this::getClonedPCP).collect(Collectors.toList());
		pcpAssignmentFlow.setDrivingDistScoredPcps(pcpInfos);
		return pcpInfos;
	}
	
	private PCP getClonedPCP(PCP oldPcp) {
		PCP newPcp = new PCP();
		BeanUtils.copyProperties(oldPcp, newPcp);
		return newPcp;
	}

}