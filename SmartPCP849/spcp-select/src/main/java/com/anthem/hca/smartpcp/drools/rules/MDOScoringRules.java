package com.anthem.hca.smartpcp.drools.rules;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.ActionPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

/**
 * The MDOScoringRules class is used to encapsulate all the properties and behaviors of
 * a MDO Scoring Rule. It extends the base AbstractRules class and also provides a custom
 * implementation of the isFallbackRequired() method.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.4
 */

public class MDOScoringRules extends AbstractRules {

	private int mdoRank;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a list of PCP Ranks and their corresponding MDO Scores")
	private List<ActionPair<Integer, Integer>> mdoRankScore = new ArrayList<>();

	private String proximity;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a list of Proximities and their corresponding MDO Scores")
	private List<ActionPair<String, Integer>> proximityScore = new ArrayList<>();

	private boolean languageMatch;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains Language Match (Y/N) and their corresponding MDO Scores")
	private List<ActionPair<Boolean, Integer>> languageMatchScore = new ArrayList<>();

	private boolean ageSpecialtyMatch;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains Age Specialty Match (Y/N) and their corresponding MDO Scores")
	private List<ActionPair<Boolean, Integer>> ageSpecialtyMatchScore = new ArrayList<>();

	@ApiModelProperty(required = true, dataType = "String[]", notes = "Contains a list of Restricted Specialties for which there is a Age Restriction")
	private String[] restrictedSpecialties;

	private boolean vbaParticipation;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains VBA Participation flag (Y/N) and their corresponding MDO Scores")
	private List<ActionPair<Boolean, Integer>> vbaParticipationScore = new ArrayList<>();

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the number of Days to be considered for a PCP to allow Limited Bonus")
	private int limitedTime;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Panel Capacity of a PCP to be considered to allow Limited Bonus")
	private int panelCapacity;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the MDO Score for a PCP to be considered for Limited Bonus")
	private int limitedTimeScore;

