package com.anthem.hca.smartpcp.providervalidation.helper;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.utility.ProviderValidationLogic;
import com.anthem.hca.smartpcp.providervalidation.vo.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProviderValidationHelper {

	private static final Logger logger = LoggerFactory.getLogger(ProviderValidationHelper.class);

	/**
	 * This method will be called from Affinity Service to trigger Provider
	 * validations for Affinity
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return PCP or null
	 */
	public PCP getPCPForAffinity(Member member, List<PCP> provInfo, JsonNode rules) {
		Rules ruleSet = null;
		ProviderValidationLogic proLog = null;
		logger.debug("Affinity Provider Validation build started {}", "");
		logger.debug("ProviderValidationHelper - getPCPForAffinity :: input pcp list {}",
				provInfo == null ? 0 : provInfo.size());
		try {
			ruleSet = new ObjectMapper().treeToValue(rules, Rules.class);
		} catch (JsonProcessingException e) {
			logger.error("Error occurred while convertig Rules Json Node to Rules Pojo {}", e.getMessage());
		}
		boolean ageValidation = false;
		boolean specialityValidation = false;
		boolean contractValidation = false;
		boolean rollOver = false;
		boolean drivingValidation = false;
		boolean genderVlidation = false;
		proLog = new ProviderValidationLogic();
		PCP pcp = null;
		for (PCP pcpDtl : provInfo) {

			if (!StringUtils.isBlank(pcpDtl.getProvPcpId())) {
				ageValidation = proLog.isAgeValidate(member, pcpDtl, ruleSet);
				genderVlidation = proLog.isGendrValidate(member, pcpDtl, ruleSet);
				specialityValidation = proLog.isSpecialtyValidate(pcpDtl, ruleSet);
				// boolean networkValidation = proLog.isNtwkValidate(member, pcpDtl, ruleSet);
				contractValidation = proLog.isCntrctValidate(member, pcpDtl, ruleSet);
				// boolean tierValidation = proLog.isTierValidate(pcpDtl, ruleSet);
				rollOver = proLog.isRollOver(member, pcpDtl, ruleSet);
				drivingValidation = proLog.isDistanceValidate(pcpDtl, ruleSet);

				if (ageValidation && genderVlidation && specialityValidation && contractValidation && rollOver
						&& drivingValidation) {
					pcp = pcpDtl;
					break;

				}
			} else {
				logger.error("Provider PCPId is null for the current request{}", "");
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
	 * @throws JsonProcessingException 
	 **/
	public List<PCP> getPCPListMDO(Member member, List<PCP> provInfo, JsonNode rules) throws JsonProcessingException {
		Rules ruleSet = null;
		List<PCP> listDtls = new ArrayList<>();
		ProviderValidationLogic proLog = null;
		logger.debug("MDO Provider Validation build started {}", "");
		logger.debug("ProviderValidationHelper - getFinalPCPMDO :: input pcp list {}",
				provInfo == null ? 0 : provInfo.size());
		proLog = new ProviderValidationLogic();

		ruleSet = new ObjectMapper().treeToValue(rules, Rules.class);
		
		boolean ageValidation = false;
		boolean contractValidation = false;
		boolean specialityValidation = false;
		boolean acceptNewPatient = false;
		boolean rollOver = false;
		boolean panelCapacity = false;
		boolean genderVlidation = false;
		for (PCP pcpDtl : provInfo) {

			if (!StringUtils.isBlank(pcpDtl.getProvPcpId())) {
				ageValidation = proLog.isAgeValidate(member, pcpDtl, ruleSet);
				genderVlidation = proLog.isGendrValidate(member, pcpDtl, ruleSet);
				specialityValidation = proLog.isSpecialtyValidate(pcpDtl, ruleSet);
				// boolean networkValidation = proLog.isNtwkValidate(member, pcpDtl, ruleSet);
				contractValidation = proLog.isCntrctValidate(member, pcpDtl, ruleSet);
				// boolean tierValidation = proLog.isTierValidate(pcpDtl, ruleSet);
				acceptNewPatient = proLog.isAcceptPatient(pcpDtl, ruleSet);
				rollOver = proLog.isRollOver(member, pcpDtl, ruleSet);
				panelCapacity = proLog.isPanelCpcityValidate(pcpDtl, ruleSet);

				if (ageValidation && genderVlidation && specialityValidation && contractValidation && acceptNewPatient
						&& rollOver && panelCapacity) {

					listDtls.add(pcpDtl);
					if (ruleSet != null && listDtls.size() == ruleSet.getPoolSize())
						break;

				}
			} else {
				logger.error("Provider PCPId is null for the current request{}", "");
			}
		}
		return listDtls;
	}

}
