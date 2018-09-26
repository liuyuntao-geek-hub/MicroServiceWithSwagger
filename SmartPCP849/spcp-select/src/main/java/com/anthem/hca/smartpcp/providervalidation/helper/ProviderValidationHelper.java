package com.anthem.hca.smartpcp.providervalidation.helper;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Provider;

@Service
public class ProviderValidationHelper {

	private static final Logger logger = LoggerFactory.getLogger(ProviderValidationHelper.class);
	
	@Autowired
	private ProviderValidationLogic providerValidationLogic;

	/**
	 * This method will be called from Affinity Service to trigger Provider
	 * validations for Affinity
	 * 
	 * @param member
	 * @param provInfo
	 * @param ruleSet
	 * @return Provider or null
	 */
	public Provider getPCPForAffinity(Member member, List<Provider> provInfo, ProviderValidationRules rules) {
		logger.debug("Affinity Provider Validation build started {}", "");
		logger.debug("ProviderValidationHelper - getPCPForAffinity :: input pcp list {}",
				provInfo == null ? 0 : provInfo.size());
		boolean ageValidation = false;
		boolean specialityValidation = false;
		boolean contractValidation = false;
		boolean rollOver = false;
		boolean drivingValidation = false;
		boolean genderVlidation = false;
		Provider pcp = null;
		for (Provider pcpDtl : provInfo) {

			if (!StringUtils.isBlank(pcpDtl.getProvPcpId())) {
				ageValidation = providerValidationLogic.isAgeValidate(member, pcpDtl, rules);
				genderVlidation = providerValidationLogic.isGendrValidate(member, pcpDtl, rules);
				specialityValidation = providerValidationLogic.isSpecialtyValidate(pcpDtl, rules);
				contractValidation = providerValidationLogic.isCntrctValidate(member, pcpDtl, rules);
				rollOver = providerValidationLogic.isRollOver(member, pcpDtl, rules);
				drivingValidation = providerValidationLogic.isDistanceValidate(pcpDtl, rules);

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
	
	@Bean
	public ProviderValidationLogic providerValidationLogic(){
		return new ProviderValidationLogic();
	}

}
