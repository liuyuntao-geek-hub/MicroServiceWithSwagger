package com.anthem.hca.smartpcp.providervalidation.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthem.hca.smartpcp.constants.ProviderValidationConstants;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidation;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.util.AgeCalculation;




public class ProviderValidationLogic {

	private static final Logger logger = LoggerFactory.getLogger(ProviderValidationLogic.class);

	/**
	 * Method for Age Validation Current Age Validation is only applicable for
	 * Pediatrics and Geriatrics Specialities.Other Specialities are not applicable
	 * for this validation
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return boolean
	 */
	public boolean isAgeValidate(Member member, Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;
		List<String> specialityList = null;
		Integer minAge = null;
		Integer maxAge = null;
		double memberAge = 0;

		specialityList = provInfo.getSpecialityDesc();

		if (ruleSet.isValidationRequired(ProviderValidation.AGE_RANGE_VALIDATION) && specialityList != null
				&& !StringUtils.isBlank(member.getMemberDob())) {

			for (String speciality : specialityList) {
				minAge = ruleSet.getMinAgeAllowedForSpecialty(speciality);
				maxAge = ruleSet.getMaxAgeAllowedForSpecialty(speciality);

				memberAge = AgeCalculation.calculateAge(member.getMemberDob());

				if ((null != minAge) && (null != maxAge) && !(memberAge >= minAge && memberAge <= maxAge)) {
					validation = false;
				} else {
					validation = true;
					break;
				}
			}
		} else if (specialityList == null) {
			validation = false;
			logger.error("Specialty  is missing for provider with Provider id= {}", provInfo.getProvPcpId());
		} else {
			validation = false;
			logger.error("Member DateofBirth is missing for member with member id= {}", member.getMemberEid());
		}

		return validation;

	}

	/**
	 * Method for Gender Validation Current Gender Validation is only applicable for
	 * OB/GYN Speciality.Other Specialities are not applicable for this Validation
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return Boolean
	 */
	public boolean isGendrValidate(Member member, Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;
		List<String> specialityList = null;
		String rulesGender = null;

		if (ruleSet.isValidationRequired(ProviderValidation.GENDER_VALIDATION)) {

			specialityList = provInfo.getSpecialityDesc();

			if (specialityList != null && !StringUtils.isBlank(member.getMemberGender())) {
				for (String speciality : specialityList) {
					rulesGender = ruleSet.getGenderForSpecialty(speciality);

					if (!StringUtils.isBlank(rulesGender)
							&& !(member.getMemberGender().trim().equalsIgnoreCase(rulesGender))) {
						validation = false;
					} else {
						validation = true;
						break;
					}
				}
			} else if (specialityList == null) {
				validation = false;
				logger.error("Specialty Description is missing for provider with Provider id={}",
						provInfo.getProvPcpId());
			} else {
				validation = false;
				logger.error("Member Gender is missing for member with member id={}", member.getMemberEid());
			}

		}

		return validation;
	}

	/**
	 * Method for Speciality Validation
	 * 
	 * @param provInfo
	 * @param ruleSet
	 * @return boolean
	 */
	public boolean isSpecialtyValidate(Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;
		List<String> specialityList = null;
		String[] rulesSpecialityList = null;

		specialityList = provInfo.getSpecialityDesc();

		if (ruleSet.isValidationRequired(ProviderValidation.SPECIALTY_VALIDATION) && specialityList != null) {

			rulesSpecialityList = ruleSet.getPrimarySpecialties();
			for (String rulespeciality : rulesSpecialityList) {
				for (String speciality : specialityList) {
					if (rulespeciality != null && speciality != null && speciality.equalsIgnoreCase(rulespeciality)) {
						validation = true;
						break;
					} else {
						validation = false;
					}
				}
				if(validation)
					break;
			}
		} else {
			validation = false;
			logger.error("Specialty Description is missing for provider with Provider id= {}", provInfo.getProvPcpId());
		}

		return validation;
	}

	/**
	 * Method for Network Validation
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return boolean
	 */
	public boolean isNtwkValidate(Member member, Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;
		String provNetworkId = null;
		List<String> memNetworkId = null;

		if (ruleSet.isValidationRequired(ProviderValidation.NETWORK_VALIDATION)) {

			if (null != provInfo.getRgnlNtwkId()) {
				provNetworkId = provInfo.getRgnlNtwkId().trim();
			}

			if (null != member.getMemberNetworkId() && !member.getMemberNetworkId().isEmpty()) {
				memNetworkId = member.getMemberNetworkId();
			} else {
				memNetworkId = member.getMemberContractCode();
			}

			if (null != provNetworkId && null != memNetworkId && memNetworkId.contains(provNetworkId)) {
				validation = true;
			} else if (null == provNetworkId) {
				validation = false;
				logger.error("Provider NetworkId is missing for provider with Provider id= {}",
						provInfo.getProvPcpId());
			} else {
				validation = false;
				logger.error("Member NetworkId is missing for member with member id= {}", member.getMemberEid());
			}

		}
		return validation;
	}

