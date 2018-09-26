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
package com.anthem.hca.smartpcp.mdoscoring.helper;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Configuration;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.ScoringProvider;

@Configuration
public class MDOScoringCalculation {

	public int specialtyTotalScore(ScoringProvider pcpInfo, MDOScoringRules rulesInfo) {
		int score = 0;
		if (null != pcpInfo.getSpeciality() && null != rulesInfo.getRestrictedAgeSpecialties()) {
			for (String specialty : pcpInfo.getSpeciality()) {
				if (Arrays.asList(rulesInfo.getRestrictedAgeSpecialties()).contains(specialty)) {
					score = rulesInfo.getAgeSpecialtyMatchScore(specialty);
					break;
				}
			}
		}
		pcpInfo.setSpecialityScore(score);
		return score;
	}

	public int langTotalScore(ScoringProvider pcpInfo, Member member, MDOScoringRules rulesInfo) {
		int score = 0;
		int cnt = 0;
		for (String mem : member.getMemberLanguageCode()) {
			if (!Constants.ENG_CON.equalsIgnoreCase(mem) && pcpInfo.getPcpLang().contains(mem)) {
				cnt++;
			}
		}
		if (cnt > 0) {
			score = rulesInfo.getLanguageMatchScore(true);
		}
		pcpInfo.setLanguageScore(score);
		return score;
	}

	public int vbpTotalScore(ScoringProvider pcpInfo, MDOScoringRules rulesInfo) {
		int score = 0;
		if (StringUtils.isNotBlank(pcpInfo.getVbpFlag()) && Constants.VBP_FLAG.equalsIgnoreCase(pcpInfo.getVbpFlag())) {
			score = rulesInfo.getVBAParticipationScore(true);
		}
		pcpInfo.setVbpScore(score);
		return score;
	}

	public int aerialTotalScore(ScoringProvider pcpInfo, MDOScoringRules rulesInfo) {
		int score = rulesInfo.getProximityScore(pcpInfo.getDistance());
		pcpInfo.setDistanceScore(score);
		return score;
	}

	public int rankTotalScore(ScoringProvider pcpInfo, MDOScoringRules rulesInfo) {
		int score = rulesInfo.getMDORankScore(pcpInfo.getRank());
		pcpInfo.setRankScore(score);
		return score;
	}

	public int bonusTotalScore(ScoringProvider pcpInfo, MDOScoringRules rulesInfo) {
		int score = 0;
		long daysBetween = rulesInfo.getLimitedTime() + 1L;

		if (null != pcpInfo.getGrpgRltdPadrsEfctvDt()) {

			daysBetween = Days.daysBetween(new LocalDate(new Date().getTime()),
					new LocalDate(pcpInfo.getGrpgRltdPadrsEfctvDt().getTime())).getDays();
		}

		if (daysBetween <= rulesInfo.getLimitedTime()
				|| pcpInfo.getPanelCapacity() <= rulesInfo.getPanelCapacityPercent()) {
			score = rulesInfo.getLimitedTimeScore();
		}
		pcpInfo.setLimitedTimeBonusScore(score);
		return score;
	}
}