	public MDOScoringRules() {
		setAgendaGroup(AgendaGroup.MDO_SCORING);
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a list of PCP Ranks and their corresponding MDO Scores")
	public List<ActionPair<Integer, Integer>> getMDORankScoreList() {
		return mdoRankScore;
	}

	public int getMDORankScore(int rank) {
		Optional<ActionPair<Integer, Integer>> val = mdoRankScore.stream().filter(ap -> ap.getKey().intValue() == rank).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public void setMDORank(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				mdoRank = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("MDO Rank cannot be empty in MDO-Provider-Ranking-Scoring-Rules.xls");
		}
	}

	public void setMDORankScore(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				mdoRankScore.add(new ActionPair<Integer, Integer>(mdoRank, Integer.parseInt(param.trim())));
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("MDO Rank Point cannot be empty in MDO-Provider-Ranking-Scoring-Rules.xls");
		}
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a list of Proximities and their corresponding MDO Scores")
	public List<ActionPair<String, Integer>> getProximityScoreList() {
		return proximityScore;
	}

	public int getProximityScore(double distance) {
        int score = 0;

        for (ActionPair<String, Integer> entry: proximityScore) {
        	String[] distArr = entry.getKey().split("[\\s]*-[\\s]*");

        	int distMin = 0;
        	int distMax = Short.MAX_VALUE;

        	try {
        		distMin = distArr[0].matches("[\\d]+[\\s]*\\+")
        				? Integer.parseInt(distArr[0].split("\\+")[0].trim())
        				: Integer.parseInt(distArr[0].trim());

        		distMax = (distArr.length > 1) ? Integer.parseInt(distArr[1].trim()): Short.MAX_VALUE;
        	} catch (NumberFormatException nfe) {
        		distMin = distMax = Short.MIN_VALUE;
        	}
  
        	if ((distance == 0f && distance >= (float)distMin && distance <= (float)distMax) || (distance > (float)distMin && distance <= (float)distMax)) {
        		score = entry.getValue();
        		break;
        	}
        }

        return score;
	}

	public void setProximity(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.PROXIMITY)) {
				proximity = param.trim();
			}
			else {
				throw new DroolsParseException("Proximity should be in the format [A-B] or [C+] (where A, B, C are Integers) in MDO-Proximity-Scoring-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Proximity cannot be empty in MDO-Proximity-Scoring-Rules.xls");
		}
	}

	public void setProximityScore(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				proximityScore.add(new ActionPair<String, Integer>(proximity, Integer.parseInt(param.trim())));
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Proximity Point cannot be empty in MDO-Proximity-Scoring-Rules.xls");
		}
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains Language Match (Y/N) and their corresponding MDO Scores")
	public List<ActionPair<Boolean, Integer>> getLanguageMatchScoreList() {
		return languageMatchScore;
	}

	public int getLanguageMatchScore(boolean match) {
		Optional<ActionPair<Boolean, Integer>> val = languageMatchScore.stream().filter(ap -> ap.getKey().booleanValue() == match).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public void setLanguageMatch(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.YES_NO)) {
				languageMatch = BooleanUtils.toBoolean(param.trim());
			}
			else {
				throw new DroolsParseException("Language Match must be one of these - 'Y/N/YES/NO' in MDO-Language-Match-Scoring-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Language Match cannot be empty in MDO-Language-Match-Scoring-Rules.xls");
		}
	}

	public void setLanguageMatchScore(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				languageMatchScore.add(new ActionPair<Boolean, Integer>(languageMatch, Integer.parseInt(param.trim())));
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Language Point cannot be empty in MDO-Language-Match-Scoring-Rules.xls");
		}
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains Age Specialty Match (Y/N) and their corresponding MDO Scores")
	public List<ActionPair<Boolean, Integer>> getAgeSpecialtyMatchScoreList() {
		return ageSpecialtyMatchScore;
	}

	public int getAgeSpecialtyMatchScore(String specialty) {
		boolean contains = Arrays.stream(restrictedSpecialties).anyMatch(s -> s.equalsIgnoreCase(specialty.trim()));
		Optional<ActionPair<Boolean, Integer>> val = ageSpecialtyMatchScore.stream().filter(ap -> ap.getKey().booleanValue() == contains).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	@ApiModelProperty(required = true, dataType = "String[]", notes = "Contains a list of Restricted Specialties for which there is a Age Restriction")
	public String[] getRestrictedAgeSpecialties() {
		return restrictedSpecialties;
	}

	public void setRestrictedAgeSpecialties(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			restrictedSpecialties = param.split(RulesRegex.PRIMARY_SPECIALTIES);
			restrictedSpecialties = Arrays.stream(restrictedSpecialties).map(String::trim).toArray(String[]::new);
		}
		else {
			throw new DroolsParseException("Restricted Specialties cannot be empty in MDO-Age-Specialty-Match-Scoring-Rules.xls");
		}
	}

	public void setAgeSpecialtyMatch(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.YES_NO)) {
				ageSpecialtyMatch = BooleanUtils.toBoolean(param.trim());
			}
			else {
				throw new DroolsParseException("Age-Specialty Match must be one of these - 'Y/N/YES/NO' in MDO-Age-Specialty-Match-Scoring-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Age-Specialty Match cannot be empty in MDO-Age-Specialty-Match-Scoring-Rules.xls");
		}
	}

	public void setAgeSpecialtyMatchScore(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				ageSpecialtyMatchScore.add(new ActionPair<Boolean, Integer>(ageSpecialtyMatch, Integer.parseInt(param.trim())));
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Age-Specialty Point cannot be empty in MDO-Age-Match-Scoring-Rules.xls");
		} 
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains VBA Participation flag (Y/N) and their corresponding MDO Scores")
	public List<ActionPair<Boolean, Integer>> getVBAParticipationScoreList() {
		return vbaParticipationScore;
	}

	public int getVBAParticipationScore(boolean participate) {
		Optional<ActionPair<Boolean, Integer>> val = vbaParticipationScore.stream().filter(ap -> ap.getKey().booleanValue() == participate).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public void setVBAParticipation(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.YES_NO)) {
				vbaParticipation = BooleanUtils.toBoolean(param.trim());
			}
			else {
				throw new DroolsParseException("VBA Participation must be one of these - 'Y/N/YES/NO' in MDO-Value-Based-Agreement-Scoring-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("VBA Participation cannot be empty in MDO-Value-Based-Agreement-Scoring-Rules.xls");
		}
	}

	public void setVBAParticipationScore(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				vbaParticipationScore.add(new ActionPair<Boolean, Integer>(vbaParticipation, Integer.parseInt(param.trim())));
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("VBA Point cannot be empty in MDO-Value-Based-Agreement-Scoring-Rules.xls");
		}
	}

	public int getLimitedTime() {
		return limitedTime;
	}

	public void setLimitedTime(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				limitedTime = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Limited Time cannot be empty in MDO-Limited-Time-Bonus-Scoring-Rules.xls");
		}
	}

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Panel Capacity of a PCP to be considered to allow Limited Bonus")
	public int getPanelCapacityPercent() {
		return panelCapacity;
	}

	public void setPanelCapacityPercent(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				panelCapacity = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Panel Capacity percent cannot be empty in Provider-Panel-Capacity-Rules.xls");
		}
	}

	public int getLimitedTimeScore() {
		return limitedTimeScore;
	}

	public void setLimitedTimeScore(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				limitedTimeScore = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Limited Time Score cannot be empty in MDO-Limited-Time-Bonus-Scoring-Rules.xls");
		}
	}

	@Override
	@JsonIgnore
	public boolean isFallbackRequired() {
		return
			mdoRankScore.isEmpty()
			|| proximity == null
			|| proximityScore.isEmpty()
			|| languageMatchScore.isEmpty()
			|| restrictedSpecialties == null
			|| ageSpecialtyMatchScore.isEmpty()
			|| vbaParticipationScore.isEmpty()
		;

		/*
		 * Default values of the following attributes should not be checked
		 * to determine if Rules have been successfully fired or not. This
		 * is because the Default values of these attributes can be same as
		 * valid data provided by the Business.
		 * 
		 * mdoRank == 0
		 * languageMatch == false
		 * ageSpecialtyMatch == false
		 * vbaParticipation == false
		 * limitedTime == 0
		 * panelCapacity == 0
		 * limitedTimeScore == 0
		 */
	}

}
