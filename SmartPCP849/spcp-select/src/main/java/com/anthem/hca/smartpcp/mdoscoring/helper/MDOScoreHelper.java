/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - MDOScoreHelper used for calculating scores, calling bing service
 *  			calling tie breaker logic. At the end it will give Provider with highest score
 * 
 * @author AF70896
 * 
 * 
 *
 */
package com.anthem.hca.smartpcp.mdoscoring.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.mdoscoring.service.ScoringClientService;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.model.PCPTrackAudit;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.util.ThreadExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RefreshScope
public class MDOScoreHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(MDOScoreHelper.class);
	
	@Autowired
	private TieBreakerHelper tieBreakerHelper;
	
	@Autowired
	private MDOScoringCalculation scoringCalculator;
	
	@Autowired
	private ScoringClientService scoringClientService;
	
	@Autowired
	private Tracer tracer;
	
	@Value("${spcp.pcp.track.audit.enabled}")
	private boolean pcpTrackAudit;
	
	@Autowired
	private ThreadExecutor threadExecutor;
	
	private int numberOfThreads = 4;

	protected static final List<String> ENG_LANG_CONSTANT = Arrays.asList("ENG");

	public ScoringProvider getBestPCP(List<ScoringProvider> pcpList, MDOScoringRules rulesInfo, Member member,
			PCPAssignmentFlow pcpAssignmentFlow) throws Exception {
		ScoringProvider finalPCP = null;
		if (!pcpList.isEmpty()) {
			
			int listSize = pcpList.size();
			
		    int minItemsPerThread = listSize / numberOfThreads;
		    int maxItemsPerThread = minItemsPerThread + 1;
		    int threadsWithMaxItems = listSize - numberOfThreads * minItemsPerThread;
		    int start = 0;
		    List<Future<?>> futures = new ArrayList<>(numberOfThreads);
		    for (int i = 0; i < numberOfThreads; i++) {
		    	int itemsCount = (i < threadsWithMaxItems) ? maxItemsPerThread : minItemsPerThread;
		        int end = start + itemsCount;
		        List<ScoringProvider> pcp = pcpList.subList(start, end);
		        futures.add(threadExecutor.getExecutorService().submit(new Runnable() {
					@Override
					public void run() {
						getAllPCPScore(pcp, rulesInfo, member);
					}
				}));
		        start = end;
		    }
		    
		    for(Future<?> f : futures){
					f.get();
		    }
			
		    Collections.sort(pcpList, ScoringProvider.totalscore);
			pcpAssignmentFlow.setScoredSortedPoolPcps(pcpList);
			int value = 50;
			if (value >= pcpList.size()) {
				value = pcpList.size();
			}
			List<ScoringProvider> pcpSortedInfo = new ArrayList<>();
			pcpSortedInfo.addAll(pcpList.subList(0, value));
			List<ScoringProvider> pcpInfos = pcpSortedInfo.stream().map(this::getClonedPCP).collect(Collectors.toList());
			pcpAssignmentFlow.setDrivingDistScoredPcps(pcpInfos);
			finalPCP =  tieBreaker(pcpSortedInfo, pcpAssignmentFlow);
			pcpAssignmentFlow.setSelectedPcp(finalPCP);
		}
		
		if(pcpTrackAudit){
			pcpAssignmentFlow.setTraceId(tracer.getCurrentSpan().traceIdString());
			pcpAssignmentFlow.setCreatedTime(new Timestamp(System.currentTimeMillis()));
			persistPCPAssignmentFlow(pcpAssignmentFlow);
		}
		
		return finalPCP;

	}

	public ScoringProvider tieBreaker(List<ScoringProvider> pcpWithDrivingDistancePoints, PCPAssignmentFlow pcpAssignmentFlow) {
		
		ScoringProvider bestPCP;

		int highestScore = pcpWithDrivingDistancePoints.get(0).getPcpScore();
		List<ScoringProvider> highestScorePCPList = new ArrayList<>();
		List<ScoringProvider> nearestPCPList;
		List<ScoringProvider> vbpPCPList;
		for (ScoringProvider pcp : pcpWithDrivingDistancePoints) {
			if (highestScore == pcp.getPcpScore()) {
				highestScorePCPList.add(pcp);
			}
		}
		if (1 < highestScorePCPList.size()) {
			nearestPCPList = tieBreakerHelper.getNearestPCP(highestScorePCPList, pcpAssignmentFlow);

			if (1 < nearestPCPList.size()) { 
				vbpPCPList = tieBreakerHelper.getVbpPcp(nearestPCPList, pcpAssignmentFlow);
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

	public ScoringProvider vbpLogicValidation(List<ScoringProvider> vbpPCPList, List<ScoringProvider> nearestPCPList,
			PCPAssignmentFlow pcpAssignmentFlow) {
		ScoringProvider bestPCP;
		List<ScoringProvider> leastAssignedPCPList;
		List<ScoringProvider> alphabeticallySortedPCPList;

		if (1 < vbpPCPList.size()) {
			leastAssignedPCPList = tieBreakerHelper.getleastAssignedPCP(vbpPCPList, pcpAssignmentFlow);

			if (1 < leastAssignedPCPList.size()) {
				alphabeticallySortedPCPList = tieBreakerHelper.getAlphabeticallySortedPCPList(leastAssignedPCPList,
						pcpAssignmentFlow);
				bestPCP = alphabeticallySortedPCPList.get(0);
			}

			else {
				bestPCP = leastAssignedPCPList.get(0);
			}
		}

		else if (vbpPCPList.isEmpty()) {
			leastAssignedPCPList = tieBreakerHelper.getleastAssignedPCP(nearestPCPList, pcpAssignmentFlow);

			if (1 < leastAssignedPCPList.size()) {
				alphabeticallySortedPCPList = tieBreakerHelper.getAlphabeticallySortedPCPList(leastAssignedPCPList,
						pcpAssignmentFlow);
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

	public void getAllPCPScore(List<ScoringProvider> pcpList, MDOScoringRules rulesInfo, Member member){

		for (ScoringProvider pcpInfo : pcpList) {
			int totScore = 0;
			if (null != member.getMemberLanguageCode() && (!ENG_LANG_CONSTANT.equals(member.getMemberLanguageCode()))
					&& null != pcpInfo.getPcpLang()) {
				totScore = totScore + scoringCalculator.langTotalScore(pcpInfo, member, rulesInfo);
			}
			totScore = totScore + scoringCalculator.bonusTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + scoringCalculator.specialtyTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + scoringCalculator.vbpTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + scoringCalculator.aerialTotalScore(pcpInfo, rulesInfo);
			totScore = totScore + scoringCalculator.rankTotalScore(pcpInfo, rulesInfo);
			pcpInfo.setPcpScore(totScore);
		}
	}
	
	private void persistPCPAssignmentFlow(PCPAssignmentFlow pcpAssignmentFlow) {
		try {
			PCPTrackAudit pcpAuditInfo = new PCPTrackAudit();
			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = mapperObj.writeValueAsString(pcpAssignmentFlow);
			pcpAuditInfo.setTraceId(tracer.getCurrentSpan().traceIdString());
			pcpAuditInfo.setProviderData(jsonStr);
			scoringClientService.persistPCPAssignmentFlow(pcpAuditInfo);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error occured while saving PCP details", e);
		}
		
	}

	private ScoringProvider getClonedPCP(ScoringProvider oldPcp) {
		ScoringProvider newPcp = new ScoringProvider();
		BeanUtils.copyProperties(oldPcp, newPcp);
		return newPcp;
	}
}