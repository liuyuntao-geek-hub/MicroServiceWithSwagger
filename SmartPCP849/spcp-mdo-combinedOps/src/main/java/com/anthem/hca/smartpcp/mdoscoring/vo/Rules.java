package com.anthem.hca.smartpcp.mdoscoring.vo;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "rules", description = "Contains rules request information received by spcp-mdo-scoring microservice")
public class Rules {
	@ApiModelProperty(dataType = "String", notes = "Contains market from rules")
	private String market;
	@ApiModelProperty(dataType = "String", notes = "Contains lob from rules")
	private String lob;
	@ApiModelProperty(dataType = "String", notes = "Contains product from rules")
	private String product;
	@ApiModelProperty(dataType = "String", notes = "Contains assignment type from rules")
	private String assignmentType;
	@ApiModelProperty(dataType = "String", notes = "Contains assignment method from rules")
	private String assignmentMethod;
	@ApiModelProperty(dataType = "String", notes = "Contains fall back required of rules")
	private boolean fallbackRequired = true;
	@NotNull(message = "mdorankScoreList should be present")
	@Size(min = 1, message = "mdorankScoreList should have atleast one element")
	@ApiModelProperty(required = true, dataType = "List<ActionPair<Integer, Integer>>", notes = "Contains mdo rank scores from rules")
	private List<@NotNull(message = "mdorankScoreList should be present") @Valid ActionPair<Integer, Integer>> mdorankScoreList;

	@NotNull(message = "proximityScoreList should be present")
	@Size(min = 1, message = "proximityScoreList should have atleast one element")
	@ApiModelProperty(required = true, dataType = "List<ActionPair<String, Integer>>", notes = "Contains proximity scores from rules")
	private List<@NotNull(message = "proximityScoreList should be present") @Valid ActionPair<String, Integer>> proximityScoreList;

	@NotNull(message = "languageMatchScoreList should be present")
	@Size(min = 1, message = "languageMatchScoreList should have atleast one element")
	@ApiModelProperty(required = true, dataType = "List<ActionPair<Boolean, Integer>>", notes = "Contains language match score from rules")
	private List<@NotNull(message = "languageMatchScoreList should be present") @Valid ActionPair<Boolean, Integer>> languageMatchScoreList;

	@NotNull(message = "ageSpecialtyMatchScoreList should be present")
	@Size(min = 1, message = "ageSpecialtyMatchScoreList should have atleast one element")
	@ApiModelProperty(required = true, dataType = "List<ActionPair<Boolean, Integer>>", notes = "Contains age speciality from rules")
	private List<@NotNull(message = "ageSpecialtyMatchScoreList should be present") @Valid  ActionPair<Boolean, Integer>> ageSpecialtyMatchScoreList;

	@NotNull(message = "vbaparticipationScoreList should be present")
	@Size(min = 1, message = "vbaparticipationScoreList should have atleast one element")
	@ApiModelProperty(required = true, dataType = "List<ActionPair<Boolean, Integer>>", notes = "Contains vbp participation scores from rules")
	private List<@NotNull(message = "vbaparticipationScoreList should be present") @Valid ActionPair<Boolean, Integer>> vbaparticipationScoreList;

	@NotNull(message = "limitedTime should be present")
	@ApiModelProperty(required = true, dataType = "int", notes = "Contains limited time from rules")
	private Integer limitedTime;

	@NotNull(message = "panelCapacityPercent should be present")
	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the panel capacity limit from rules")
	private Integer panelCapacityPercent;

	@NotNull(message = "limitedTimeScore should be present")
	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the limited time score from rules")
	private Integer limitedTimeScore;

	@NotNull(message = "restrictedAgeSpecialties should be present")
	@Size(min = 1, message = "restrictedAgeSpecialties should have atleast one element")
	@ApiModelProperty(required = true, dataType = "List<String>", notes = "Contains the specialities from rules")
	private List<@NotBlank(message = "restrictedAgeSpecialties elements cannot be null or empty") @Pattern(regexp = "[a-z-A-Z]*", message = "restrictedAgeSpecialties elements have invalid characters") String> restrictedAgeSpecialties;

	public List<String> getRestrictedAgeSpecialties() {
		return restrictedAgeSpecialties;
	}

	public void setRestrictedAgeSpecialties(List<String> restrictedAgeSpecialties) {
		this.restrictedAgeSpecialties = restrictedAgeSpecialties;
	}

	public Integer getLimitedTime() {
		return limitedTime;
	}

	public void setLimitedTime(Integer limitedTime) {
		this.limitedTime = limitedTime;
	}

	public Integer getPanelCapacityPercent() {
		return panelCapacityPercent;
	}

	public void setPanelCapacityPercent(Integer panelCapacityPercent) {
		this.panelCapacityPercent = panelCapacityPercent;
	}

	public Integer getLimitedTimeScore() {
		return limitedTimeScore;
	}

	public void setLimitedTimeScore(Integer limitedTimeScore) {
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

	public Integer getMdorankScoreList(Integer rank) {
		Optional<ActionPair<Integer, Integer>> val = mdorankScoreList.stream()
				.filter(ap -> ap.getKey().intValue() == rank).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public Integer getProximityScoreList(float distance) {
		Integer score = 0;
		Integer distMin = 0;
		Integer distMax = 0;

		for (ActionPair<String, Integer> entry : proximityScoreList) {
			if (!entry.getKey().isEmpty()) {
				String[] distArr = entry.getKey().split("[\\s]*-[\\s]*");

				try {
					distMin = Integer.parseInt(distArr[0].trim());

					distMax = (distArr.length > 1) ? Integer.parseInt(distArr[1].trim()) : Short.MAX_VALUE;
				} catch (NumberFormatException nfe) {
					distMin = distMax = (int) Short.MIN_VALUE;
				}

				if ((distance == 0f && distance >= (float) distMin && distance <= (float) distMax)
						|| (distance > (float) distMin && distance <= (float) distMax)) {
					score = entry.getValue();
					break;
				}
			}
		}

		return score;
	}

	public Integer getLanguageMatchScoreList(boolean match) {
		Optional<ActionPair<Boolean, Integer>> val = languageMatchScoreList.stream()
				.filter(ap -> ap.getKey().booleanValue() == match).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public Integer getAgeSpecialtyMatchScoreList(String specialty) {
		boolean contains = restrictedAgeSpecialties.stream().anyMatch(s -> s.equalsIgnoreCase(specialty.trim()));
		Optional<ActionPair<Boolean, Integer>> val = ageSpecialtyMatchScoreList.stream()
				.filter(ap -> ap.getKey().booleanValue() == contains).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public Integer getVbaparticipationScoreList(boolean participate) {
		Optional<ActionPair<Boolean, Integer>> val = vbaparticipationScoreList.stream()
				.filter(ap -> ap.getKey().booleanValue() == participate).findAny();
		return (val.isPresent()) ? val.get().getValue() : 0;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	public void setAssignmentMethod(String assignmentMethod) {
		this.assignmentMethod = assignmentMethod;
	}

	public boolean isFallbackRequired() {
		return fallbackRequired;
	}

	public void setFallbackRequired(boolean fallbackRequired) {
		this.fallbackRequired = fallbackRequired;
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