	/**
	 * Method for Contract Validation
	 * 
	 * @param provInfo
	 * @param ruleSet
	 * @return boolean
	 */
	public boolean isCntrctValidate(Member member, Provider provInfo, ProviderValidationRules ruleSet) {
		boolean validation = true;
		LocalDate rulesDate = null;
		LocalDate providerTerminationDate = null;
		LocalDate providerEffectiveDate = null;
		LocalDate memberEffectiveDate = null;

		if (ruleSet.isValidationRequired(ProviderValidation.CONTRACT_VALIDATION)) {

			if (null != provInfo.getGrpgRltdPadrsTrmntnDt() && null != provInfo.getGrpgRltdPadrsEfctvDt()
					&& null != member.getMemberEffectiveDate()) {

				memberEffectiveDate = LocalDate.parse(member.getMemberEffectiveDate());

				providerEffectiveDate = provInfo.getGrpgRltdPadrsEfctvDt().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				providerTerminationDate = provInfo.getGrpgRltdPadrsTrmntnDt().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				if (memberEffectiveDate.isAfter(LocalDate.now())) {
					rulesDate = memberEffectiveDate.plusDays(ruleSet.getContractCushionPeriod() - (long) 1);
				} else {
					rulesDate = LocalDate.now().plusDays(ruleSet.getContractCushionPeriod() - (long) 1);
				}

				if ((memberEffectiveDate.isAfter(providerEffectiveDate)
						|| memberEffectiveDate.equals(providerEffectiveDate))
						&& providerTerminationDate.isAfter(memberEffectiveDate)
						&& providerTerminationDate.isAfter(rulesDate)) {
					validation = true;
				} else {
					validation = false;
				}

			} else {
				validation = false;
				logger.error("Provider Termination Date is missing for provider with Provider id= {}",
						provInfo.getProvPcpId());
			}

		}

		return validation;
	}

	/**
	 * Method for Distance Validation
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return true or false
	 */
	public boolean isDistanceValidate(Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;
		Double distanceVal = provInfo.getDrivingDistance() != null ? provInfo.getDrivingDistance()
				: provInfo.getAerialDistance();

		if (ruleSet.isValidationRequired(ProviderValidation.DISTANCE_VALIDATION)) {

			if (null != distanceVal) {
				if (distanceVal >= 0.0 && ruleSet.getDistance() >= 0.0) {
					validation = (distanceVal <= ruleSet.getDistance());
				} else {
					validation = false;
				}
			} else {
				validation = false;
				logger.error("Driving Distance is missing for provider with Provider id={}", provInfo.getProvPcpId());
			}
		}

		return validation;

	}

	/**
	 * Method for Tier Validation
	 * 
	 * @param provInfo
	 * @param ruleSet
	 * @return boolean
	 */
	public boolean isTierValidate(Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;

		if (ruleSet.isValidationRequired(ProviderValidation.TIER_VALIDATION)) {

			if (null != ruleSet.getProviderTiers() && null != provInfo.getTierLvl()) {
				return (Arrays.asList(ruleSet.getProviderTiers()).contains(provInfo.getTierLvl())
						&& provInfo.getTierLvl().equals(ProviderValidationConstants.TIER_LEVEL_1)) ? true : false;
			} else {
				validation = false;
				logger.info("Tier Level is missing for provider with Provider id= {}", provInfo.getProvPcpId());
			}
		}
		return validation;

	}

	/**
	 * Method for Accept new patient Validation
	 * 
	 * @param provInfo
	 * @return boolean
	 */
	public boolean isAcceptPatient(Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;

		if (ruleSet.isValidationRequired(ProviderValidation.ACCEPTING_PATIENTS_VALIDATION)) {

			if (null != provInfo.getAccNewPatientFlag()) {
				return (provInfo.getAccNewPatientFlag().equalsIgnoreCase(ProviderValidationConstants.ACCEPT_NEW_PATIENT)) ? true : false;
			} else {
				validation = false;
				logger.info("CpTypeCd is missing for provider with Provider id= {}", provInfo.getProvPcpId());
			}
		}

		return validation;
	}

	/**
	 * Method for RollOver Validation
	 * 
	 * @param member
	 * @param provInfo
	 * @return boolean
	 */
	public boolean isRollOver(Member member, Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;

		if (ruleSet.isValidationRequired(ProviderValidation.ROLLOVER_VALIDATION)) {

			if (!StringUtils.isBlank(ruleSet.getRolloverFlag()) && !StringUtils.isBlank(member.getRollOverPcpId())) {
				return (ruleSet.getRolloverFlag().matches(ProviderValidationConstants.INVOCATION_SYSTEM)
						&& provInfo.getProvPcpId().trim().equalsIgnoreCase(member.getRollOverPcpId())) ? false : true;
			} else {
				validation = true;
			}
		}

		return validation;

	}

	/**
	 * Method for Panel Capiacity Validation
	 * 
	 * @param provInfo
	 * @param ruleSet
	 * @return boolean
	 */
	public boolean isPanelCpcityValidate(Provider provInfo, ProviderValidationRules ruleSet) {

		boolean validation = true;

		if (ruleSet.isValidationRequired(ProviderValidation.PANEL_CAPACITY_VALIDATION)) {

			if (null != provInfo.getCurntMbrCnt() && null != provInfo.getMaxMbrCnt()) {
				double provPanelCap = ((provInfo.getCurntMbrCnt() / provInfo.getMaxMbrCnt()) * 100);

				if (provPanelCap <= ruleSet.getPanelCapacityPercent()) {
					validation = true;

				} else {
					validation = false;
				}
			} else if (null == provInfo.getCurntMbrCnt()) {
				validation = false;
				logger.error("Curret Member Count is missing for provider with Provider id= {}",
						provInfo.getProvPcpId());
			} else {
				validation = false;
				logger.error("Max Member Count is missing for provider with Provider id= {}", provInfo.getProvPcpId());
			}

		}

		return validation;
	}

}
