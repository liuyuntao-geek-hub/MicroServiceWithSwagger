package com.anthem.hca.smartpcp.mdoscoring.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.service.BingRestClientService;
import com.anthem.hca.smartpcp.mdoscoring.utility.Constant;
import com.anthem.hca.smartpcp.mdoscoring.utility.MDOScoringCalculation;
import com.anthem.hca.smartpcp.mdoscoring.utility.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.utility.TieBreakerUtil;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingInputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.BingOutputPayload;
import com.anthem.hca.smartpcp.mdoscoring.vo.Destination;
import com.anthem.hca.smartpcp.mdoscoring.vo.Origin;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class MDOScoreHelper {

	private static final Logger logger = LoggerFactory.getLogger(MDOScoreHelper.class);

	@Autowired
	private BingRestClientService bingRestClientService;

	protected static final List<String> ENG_LANG_CONSTANT = Arrays.asList("ENG");

	public PCP getHighestScorePCP(List<PCP> pcpList, Rules rulesInfo, Member member)
			throws JsonProcessingException, ParseException {

		logger.info("executing Scores for{}PCP", " ");
		long mdoScoreBegin = System.nanoTime();
		List<PCP> pcpInfo = getAllPCPScore(pcpList, rulesInfo, member);
		long mdoScoreEnd = System.nanoTime();
		long timeTaken = (mdoScoreEnd - mdoScoreBegin) / 1000000;
		logger.info("Time Taken for calculating total scores for all providers is {}{} ", timeTaken, "ms");

		List<PCP> sortedPCPInfo = sortPCP(pcpInfo);

		long mdoScoreBegin1 = System.nanoTime();
		List<PCP> pcpWithDrivingDistancePoints = getDrivingDistancePoints(sortedPCPInfo, rulesInfo, member);
		long mdoScoreEnd1 = System.nanoTime();
		long timeTaken1 = (mdoScoreEnd1 - mdoScoreBegin1) / 1000000;
		logger.info("Time Taken for hitting bing service and assigning driving distance scores is {}{} ", timeTaken1,
				"ms");
		return getBestPCP(pcpWithDrivingDistancePoints);

	}

	public PCP getBestPCP(List<PCP> pcpWithDrivingDistancePoints) {

		logger.info("Selecting the best PCP{}.", " ");
		TieBreakerUtil tieBreaker = new TieBreakerUtil();
		PCP bestPCP;

		Collections.sort(pcpWithDrivingDistancePoints, PCP.totalscore);
		int highestScore = pcpWithDrivingDistancePoints.get(0).getMdoScore();
		List<PCP> highestScorePCPList = new ArrayList<>();
		List<PCP> nearestPCPList;
		List<PCP> vbpPCPList;

		for (PCP pcp : pcpWithDrivingDistancePoints) {
			if (pcp.getMdoScore() == highestScore) {
				highestScorePCPList.add(pcp);
			}
		}
		if (highestScorePCPList.size() > 1) {
			nearestPCPList = tieBreaker.getNearestPCP(highestScorePCPList);

			if (nearestPCPList.size() > 1) {
				vbpPCPList = tieBreaker.getVbpPcp(nearestPCPList);
				bestPCP = vbpLogicValidation(vbpPCPList, nearestPCPList);

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

	public PCP vbpLogicValidation(List<PCP> vbpPCPList, List<PCP> nearestPCPList) {
		PCP bestPCP;
		List<PCP> leastAssignedPCPList;
		List<PCP> alphabeticallySortedPCPList;
		TieBreakerUtil tieBreaker = new TieBreakerUtil();

		if (vbpPCPList.size() > 1) {
			leastAssignedPCPList = tieBreaker.getleastAssignedPCP(vbpPCPList);

			if (leastAssignedPCPList.size() > 1) {
				alphabeticallySortedPCPList = tieBreaker.getAlphabeticallySortedPCPList(leastAssignedPCPList);
				bestPCP = alphabeticallySortedPCPList.get(0);
			}

			else {
				bestPCP = leastAssignedPCPList.get(0);
			}
		}

		else if (vbpPCPList.isEmpty()) {
			leastAssignedPCPList = tieBreaker.getleastAssignedPCP(nearestPCPList);

			if (leastAssignedPCPList.size() > 1) {
				alphabeticallySortedPCPList = tieBreaker.getAlphabeticallySortedPCPList(leastAssignedPCPList);
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

	public List<PCP> getDrivingDistancePoints(List<PCP> pcpInfo, Rules rulesInfo, Member member)
			throws JsonProcessingException {

		logger.info("Diving Distance Points Calculation started.{}", " ");
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
		bingResponse = new BingOutputPayload();
		try {
			long mdoScoreBegin1 = System.nanoTime();
			bingResponse = bingRestClientService.getDistanceMatrix(bingInput);
			long mdoScoreEnd1 = System.nanoTime();
			long timeTaken1 = (mdoScoreEnd1 - mdoScoreBegin1) / 1000000;
			logger.info("Time Taken for hitting bing service is {}{} ", timeTaken1, "ms");
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
		float currentMember;
		float maxCnt;
		float panelCapacity;
		List<PCP> validatedProviderList = pcpInfo.stream().filter(pcp -> {
			boolean flag = false;
			if (null != pcp.getAerialDistance()) {
				flag = true;
			}
			return flag;
		}).collect(Collectors.toList());
		Collections.sort(validatedProviderList, PCP.aerialDistanceComprtr);
		logger.info("Setting Driving distance equals{}to Aerial distance", " ");
		for (PCP pcp : validatedProviderList) {
			pcp.setDrivingDistance((double) pcp.getAerialDistance());
			currentMember = pcp.getCurntMbrCnt();
			maxCnt = pcp.getMaxMbrCnt();
			panelCapacity = (currentMember * 100 / maxCnt);
			pcp.setPanelCapacity(panelCapacity);
		}

		return validatedProviderList;
	}

	public List<PCP> getAllPCPScore(List<PCP> pcpList, Rules rulesInfo, Member member) throws ParseException {

		MDOScoringCalculation mdoScoreCal = new MDOScoringCalculation();

		logger.info("Calculating total scores for all PCP's {}", " ");
		List<PCP> updatedInfo = new ArrayList<>();
		for (PCP pcpInfo : pcpList) {
			int totScore = 0;
			if (null != member.getMemberLanguageCode()) {
				if ((!member.getMemberLanguageCode().equals(ENG_LANG_CONSTANT)) && null != pcpInfo.getPcpLang()) {
					totScore = totScore + mdoScoreCal.langTotalScore(pcpInfo, member, rulesInfo);
				} else
					logger.info("Language scores is not calculated {} ", " ");
			}
			totScore = totScore + mdoScoreCal.bonusTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.specialtyTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.vbpTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.aerialTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + mdoScoreCal.rankTotalScore(pcpInfo, rulesInfo);
			pcpInfo.setMdoScore(totScore);

			updatedInfo.add(pcpInfo);

		}

		return updatedInfo;
	}

	public List<PCP> sortPCP(List<PCP> pcpInfo) {

		logger.info("Sorting Top 50 Providers{}", " ");
		ArrayList<PCP> pcpSortedInfo = new ArrayList<>();
		Collections.sort(pcpInfo, PCP.totalscore);
		int value = 50;
		if (value >= pcpInfo.size()) {
			value = pcpInfo.size();
		}
		pcpSortedInfo.addAll(pcpInfo.subList(0, value));
		return pcpSortedInfo;

	}

}