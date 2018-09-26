package com.anthem.hca.smartpcp.mdoscoring.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.Resource;
import com.anthem.hca.smartpcp.mdoscoring.vo.ResourceSet;
import com.anthem.hca.smartpcp.mdoscoring.vo.Result;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;

public class MDOScoringCalculation {

	private static final Logger logger = LoggerFactory.getLogger(MDOScoringCalculation.class);
	OutputPayloadInfo outputPayload = new OutputPayloadInfo();

	public int specialtyTotalScore(PCP pcpInfo, Rules rulesInfo) {
		int score = 0;
			score = rulesInfo.getAgeSpecialtyMatchScoreList(pcpInfo.getSpcltyDesc());
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
			score = cnt * (rulesInfo.getLanguageMatchScoreList(true));
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
		if(null!= pcpInfo.getAerialDistance()){
		score = rulesInfo.getProximityScoreList(pcpInfo.getAerialDistance().floatValue());
		}
		return score;
	}

	public int rankTotalScore(PCP pcpInfo, Rules rulesInfo) {
		int score = 0;
		if (!StringUtils.isBlank(pcpInfo.getPcpRankgId()) && pcpInfo.getPcpRankgId().matches("[0-9]") ) {
			int rankId = Integer.parseInt(pcpInfo.getPcpRankgId());
			score = rulesInfo.getMdorankScoreList(rankId);
		}
		return score;
	}

	public int bonusTotalScore(PCP pcpInfo, Rules rulesInfo) throws ParseException {
		int score = 0;

		if (null != pcpInfo.getGrpgRltdPadrsEfctvDt()  && 0 != pcpInfo.getMaxMbrCnt()) {

			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String currentDate = myFormat.format(date);
			Date pcpEffective = pcpInfo.getGrpgRltdPadrsEfctvDt();
			String pcpeffDate = myFormat.format(pcpEffective);
			int currentMember = pcpInfo.getCurntMbrCnt();
			int maxCnt = pcpInfo.getMaxMbrCnt();
			float panelCapcity = (currentMember * 100 / maxCnt);
			Date dateAfter = myFormat.parse(currentDate);
			Date dateBefore = myFormat.parse(pcpeffDate);
			long daysBetween = (dateAfter.getTime() - dateBefore.getTime()) / (1000 * 60 * 60 * 24);
			if (daysBetween <= rulesInfo.getLimitedTime() && panelCapcity <= rulesInfo.getPanelCapacityPercent()) {
				score = rulesInfo.getLimitedTimeScore();
			}
		}
		return score;
	}

	public List<PCP> drivingDistanceTotalScore(List<ResourceSet> distMatrixResourceSet, List<PCP> pcpInfo,
			Rules rules) {

		List<PCP> filteredPCPs = new ArrayList<>();
		int j = 0;
		List<Resource> resources;
		List<Double> distanceList;
		float drivingDistance;
		int totScore;
		float currentMember;
		float maxCnt;
		float panelCapacity;
		
		for (ResourceSet resourceSet : distMatrixResourceSet) {
			resources = resourceSet.getResources();
			distanceList = distanceListDtl(resources);

			if (pcpInfo.size() == distanceList.size()) {
				int i = 0;
				for (PCP pcp : pcpInfo) {
					drivingDistance = distanceList.get(i).floatValue();
					if (-1.00!= drivingDistance && 0!=pcp.getMaxMbrCnt()) {
						totScore = pcp.getMdoScore() + rules.getProximityScoreList(drivingDistance);
						pcp.setMdoScore(totScore);
						pcp.setDrivingDistance(distanceList.get(i));
						currentMember = pcp.getCurntMbrCnt();
						maxCnt = pcp.getMaxMbrCnt();
						panelCapacity = (currentMember * 100 / maxCnt);
						pcp.setPanelCapacity(panelCapacity);
						filteredPCPs.add(pcp);
						j++;
					} else {
						logger.info("Filtering out this PCP: PcpId:" + pcp.getProvPcpId() + " PCP Last Name: "
								+ pcp.getPcpLastNm() + " as driving distance = -1");
					}
					i++;

				}
			} else {
				logger.info("Number of PCPs and number of distances is not same{}"," ");
			}
		}
		logger.info("Total number of PCPs after filtering = {}" , j);
		return filteredPCPs;
	}

	public List<Double> distanceListDtl(List<Resource> resources) {
		List<Double> distanceList = new ArrayList<>();
		List<Result> results;
		for (Resource resource : resources) {
			results = resource.getResults();

			for (Result result : results) {
				distanceList.add(Math.round(result.getTravelDistance() * 100) / 100D);
			}
		}
		return distanceList;
	}

}