package com.anthem.hca.smartpcp.drools.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 */
public class Rules {

	// All Provider Validations
	public enum ProviderValidation {
		SPECIALTY_VALIDATION,
		AGE_RANGE_VALIDATION,
		GENDER_VALIDATION,
		NETWORK_VALIDATION,
		CONTRACT_VALIDATION,
		DISTANCE_VALIDATION,
		ACCEPTING_PATIENTS_VALIDATION,
		PANEL_CAPACITY_VALIDATION
	}

	// Attributes required for Decision Table Conditions
	private String market;
	private String lob;
	private String product;
	private String assignmentType;
	private String assignmentMethod;
	private String invocationOrder;
	
	private int poolSize;
	private int maxRadiusToPool;
	private String ageMatch;
	private int ageScore;
	private String languageMatch;
	private int languageScore;
	
	private String limitedTimePanelLimit;
	private int limitedTimeTimeLimit;
	private int limitedTimeScore;
	private String proximity;
	private int proximityScore;
	private String specialityMatch;
	private int specialityScore;
	

	// Use Rules given in sheet or switch to Fallback
	private boolean fallbackRequired = true;

	// Primary PCP specialties allowed for a combination of LOB/Market/Product/AssignmentType/AssignmentMethod/Agenda-Group	
	private String[] primarySpecialties;

	// Number of days allowed after which Provider can terminate for a combination of LOB/Market/Product/AssignmentType/AssignmentMethod/Agenda-Group	
	private int daysToTerminationAllowed;

	// Max Driving distance to find PCP for a combination of LOB/Market/Product/AssignmentType/AssignmentMethod/Agenda-Group	
	private int drivingDistance;

	// Map containing Specialties and Minimum allowed age of a Member that the Specialist can diagnose
	private Map<String, Integer> specialtyMinAge = new HashMap<>();

	// Map containing Specialties and Maximum allowed age of a Member that the Specialist can diagnose
	private Map<String, Integer> specialtyMaxAge = new HashMap<>();

	// Map containing Provider Validation Names and a flag (TRUE/FALSE) which signifies whether it is required to be executed
	private Map<String, Boolean> validationYesNoFlag = new HashMap<>();
	
	// Map containing Specialties and Gender allowed of a Member that the Specialist can diagnose
	private Map<String, String> specialtyGender = new HashMap<>();

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

	public String getInvocationOrder() {
		return invocationOrder;
	}

	public void setInvocationOrder(String order) {
		this.invocationOrder = order;
	}
	
	public boolean isFallbackRequired() {
        return fallbackRequired;
	}

	public void setToActualOrFallback(String value) {
        this.fallbackRequired = !value.equalsIgnoreCase("ACTUAL");
	}

	public String[] getPrimarySpecialties() {
		return this.primarySpecialties;
	}

	public void setPrimarySpecialties(String specialties) {
		primarySpecialties = specialties.split(",");
	}

	public int getDaysToTerminationAllowed() {
		return this.daysToTerminationAllowed;
	}

	public void setDaysToTerminationAllowed(int days) {
		this.daysToTerminationAllowed = days;
	}

	public int getDrivingDistance() {
		return this.drivingDistance;
	}

	public void setDrivingDistance(int distance) {
		this.drivingDistance = distance;
	}

	public Integer getMinAllowedAgeForSpeciality(String specialty) {
		return specialtyMinAge.get(specialty);
	}

	public Integer getMaxAllowedAgeForSpeciality(String specialty) {
		return specialtyMaxAge.get(specialty);
	}

	public void setMinAllowedAgeForSpeciality(String specialty, Integer age) {
		specialtyMinAge.put(specialty, age);
	}

	public void setMaxAllowedAgeForSpeciality(String specialty, Integer age) {
		specialtyMaxAge.put(specialty, age);
	}

	public String getGenderForSpeciality(String specialty) {
		return specialtyGender.get(specialty);
	}

	public void setGenderForSpeciality(String specialty, String genderCode) {
		specialtyGender.put(specialty, genderCode);
	}

	public void setAge(String data) {
		String[] specialtyAgeArr = data.split(",");

		for (String specialtyAge: specialtyAgeArr) {
			String[] elems = specialtyAge.split(":");
			String specialty = elems[0];
			Integer minAge = NumberUtils.toInt(elems[1], Integer.MIN_VALUE);
			Integer maxAge = (elems.length > 2)? NumberUtils.toInt(elems[2], Integer.MAX_VALUE): Integer.MAX_VALUE;

			setMinAllowedAgeForSpeciality(specialty, minAge);
			setMaxAllowedAgeForSpeciality(specialty, maxAge);
		}
	}

	public Boolean isValidationRequired(Rules.ProviderValidation val) {
		return validationYesNoFlag.get(val.name());
	}

