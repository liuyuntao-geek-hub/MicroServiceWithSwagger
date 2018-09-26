package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;

import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.model.ActionPair;
import com.anthem.hca.smartpcp.model.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;

/**
 * The MDOPoolingProviderValidationRules class is used to encapsulate all the properties and behaviors of
 * a MDO Pooling and a Provider Validation Rule. It extends the base AbstractRules class and also provides
 * a custom implementation of the isFallbackRequired() method.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class MDOPoolingProviderValidationRules extends AbstractRules {

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Pool Size based on which Providers will be searched")
	private int poolSize;

	@ApiModelProperty(required = true, dataType = "String", notes = "Contains the Dummy PCP Id to consider if no Provider is found")
	private String dummyProviderId;

	public static final int MIN_AGE = 0;
	public static final int MAX_AGE = 999;

	private String invocationSystem;

	@ApiModelProperty(required = true, dataType = "String[]", notes = "Contains a List of Available Specialties")
	private String[] primarySpecialties;

	private String specialty;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of Specialties and Minimum Age of Patient allowed for each Specialty")
	private List<ActionPair<String, Integer>> specialtyMinAge = new ArrayList<>();

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of Specialties and Maximum Age of Patient allowed for each Specialty")
	private List<ActionPair<String, Integer>> specialtyMaxAge = new ArrayList<>();

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of Specialties and Gender of Patient allowed for each Specialty")
	private List<ActionPair<String, String>> specialtyGender = new ArrayList<>();

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Cushion Period (In Days) before the PCP's Contract expires")
	private int contractCushionPeriod;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains Distance (Driving for Affinity, Aerial for MDO) between Member and a PCP")	
	private int distance;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Maximum allowable Panel Capacity of a PCP")
	private int panelCapacity;

	@ApiModelProperty(required = true, dataType = "int[]", notes = "Contains Tiering information for a Provider")
	private int[] providerTiers;

	@ApiModelProperty(required = true, dataType = "String", notes = "Contains information whether Member has requested for PCP Rollover or not (Y/N/YES/NO)")
	private String rolloverFlag;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of all Provider Validation modules and whether that needs to be executed")
	private List<ActionPair<String, Boolean>> validationFlag = new ArrayList<>();

	public MDOPoolingProviderValidationRules(AgendaGroup ag) {
		setAgendaGroup(ag);
	}

	public MDOPoolingProviderValidationRules(MDOPoolingRules r1, ProviderValidationRules r2) {
		// Initialize MDO Pooling parameters
		this.poolSize = r1.getPoolSize();
		this.dummyProviderId = r1.getDummyProviderId();

		// Initialize Provider Validation parameters
		this.invocationSystem = r2.getInvocationSystem();
		this.primarySpecialties = r2.getPrimarySpecialties();
		this.specialtyMinAge = r2.getSpecialtyMinAgeList();
		this.specialtyMaxAge = r2.getSpecialtyMaxAgeList();
		this.specialtyGender = r2.getSpecialtyGenderList();
		this.contractCushionPeriod = r2.getContractCushionPeriod();
		this.distance = r2.getDistance();
		this.panelCapacity = r2.getPanelCapacityPercent();
		this.providerTiers = r2.getProviderTiers();
		this.rolloverFlag = r2.getRolloverFlag();
		this.validationFlag = r2.getValidationFlagList();
	}

	@JsonIgnore
	public String getInvocationSystem() {
		return invocationSystem;
	}

	public void setInvocationSystem(String invocationSystem) {
		this.invocationSystem = invocationSystem;
	}

	public String[] getPrimarySpecialties() {
		return primarySpecialties;
	}

	public void setPrimarySpecialties(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			primarySpecialties = param.split(RulesRegex.PRIMARY_SPECIALTIES);
			primarySpecialties = Arrays.stream(primarySpecialties).map(String::trim).toArray(String[]::new);
		}
		else {
			throw new DroolsParseException("Primary Specialties cannot be empty in Primary-Specialty-Code-Rules.xls");
		}
	}

	public void setSpecialty(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			specialty = param.trim();
		}
		else {
			throw new DroolsParseException("Specialty cannot be empty in Specialty-Age-Range-Rules.xls or Specialty-Gender-Rules.xls");
		}
	}
	
	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of Specialties and Minimum Age of Patient allowed for each Specialty")
	public List<ActionPair<String, Integer>> getSpecialtyMinAgeList() {
		return specialtyMinAge;
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of Specialties and Maximum Age of Patient allowed for each Specialty")
	public List<ActionPair<String, Integer>> getSpecialtyMaxAgeList() {
		return specialtyMaxAge;
	}

	public int getMinAgeAllowedForSpecialty(String specialty) {
		Optional<ActionPair<String, Integer>> val = specialtyMinAge.stream().filter(ap -> ap.getKey().equals(specialty)).findAny();
		return (val.isPresent()) ? val.get().getValue() : MIN_AGE;
	}

	public int getMaxAgeAllowedForSpecialty(String specialty) {
		Optional<ActionPair<String, Integer>> val = specialtyMaxAge.stream().filter(ap -> ap.getKey().equals(specialty)).findAny();
		return (val.isPresent()) ? val.get().getValue() : MAX_AGE;
	}

	public void setMinAgeAllowedForSpecialty(String param) throws DroolsParseException {
		int minAge = MIN_AGE;

		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				minAge = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}

		specialtyMinAge.add(new ActionPair<String, Integer>(specialty, minAge));
	}

	public void setMaxAgeAllowedForSpecialty(String param) throws DroolsParseException {
		int maxAge = MAX_AGE;

		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				maxAge = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}

		specialtyMaxAge.add(new ActionPair<String, Integer>(specialty, maxAge));
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of Specialties and Gender of Patient allowed for each Specialty")
	public List<ActionPair<String, String>> getSpecialtyGenderList() {
		return specialtyGender;
	}

	public String getGenderForSpecialty(String specialty) {
		Optional<ActionPair<String, String>> val = specialtyGender.stream().filter(ap -> ap.getKey().equals(specialty)).findAny();
		return (val.isPresent()) ? val.get().getValue() : "";
	}

	public void setGenderForSpecialty(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.GENDER)) {
				specialtyGender.add(new ActionPair<String, String>(specialty, param.trim()));
			}
			else {
				throw new DroolsParseException("Gender code must be one of these - 'M/F/MALE/FEMALE' in Specialty-Gender-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Gender cannot be empty in Specialty-Gender-Rules.xls");
		}
	}

	public int getContractCushionPeriod() {
		return contractCushionPeriod;
	}

	public void setContractCushionPeriod(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				contractCushionPeriod = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Contract Cushion Period cannot be empty in Provider-Contract-Date-Rules.xls");
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				distance = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Distance cannot be empty in Provider-Distance-Rules.xls");
		}
	}

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Maximum allowable Panel Capacity of a PCP")
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

	public int[] getProviderTiers() {
		return providerTiers;
	}

	public void setProviderTiers(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				providerTiers = Arrays.stream(param.split(RulesRegex.PROVIDER_TIERS)).mapToInt(elm -> Integer.parseInt(elm.trim())).toArray();
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Provider Tier(s) cannot be empty in Provider-Tier-Rules.xls");
		}
	}

	public String getRolloverFlag() {
		return (rolloverFlag == null) ? "N" : rolloverFlag;
	}

	public void setRolloverFlag(String param) throws DroolsParseException {
        if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
            if (param.matches(RulesRegex.YES_NO)) {
            	rolloverFlag = param.trim();
            }
            else {
                  throw new DroolsParseException("Rollover Flag must be one of these - 'Y/N/YES/NO' in Provider-Invocation-System-Mapping-Rules.xls");
            }
        }
        else {
            throw new DroolsParseException("Rollover Flag cannot be empty in Provider-Invocation-System-Mapping-Rules.xls");
        }
	}

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of all Provider Validation modules and whether that needs to be executed for Affinity or MDO")
	public List<ActionPair<String, Boolean>> getValidationFlagList() {
		return validationFlag;
	}

	public boolean isValidationRequired(ProviderValidation val) {
		Optional<ActionPair<String, Boolean>> ret = validationFlag.stream().filter(ap -> ap.getKey().equals(val.name())).findAny();
		return (ret.isPresent()) ? ret.get().getValue() : false;
	}

	public void setValidationRequired(String validationName, String param) throws DroolsParseException {
		if (null != validationName && "" != validationName) {
			if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
				if (param.matches(RulesRegex.YES_NO)) {
					validationFlag.add(new ActionPair<String, Boolean>(validationName, BooleanUtils.toBoolean(param.trim())));
				}
				else {
					throw new DroolsParseException("Validation Flag must be one of these - 'Y/N/YES/NO' in Provider-Validation-Invocation-Rules.xls");
				}
			}
			else {
				throw new DroolsParseException("Validation Flag cannot be empty in Provider-Validation-Invocation-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Validation Name cannot be empty in Provider-Validation-Invocation-Rules.xls");
		}
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				poolSize = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Pool Size cannot be empty in MDO-Provider-Pooling-Rules.xls");
		}
	}

	public String getDummyProviderId() {
		return dummyProviderId;
	}

	public void setDummyProviderId(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			dummyProviderId = param.trim();
		}
		else {
			throw new DroolsParseException("Dummy PCP cannot be empty in MDO-Provider-Pooling-Dummy-Pcp-Rules");
		}
	}

	@Override
	public void setMarketParams(Member m) {
		/*
		 * Business Requirement:
		 * 
		 * For Dummy PCP Rule: Use the LOB that is received in the Input Payload
		 * For All other Rules: Ignore the LOB from the Input Payload. Use 'Commercial' for Phase-1
		 * Also, for Dummy PCP Rule, we just need the LOB to fire the rule. Other Market parameters are not required.
		 */

		AgendaGroup group = getAgendaGroup();

		if (group == AgendaGroup.DUMMYPCP) {
			setLob(m.getMemberLineOfBusiness());
		}
		else if (group == AgendaGroup.INVOCATION_SYSTEM_MAPPING) {
			setInvocationSystem(m.getInvocationSystem());
		}
		else if (group == AgendaGroup.MDO_POOLING || group == AgendaGroup.MDO_PROVIDER_VALIDATION){
			super.setMarketParams(m);
		}

	}

	@Override
	@JsonIgnore
	public boolean isFallbackRequired() {

		AgendaGroup group = getAgendaGroup();

		boolean fallbackRequired = true;

		// For DummyPCP and Invocation System to Rollover Flag Mapping, Fallback rules are never required
		if (group == AgendaGroup.DUMMYPCP || group == AgendaGroup.INVOCATION_SYSTEM_MAPPING) {
			fallbackRequired = false;
		}
		else if (group == AgendaGroup.MDO_POOLING) {
			fallbackRequired = (poolSize == 0);
		}
		else if (group == AgendaGroup.MDO_PROVIDER_VALIDATION) {
			fallbackRequired =
				   primarySpecialties == null
				|| specialty == null
				|| specialtyMinAge.isEmpty()
				|| specialtyMaxAge.isEmpty()
				|| specialtyGender.isEmpty()
				|| providerTiers == null
				|| validationFlag.isEmpty();
		}

		/*
		 * Default values of the following attributes should not be checked
		 * to determine if Rules have been successfully fired or not. This
		 * is because the Default values of these attributes can be same as
		 * valid data provided by the Business.
		 * 
		 * contractCushionPeriod == 0
		 * distance == 0
		 * panelCapacity == 0
		 */

		return fallbackRequired;
	}

}
