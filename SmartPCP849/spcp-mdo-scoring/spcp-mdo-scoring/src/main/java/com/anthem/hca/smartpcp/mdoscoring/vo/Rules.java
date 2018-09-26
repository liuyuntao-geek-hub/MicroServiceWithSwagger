/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "rules", description = "Contains rules request information received by spcp-mdo-scoring microservice")
public class Rules {
	@ApiModelProperty(dataType = "List<ActionPair<Integer, Integer>>", notes = "Contains mdo rank scores from rules")
	private List<ActionPair<Integer, Integer>> mdorankScoreList;

	@ApiModelProperty(dataType = "List<ActionPair<String, Integer>>", notes = "Contains proximity scores from rules")
	private List<ActionPair<String, Integer>> proximityScoreList;

	@ApiModelProperty(dataType = "List<ActionPair<Boolean, Integer>>", notes = "Contains language match score from rules")
	private List<ActionPair<Boolean, Integer>> languageMatchScoreList;

	@ApiModelProperty(dataType = "List<ActionPair<Boolean, Integer>>", notes = "Contains age speciality from rules")
	private List<ActionPair<Boolean, Integer>> ageSpecialtyMatchScoreList;

	@ApiModelProperty(dataType = "List<ActionPair<Boolean, Integer>>", notes = "Contains vbp participation scores from rules")
	private List<ActionPair<Boolean, Integer>> vbaparticipationScoreList;

	@ApiModelProperty(dataType = "int", notes = "Contains limited time from rules")
	private int limitedTime;

	@ApiModelProperty(dataType = "int", notes = "Contains the panel capacity limit from rules")
	private int panelCapacityPercent;

	@ApiModelProperty(dataType = "int", notes = "Contains the limited time score from rules")
	private int limitedTimeScore;

	@ApiModelProperty(dataType = "List<String>", notes = "Contains the specialities from rules")
	private List<String> restrictedAgeSpecialties;

	public List<String> getRestrictedAgeSpecialties() {
		return restrictedAgeSpecialties;
	}

	public void setRestrictedAgeSpecialties(List<String> restrictedAgeSpecialties) {
		this.restrictedAgeSpecialties = restrictedAgeSpecialties;
	}

	public int getLimitedTime() {
		return limitedTime;
	}

	public void setLimitedTime(int limitedTime) {
		this.limitedTime = limitedTime;
	}

	public int getPanelCapacityPercent() {
		return panelCapacityPercent;
	}

	public void setPanelCapacityPercent(int panelCapacityPercent) {
		this.panelCapacityPercent = panelCapacityPercent;
	}

	public int getLimitedTimeScore() {
		return limitedTimeScore;
	}

	public void setLimitedTimeScore(int limitedTimeScore) {
		this.limitedTimeScore = limitedTimeScore;
	}

	public void setMdorankScoreList(List<ActionPair<Integer, Integer>> mdorankScoreList) {
		this.mdorankScoreList = mdorankScoreList;
	}

	public void setProximityScoreList(List<ActionPair<String, Integer>> proximityScoreList) {
		this.proximityScoreList = proximityScoreList;
	}

	public void setLanguageMatchScoreList(List<ActionPair<Boolean, Integer>> languageMatchScoreList) {
		this.languageMatchScoreList = languageMatchScoreList;
	}

	public void setAgeSpecialtyMatchScoreList(List<ActionPair<Boolean, Integer>> ageSpecialtyMatchScoreList) {
		this.ageSpecialtyMatchScoreList = ageSpecialtyMatchScoreList;
	}

	public void setVbaparticipationScoreList(List<ActionPair<Boolean, Integer>> vbaparticipationScoreList) {
		this.vbaparticipationScoreList = vbaparticipationScoreList;
	}

	public int getMdorankScoreList(Integer rank) {
		int score = 0;
		if (null != mdorankScoreList) {
			Optional<ActionPair<Integer, Integer>> val = mdorankScoreList.stream()
					.filter(ap -> ap.getKey().intValue() == rank).findAny();
			score = (val.isPresent()) ? val.get().getValue() : 0;
		}
		return score;
	}

	public int getProximityScoreList(float distance) {
		int score = 0;
		if (null != proximityScoreList) {
			score=scoreAgeSpeciality(distance,proximityScoreList);
		}
		return score;
	}

	public int scoreAgeSpeciality(float distance,List<ActionPair<String, Integer>> proximtyScoreList){
		int score=0;
		int distMin = 0;
		int distMax = 0;
		for (ActionPair<String, Integer> entry : proximtyScoreList) {
			if (!entry.getKey().isEmpty()) {
				
				String[] distArr = entry.getKey().split("[\\s]*-[\\s]*");
				try {
					distMin = Integer.parseInt(distArr[0].replaceAll("\\+", " ").trim());
					distMax = (distArr.length > 1) ? Integer.parseInt(distArr[1].trim()) : Short.MAX_VALUE;
				} catch (NumberFormatException nfe) {
					distMin = distMax = (int) Short.MIN_VALUE;
				}
				if ((distance >= (float) distMin && distance <= (float) distMax)
						|| (distance > (float) distMin && distance <= (float) distMax)) {
					score = entry.getValue();
					break;
				}
			}
		}
		return score;
	}
	public int getLanguageMatchScoreList(boolean match) {
		int score = 0;
		if (null != languageMatchScoreList) {
		Optional<ActionPair<Boolean, Integer>> val = languageMatchScoreList.stream()
				.filter(ap -> ap.getKey().booleanValue() == match).findAny();
		score =  (val.isPresent()) ? val.get().getValue() : 0;
		}
		return score;
	}

	public int getAgeSpecialtyMatchScoreList(String specialty) {
		int score = 0;
		if (null != ageSpecialtyMatchScoreList) {
			boolean contains = restrictedAgeSpecialties.stream().anyMatch(s -> s.equalsIgnoreCase(specialty.trim()));
			Optional<ActionPair<Boolean, Integer>> val = ageSpecialtyMatchScoreList.stream()
					.filter(ap -> ap.getKey().booleanValue() == contains).findAny();
			score = (val.isPresent()) ? val.get().getValue() : 0;
		}
		return score;
	}

	public int getVbaparticipationScoreList(boolean participate) {
		int score = 0;
		if (null != vbaparticipationScoreList) {
		Optional<ActionPair<Boolean, Integer>> val = vbaparticipationScoreList.stream()
				.filter(ap -> ap.getKey().booleanValue() == participate).findAny();
		score = (val.isPresent()) ? val.get().getValue() : 0;
		}
		return score;
	}

	/**
	 * Setting Default getters for List pairs
	 * 
	 */
	public List<ActionPair<Boolean, Integer>> getAgeSpecialtyMatchScoreList() {
		return ageSpecialtyMatchScoreList;
	}

	public List<ActionPair<Boolean, Integer>> getLanguageMatchScoreList() {
		return languageMatchScoreList;
	}

	public List<ActionPair<Boolean, Integer>> getVbaparticipationScoreList() {
		return vbaparticipationScoreList;
	}

	public List<ActionPair<Integer, Integer>> getMdorankScoreList() {
		return mdorankScoreList;
	}

	public List<ActionPair<String, Integer>> getProximityScoreList() {
		return proximityScoreList;
	}
}