	public void setValidationRequired(String validationName, String yesno) {
		if (yesno.equalsIgnoreCase("Y") || yesno.equalsIgnoreCase("YES"))
			validationYesNoFlag.put(validationName, Boolean.TRUE);
		else
			validationYesNoFlag.put(validationName, Boolean.FALSE);
	}

	public void setGender(String data) {
        String[] specialtyGenderArr = data.split(",");
        
        for (String specialtyGndr: specialtyGenderArr) {
        	String[] elems = specialtyGndr.split(":");
        	String specialty = elems[0];
        	String genderCode = elems[1];

        	setGenderForSpeciality(specialty, genderCode);
        }
	}

	public int getSpecialityScore() {
		return specialityScore;
	}

	public void setSpecialityScore(int specialityScore) {
		this.specialityScore = specialityScore;
	}

	public void setSpecialityMatch(String specialityMatch) {
		this.specialityMatch = specialityMatch;
	}

	public int getProximityScore() {
		return proximityScore;
	}
	public int getAgeScore() {
		return ageScore;
	}

	public void setAgeScore(int ageScore) {
		this.ageScore = ageScore;
	}


	public void setProximityScore(int proximityScore) {
		this.proximityScore = proximityScore;
	}

	public void setProximity(String proximity) {
		this.proximity = proximity;
	}
	public void setLimitedTimePanelLimit(String limitedTimePanelLimit) {
		this.limitedTimePanelLimit = limitedTimePanelLimit;
	}
	public void setLimitedTimeTimeLimit(int limitedTimeTimeLimit) {
		this.limitedTimeTimeLimit = limitedTimeTimeLimit;
	}

	public int getLimitedTimeScore() {
		return limitedTimeScore;
	}

	public void setLimitedTimeScore(int limitedTimeScore) {
		this.limitedTimeScore = limitedTimeScore;
	}

	

	public void setLanguageScore(int languageScore) {
		this.languageScore = languageScore;
	}

	public void setLanguageMatch(String languageMatch) {
		this.languageMatch = languageMatch;
	}


	public void setAgeMatch(String ageMatch) {
		this.ageMatch = ageMatch;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getMaxRadiusToPool() {
		return maxRadiusToPool;
	}

	public void setMaxRadiusToPool(int maxRadiusToPool) {
		this.maxRadiusToPool = maxRadiusToPool;
	}
	
	public String getAgeMatch() {
		return ageMatch;
	}

	public String getLanguageMatch() {
		return languageMatch;
	}

	public String getLimitedTimePanelLimit() {
		return limitedTimePanelLimit;
	}

	public int getLimitedTimeTimeLimit() {
		return limitedTimeTimeLimit;
	}

	public String getProximity() {
		return proximity;
	}

	public String getSpecialityMatch() {
		return specialityMatch;
	}

	public void setFallbackRequired(boolean fallbackRequired) {
		this.fallbackRequired = fallbackRequired;
	}

	public void setPrimarySpecialties(String[] primarySpecialties) {
		this.primarySpecialties = primarySpecialties;
	}

	
	public Map<String, Integer> getSpecialtyMinAge() {
		return specialtyMinAge;
	}

	public void setSpecialtyMinAge(Map<String, Integer> specialtyMinAge) {
		this.specialtyMinAge = specialtyMinAge;
	}

	public Map<String, Integer> getSpecialtyMaxAge() {
		return specialtyMaxAge;
	}

	public void setSpecialtyMaxAge(Map<String, Integer> specialtyMaxAge) {
		this.specialtyMaxAge = specialtyMaxAge;
	}

	public Map<String, Boolean> getValidationYesNoFlag() {
		return validationYesNoFlag;
	}

	public void setValidationYesNoFlag(Map<String, Boolean> validationYesNoFlag) {
		this.validationYesNoFlag = validationYesNoFlag;
	}

	public Map<String, String> getSpecialtyGender() {
		return specialtyGender;
	}

	public void setSpecialtyGender(Map<String, String> specialtyGender) {
		this.specialtyGender = specialtyGender;
	}

	public int getLanguageScore() {
		return languageScore;
	}
	
	@Override
	public String toString() {
		return "Rules [market=" + market + ", lob=" + lob + ", product=" + product + ", assignmentType="
				+ assignmentType + ", assignmentMethod=" + assignmentMethod + ", invocationOrder=" + invocationOrder
				+ ", fallbackRequired=" + fallbackRequired + ", primarySpecialties="
				+ Arrays.toString(primarySpecialties) + ", daysToTerminationAllowed=" + daysToTerminationAllowed
				+ ", drivingDistance=" + drivingDistance + ", specialtyMinAge=" + specialtyMinAge + ", specialtyMaxAge="
				+ specialtyMaxAge + ", validationYesNoFlag=" + validationYesNoFlag + ", specialtyGender="
				+ specialtyGender + "]";
	}

}
