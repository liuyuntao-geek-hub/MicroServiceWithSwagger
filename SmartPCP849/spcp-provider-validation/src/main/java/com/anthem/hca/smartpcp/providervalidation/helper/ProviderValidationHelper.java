/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation.helper;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anthem.hca.smartpcp.providervalidation.utility.ProviderValidationLogic;
import com.anthem.hca.smartpcp.providervalidation.vo.Member;
import com.anthem.hca.smartpcp.providervalidation.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.vo.Rules;

public class ProviderValidationHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderValidationHelper.class);

	/**
	 * This method will be called from Affinity Service to trigger Provider
	 * validations for Affinity
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return PCP or null
	 */
	public PCP getPCPForAffinity(Member member, List<PCP> provInfo, Rules ruleSet) {
		ProviderValidationLogic proLog = null;
		LOGGER.debug("Affinity Provider Validation build started {}", "");
		proLog = new ProviderValidationLogic();
		PCP pcp = null;
		boolean ageValidation = false;
		boolean specialityValidation = false;
		boolean networkValidation = false;
		boolean contractValidation = false;
		boolean drivingValidation = false;
		boolean rollOver = false;
		boolean genderVlidation = false;
		for (PCP pcpDtl : provInfo) {

			if (!StringUtils.isBlank(pcpDtl.getProvPcpId())) {
				ageValidation = proLog.isAgeValidate(member, pcpDtl, ruleSet);
				genderVlidation = proLog.isGendrValidate(member, pcpDtl, ruleSet);
				specialityValidation = proLog.isSpecialtyValidate(pcpDtl, ruleSet);
				networkValidation = proLog.isNtwkValidate(member, pcpDtl, ruleSet);
				contractValidation = proLog.isCntrctValidate(member, pcpDtl, ruleSet);
				// boolean tierValidation = proLog.isTierValidate(pcpDtl, ruleSet);
				rollOver = proLog.isRollOver(member, pcpDtl, ruleSet);
				drivingValidation = proLog.isDistanceValidate(pcpDtl, ruleSet);
				
				LOGGER.debug("ageValidation = {}, genderVlidation = {}, specialityValidation = {} networkValidation = {}, contractValidation = {},  rollOver = {}, drivingValidation = {}",
						ageValidation,genderVlidation,specialityValidation,networkValidation,contractValidation,rollOver,drivingValidation);
				
				if (ageValidation && genderVlidation && specialityValidation && contractValidation && rollOver
						&& networkValidation && drivingValidation) {
					pcp = pcpDtl;
					break;

				}
			} else {
				LOGGER.error("Provider PCPId is null for the current request{}", "");
			}
		}
		return pcp;
	}

	/**
	 * This method will be called from MDO Service to trigger Provider validations
	 * for MDO
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return List<PCP> or empty List
	 **/
	public List<PCP> getPCPListMDO(Member member, List<PCP> provInfo, Rules ruleSet) {
		List<PCP> listDtls = null;
		ProviderValidationLogic proLog = null;
		LOGGER.debug("MDO Provider Validation build started {}", "");
		proLog = new ProviderValidationLogic();
		listDtls = new ArrayList<>();

		boolean ageValidation = false;
		boolean specialityValidation = false;
		boolean networkValidation = false;
		boolean contractValidation = false;
		boolean acceptNewPatient = false;
		boolean rollOver = false;
		boolean panelCapacity = false;
		boolean genderVlidation = false;
		for (PCP pcpDtl : provInfo) {

			if (!StringUtils.isBlank(pcpDtl.getProvPcpId())) {
				ageValidation = proLog.isAgeValidate(member, pcpDtl, ruleSet);
				genderVlidation = proLog.isGendrValidate(member, pcpDtl, ruleSet);
				specialityValidation = proLog.isSpecialtyValidate(pcpDtl, ruleSet);
				networkValidation = proLog.isNtwkValidate(member, pcpDtl, ruleSet);
				contractValidation = proLog.isCntrctValidate(member, pcpDtl, ruleSet);
				// boolean tierValidation = proLog.isTierValidate(pcpDtl, ruleSet);
				//drivingValidation = proLog.isDrivingValidate(pcpDtl, ruleSet);
				acceptNewPatient = proLog.isAcceptPatient(pcpDtl, ruleSet);
				rollOver = proLog.isRollOver(member, pcpDtl, ruleSet);
				panelCapacity = proLog.isPanelCpcityValidate(pcpDtl, ruleSet);

				LOGGER.debug("ageValidation = {}, genderVlidation = {}, specialityValidation = {} networkValidation = {}, contractValidation = {},  rollOver = {}",
						ageValidation,genderVlidation,specialityValidation,networkValidation,contractValidation,rollOver);
				
				
				if (ageValidation && genderVlidation && specialityValidation &&
						networkValidation && contractValidation && acceptNewPatient && rollOver && panelCapacity) {

					listDtls.add(pcpDtl);

				}
			} else {
				LOGGER.error("Provider PCPId is null for the current request{}", "");
			}
		}
		LOGGER.debug("Validated PCP's list size= {}", listDtls.size());
		return listDtls;
	}

}
