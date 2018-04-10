package com.anthem.hca.smartpcp.model;

import java.io.Serializable;
import java.util.Map;

public class Rules  implements Serializable{

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
	};

	// Attributes required for Decision Table Conditions
	private String market;
	private String lob;
	private String product;
	private String assignmentType;
	private String assignmentMethod;
	private String invocationOrder;

	// Use Market specific rules or Default rules
	//private boolean isDefault = true;
	private boolean fallbackRequired;

	
	// Primary PCP specialties allowed for a combination of LOB/Market/Product/AssignmentType/AssignmentMethod/Agenda-Group	
	private String[] primarySpecialties;

	// Number of days allowed after which Provider can terminate for a combination of LOB/Market/Product/AssignmentType/AssignmentMethod/Agenda-Group	
	private int daysToTerminationAllowed;

	// Max Driving distance to find PCP for a combination of LOB/Market/Product/AssignmentType/AssignmentMethod/Agenda-Group	
	private int drivingDistance;

	// Map containing Specialties and Minimum allowed age of a Member that the Specialist can diagnose
	private Map<String, Integer> specialtyMinAge;

	// Map containing Specialties and Maximum allowed age of a Member ` the Specialist can diagnose
	private Map<String, Integer> specialtyMaxAge ;
	// Map containing Provider Validation Names and a flag (TRUE/FALSE) which signifies whether it is required to be executed
	private Map<String, Boolean> validationYesNoFlag ;
	
	// Map containing Specialties and Gender allowed of a Member that the Specialist can diagnose
	private Map<String, String> specialtyGender;

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

	public void setInvocationOrder(String invocationOrder) {
		this.invocationOrder = invocationOrder;
	}

	public boolean isFallbackRequired() {
		return fallbackRequired;
	}

	public void setFallbackRequired(boolean fallbackRequired) {
		this.fallbackRequired = fallbackRequired;
	}

	public String[] getPrimarySpecialties() {
		return primarySpecialties;
	}

	public void setPrimarySpecialties(String[] primarySpecialties) {
		this.primarySpecialties = primarySpecialties;
	}

	public int getDaysToTerminationAllowed() {
		return daysToTerminationAllowed;
	}

	public void setDaysToTerminationAllowed(int daysToTerminationAllowed) {
		this.daysToTerminationAllowed = daysToTerminationAllowed;
	}

	public int getDrivingDistance() {
		return drivingDistance;
	}

	public void setDrivingDistance(int drivingDistance) {
		this.drivingDistance = drivingDistance;
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
}

