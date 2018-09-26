/**
* Copyright © 2018 Anthem, Inc.
* 
* Connects to Splice machine to fetch all the provider information
* based on the network of the member and the aerial distance limit
* returned by drools.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingProviderValidationRules;
import com.anthem.hca.smartpcp.mdo.pool.repository.PoolingRepo;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.util.DateUtils;

@Service
public class ProviderPoolService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderPoolService.class);

	@Autowired
	private PoolingRepo providerInfoRepo;
	
	/**
	 * @param maxRadiusToPool
	 * @param member
	 * @return List<PCP>
	 * 
	 *         This method is to form pool of providers based on the pool six
	 *         and aerial distance
	 * 
	 */
	public List<ScoringProvider> poolBuilder(Member member,MDOPoolingProviderValidationRules providerValidationRules) {
		
		List<ScoringProvider> validatedProviderList = new ArrayList<>();
		List<String> memberNtwrkIds = new ArrayList<>();
		if (null != member.getMemberNetworkId() && !member.getMemberNetworkId().isEmpty()) {
			memberNtwrkIds.addAll(member.getMemberNetworkId());
		}
		if (null != member.getMemberContractCode() && !member.getMemberContractCode().isEmpty()) {
			memberNtwrkIds.addAll(member.getMemberContractCode());
		}
		if (!memberNtwrkIds.isEmpty()) {
			if (DateUtils.isAgeUnder18(member.getMemberDob())) {
				LOGGER.debug("Fetching valid Pediatric PCP's {}","");
				validatedProviderList = providerInfoRepo.getPCPDtlsAsList(memberNtwrkIds,member,providerValidationRules,Constants.EMPTY_STRING,Constants.PEDIATRICS_SPCLTY,Constants.EMPTY_STRING);
				if(null == validatedProviderList || validatedProviderList.isEmpty()){
					LOGGER.debug("No valid Pediatric PCP found,fetching other valid  PCP's {} ","");
					validatedProviderList = providerInfoRepo.getPCPDtlsAsList(memberNtwrkIds,member,providerValidationRules,Constants.PEDIATRICS_SPCLTY,Constants.EMPTY_STRING,Constants.PEDIATRICS_SPCLTY);
				}
			}else{
				validatedProviderList = providerInfoRepo.getPCPDtlsAsList(memberNtwrkIds,member,providerValidationRules,Constants.EMPTY_STRING,Constants.EMPTY_STRING,Constants.PEDIATRICS_SPCLTY);
			}
		}
		return validatedProviderList;
	}
	
	@Bean
	public PoolingRepo getProviderInfoRepo() {
		return new PoolingRepo();
	}

}
