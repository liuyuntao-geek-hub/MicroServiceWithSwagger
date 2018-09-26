/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information
 * 
 *   Description - Contains functions used for calculating scores
 *   	based on age specialty, language match, Vbp flag, bonus points
 *   	 proximity scores, rank based.
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthem.hca.smartpcp.mdoscoring.vo.Member;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Result;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;

public class MDOScoringCalculation {
	
	private static final Logger logger = LoggerFactory.getLogger(MDOScoringCalculation.class);
	OutputPayloadInfo outputPayload = new OutputPayloadInfo();

	public int specialtyTotalScore(PCP pcpInfo, Rules rulesInfo) {
		int score = 0;
		if (null != pcpInfo.getSpcltyDesc() && null != rulesInfo.getRestrictedAgeSpecialties()) {
			score = rulesInfo.getAgeSpecialtyMatchScoreList(pcpInfo.getSpcltyDesc());
		}
		return score;
	}

	public int langTotalScore(PCP pcpInfo, Member member, Rules rulesInfo) {
		int score = 0;
		int cnt = 0;
		for (String mem : member.getMemberLanguageCode()) {
			if (!Constant.ENG_CON.equalsIgnoreCase(mem) && pcpInfo.getPcpLang().contains(mem)) {
				cnt++;
			}
		}
		if (cnt > 0) {
			score = rulesInfo.getLanguageMatchScoreList(true);
		}
		return score;
	}

	public int vbpTotalScore(PCP pcpInfo, Rules rulesInfo) {
		int score = 0;
		if (null != pcpInfo.getVbpFlag() && Constant.VBP_FLAG.equalsIgnoreCase(pcpInfo.getVbpFlag())) {
			score = rulesInfo.getVbaparticipationScoreList(true);
		}

		return score;
	}

	public int aerialTotalScore(PCP pcpInfo, Rules rulesInfo) {
		int score = 0;
		if (null != pcpInfo.getAerialDistance()) {
			score = rulesInfo.getProximityScoreList(pcpInfo.getAerialDistance().floatValue());
		}
		return score;
	}

	public int rankTotalScore(PCP pcpInfo, Rules rulesInfo) {
		int score = 0;
		if (!StringUtils.isBlank(pcpInfo.getPcpRankgId()) && pcpInfo.getPcpRankgId().matches("[0-9]")) {
			score = rulesInfo.getMdorankScoreList(Integer.parseInt(pcpInfo.getPcpRankgId()));
		}
		return score;
	}

	public int bonusTotalScore(PCP pcpInfo, Rules rulesInfo) throws ParseException {
		int score = 0;
		long daysBetween=rulesInfo.getLimitedTime()+1L;
		float panelCapcity=rulesInfo.getPanelCapacityPercent() + 1f;
		if (null != pcpInfo.getGrpgRltdPadrsEfctvDt()) {
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String currentDate = myFormat.format(date);
			Date pcpEffective = pcpInfo.getGrpgRltdPadrsEfctvDt();
			String pcpeffDate = myFormat.format(pcpEffective);
			Date dateAfter = myFormat.parse(currentDate);
			Date dateBefore = myFormat.parse(pcpeffDate);
			daysBetween = (dateAfter.getTime() - dateBefore.getTime()) / (1000 * 60 * 60 * 24);
		}
		if(null!=pcpInfo.getMaxMbrCnt()){
		float currentMember = pcpInfo.getCurntMbrCnt();
		int maxCnt = pcpInfo.getMaxMbrCnt();
		panelCapcity = (currentMember * 100 / maxCnt);
		pcpInfo.setPanelCapacity(panelCapcity);
		}
			if (daysBetween <= rulesInfo.getLimitedTime() || panelCapcity <= rulesInfo.getPanelCapacityPercent()) {
				score = rulesInfo.getLimitedTimeScore();
			}
		
		return score;
	}
	
	public List<PCP> drivingDistanceTotalScore(List<ResourceSet> distMatrixResourceSet, List<PCP> pcpInfo, Rules rules) {

		List<PCP> filteredPCPs = new ArrayList<>();
		List<Result> resources;
		double drivingDistance;
		int totScore;
		resources = distMatrixResourceSet.get(0).getResources().get(0).getResults();
		int drivingDistIndex = 0;
		for (PCP pcp : pcpInfo) {
			drivingDistance = Math.round(resources.get(drivingDistIndex).getTravelDistance() * 100) / 100D;
			if (-1.00 != drivingDistance && 0 != pcp.getMaxMbrCnt()) {
				int drivingScore = rules.getProximityScoreList((float) drivingDistance);
				totScore = pcp.getMdoScore() + drivingScore;
				pcp.setMdoScore(totScore);
				pcp.setDrivingDistance(drivingDistance);
				pcp.setDrivingDistanceScore(drivingScore);
				filteredPCPs.add(pcp);
			}
			drivingDistIndex++;
		}
		logger.info("Bing driving distance successful, PCP count={}",filteredPCPs.size());
		return filteredPCPs;
	}

}