package com.anthem.hca.smartpcp.drools.rules;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import com.anthem.hca.smartpcp.drools.model.ActionPair;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.model.Member;

/**
 * The ProviderValidationRules class is used to encapsulate all the properties and behaviors of
 * a Provider Validation Rule. It extends the base Rules class and also provides a custom implementation
 * of the isFallbackRequired() method.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.7
 */

public class ProviderValidationRules extends AbstractRules {

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

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains Number of days a PCP's Contract is valid beyond the Expiration Date")
	private int contractEffectiveBeyond;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains Driving distance between Member and a PCP")	
	private int drivingDistance;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Maximum allowable Panel Capacity of a PCP")
	private int panelCapacity;

	@ApiModelProperty(required = true, dataType = "int[]", notes = "Contains Tiering information for a Provider")
	private int[] providerTiers;

	@ApiModelProperty(required = true, dataType = "String", notes = "Contains information whether Member has requested for PCP Rollover or not (Y/N/YES/NO)")
	private String rolloverFlag;

	@ApiModelProperty(required = true, dataType = "List", notes = "Contains a List of all Provider Validation modules and whether that needs to be executed")
	private List<ActionPair<String, Boolean>> validationFlag = new ArrayList<>();

	public ProviderValidationRules(AgendaGroup ag) {
		setAgendaGroup(ag);
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

	public int getContractEffectiveBeyond() {
		return contractEffectiveBeyond;
	}

	public void setContractEffectiveBeyond(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				contractEffectiveBeyond = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Contract Effective Beyond cannot be empty in Provider-Contract-Date-Rules.xls");
		}
	}

	public int getDrivingDistance() {
		return drivingDistance;
	}

	public void setDrivingDistance(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				drivingDistance = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Driving Distance cannot be empty in Provider-Driving-Distance-Rules.xls");
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

	@Override
	public void setMarketParams(Member m) {
		if (getAgendaGroup() == AgendaGroup.INVOCATION_SYSTEM_MAPPING) {
			setInvocationSystem(m.getInvocationSystem());
		}
		else {
			super.setMarketParams(m);
		}
	}

	@Override
	@JsonIgnore
	public boolean isFallbackRequired() {
		return (getAgendaGroup() == AgendaGroup.INVOCATION_SYSTEM_MAPPING)
				? false		// For Invocation System to Rollover Flag Mapping, Fallback rules are never required
				: primarySpecialties == null
				  || specialty == null
				  || specialtyMinAge.isEmpty()
				  || specialtyMaxAge.isEmpty()
				  || specialtyGender.isEmpty()
				  || providerTiers == null
				  || validationFlag.isEmpty()
			;
		/*
		 * Default values of the following attributes should not be checked
		 * to determine if Rules have been successfully fired or not. This
		 * is because the Default values of these attributes can be same as
		 * valid data provided by the Business.
		 * 
		 * contractEffectiveBeyond == 0
		 * drivingDistance == 0
		 * panelCapacity == 0
		 */
	}

}
