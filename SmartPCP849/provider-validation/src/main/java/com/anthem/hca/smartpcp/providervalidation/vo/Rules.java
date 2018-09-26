package com.anthem.hca.smartpcp.providervalidation.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.anthem.hca.smartpcp.providervalidation.constants.ErrorMessages;

public class Rules {


	public static final int MIN_AGE = 0;
	public static final int MAX_AGE = 999;

	@NotNull(message=ErrorMessages.MISSING_DROOLS_PRIMARY_SPLTY)
	@Size(min = 1, message = ErrorMessages.MISSING_DROOLS_PRIMARY_SPLTY)
	private String [] primarySpecialties;

	@NotNull(message=ErrorMessages.MISSING_DROOLS_SPLTY_MIN_AGE)
	@Size(min = 1, message = ErrorMessages.MISSING_DROOLS_SPLTY_MIN_AGE)
	private List<@NotNull(message = ErrorMessages.MISSING_DROOLS_SPLTY_MIN_AGE) @Valid ActionPair<String, Integer>> specialtyMinAgeList = new ArrayList<>();
	
	@Size(min = 1, message = ErrorMessages.MISSING_DROOLS_SPLTY_MAX_AGE)
	@NotNull(message=ErrorMessages.MISSING_DROOLS_SPLTY_MAX_AGE)
	private List<@NotNull(message = ErrorMessages.MISSING_DROOLS_SPLTY_MAX_AGE) @Valid ActionPair<String, Integer>> specialtyMaxAgeList = new ArrayList<>();
	
	@Size(min = 1, message = ErrorMessages.MISSING_DROOLS_SPLTY_GEN)
	@NotNull(message=ErrorMessages.MISSING_DROOLS_SPLTY_GEN)
	private List<@NotNull(message = ErrorMessages.MISSING_DROOLS_SPLTY_GEN) @Valid ActionPair<String, String>> specialtyGenderList = new ArrayList<>();
	
	@NotNull(message=ErrorMessages.MISSING_DROOLS_CNTRCT_EFCTV_DAYS)
	@Min(value=0,message=ErrorMessages.INVALID_DROOLS_CNTRCT_EFCTV_DAYS)
	private Integer contractCushionPeriod;
	
	@NotNull(message=ErrorMessages.MISSING_DROOLS_DISTANCE)
	private Double distance;
	
	
	private Integer panelCapacityPercent;
	
	//@NotNull(message="providerTiers in Rules should be present")
	private Integer[] providerTiers;

	@Size(min = 1, message = ErrorMessages.MISSING_DROOLS_VALIDATION_RULESET)
	@NotNull(message=ErrorMessages.MISSING_DROOLS_VALIDATION_RULESET)
	private List<@NotNull(message =ErrorMessages.MISSING_DROOLS_VALIDATION_RULESET) @Valid ActionPair<String, Boolean>> validationFlagList = new ArrayList<>();
	
	@NotNull(message=ErrorMessages.MISSING_DROOLS_ROLLOVER_FLAG)
	@Pattern(regexp = "[Y|y|N|n]", message = ErrorMessages.INVALID_DROOLS_ROLLOVER_FLAG)
	private String rolloverFlag;
	
	private int poolSize;
	
	private String dummyProviderId;
	
	
	public enum Validations {
		SPECIALTY_VALIDATION, AGE_RANGE_VALIDATION, GENDER_VALIDATION, CONTRACT_VALIDATION, DISTANCE_VALIDATION, PANEL_CAPACITY_VALIDATION, NETWORK_VALIDATION, TIER_VALIDATION, ACCEPTING_PATIENTS_VALIDATION, ROLLOVER_VALIDATION
	}

	
	public String[] getPrimarySpecialties() {
		return primarySpecialties;
	}

	public void setPrimarySpecialties(String[] primarySpecialties) {
		this.primarySpecialties = primarySpecialties;
	}

	public List<ActionPair<String, Integer>> getSpecialtyMinAgeList() {
		return specialtyMinAgeList;
	}

	public List<ActionPair<String, Integer>> getSpecialtyMaxAgeList() {
		return specialtyMaxAgeList;
	}

	public int getMinAgeAllowedForSpecialty(String specialty) {
		Optional<ActionPair<String, Integer>> val = specialtyMinAgeList.stream()
				.filter(ap -> ap.getKey().equals(specialty)).findAny();
		return (val.isPresent()) ? val.get().getValue() : MIN_AGE;
	}

	public int getMaxAgeAllowedForSpecialty(String specialty) {
		Optional<ActionPair<String, Integer>> val = specialtyMaxAgeList.stream()
				.filter(ap -> ap.getKey().equals(specialty)).findAny();
		return (val.isPresent()) ? val.get().getValue() : MAX_AGE;
	}

	public List<ActionPair<String, String>> getSpecialtyGenderList() {
		return specialtyGenderList;
	}

	public String getGenderForSpecialty(String specialty) {
		Optional<ActionPair<String, String>> val = specialtyGenderList.stream()
				.filter(ap -> ap.getKey().equals(specialty)).findAny();
		return (val.isPresent()) ? val.get().getValue() : "";
	}

	public Integer getContractCushionPeriod() {
		return contractCushionPeriod;
	}

	public Integer getPanelCapacityPercent() {
		return panelCapacityPercent;
	}

	public Integer[] getProviderTiers() {
		return providerTiers;
	}

	public List<ActionPair<String, Boolean>> getValidationFlagList() {
		return validationFlagList;
	}

	public boolean isValidationRequired(Validations val) {
		Optional<ActionPair<String, Boolean>> ret = validationFlagList.stream()
				.filter(ap -> ap.getKey().equals(val.name())).findAny();
		return (ret.isPresent()) ? ret.get().getValue() : false;
	}

	public void setSpecialtyMinAgeList(List<ActionPair<String, Integer>> specialtyMinAgeList) {
		this.specialtyMinAgeList = specialtyMinAgeList;
	}

	public void setSpecialtyMaxAgeList(List<ActionPair<String, Integer>> specialtyMaxAgeList) {
		this.specialtyMaxAgeList = specialtyMaxAgeList;
	}

	public void setSpecialtyGenderList(List<ActionPair<String, String>> specialtyGenderList) {
		this.specialtyGenderList = specialtyGenderList;
	}

	public void setContractCushionPeriod(Integer contractCushionPeriod) {
		this.contractCushionPeriod = contractCushionPeriod;
	}

	public void setPanelCapacityPercent(Integer panelCapacityPercent) {
		this.panelCapacityPercent = panelCapacityPercent;
	}

	public void setProviderTiers(Integer[] providerTiers) {
		this.providerTiers = providerTiers;
	}

	public void setValidationFlagList(List<ActionPair<String, Boolean>> validationFlagList) {
		this.validationFlagList = validationFlagList;
	}

	public String getRolloverFlag() {
		return rolloverFlag;
	}

	public void setRolloverFlag(String rolloverFlag) {
		this.rolloverFlag = rolloverFlag;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getDummyProviderId() {
		return dummyProviderId;
	}

	public void setDummyProviderId(String dummyProviderId) {
		this.dummyProviderId = dummyProviderId;
	}
	
	
}
