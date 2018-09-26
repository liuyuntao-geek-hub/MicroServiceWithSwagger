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
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;

@Service
public class ProviderPoolService {

	private static final Logger logger = LoggerFactory.getLogger(ProviderPoolService.class);

	@Autowired
	private MDOPoolUtils mdoPoolUtils;

	@Autowired
	private DBConnectivityService dbService;

	/**
	 * @param maxRadiusToPool
	 * @param member
	 * @return List<PCP>
	 * 
	 *         This method is to form pool of providers based on the pool six
	 *         and aerial distance
	 * 
	 */
	public List<PCP> poolBuilder(int maxRadiusToPool, Member member) {
		List<PCP> validatedProviderList = null;
		logger.info("Forming pool of providers with in the given aerial distance{}", "");
		List<String> memberNtwrkIds = null;
		if (null != member.getMemberNetworkId() && !member.getMemberNetworkId().isEmpty()) {
			memberNtwrkIds = member.getMemberNetworkId();
		} else if (null != member.getMemberContractCode() && !member.getMemberContractCode().isEmpty()) {
			memberNtwrkIds = member.getMemberContractCode();
		}
		logger.info("Connecting data base to fetch provider data from Target table {} ", "");
		long prov1 = System.nanoTime();
		
		if(null != memberNtwrkIds && !memberNtwrkIds.isEmpty()) {
			List<PCP> pcpDtlsList = dbService.getPCPDetails(memberNtwrkIds,member);
			long prov2 = System.nanoTime();
			logger.info("Time taken to fetch data from DB {} ms",(prov2 - prov1) / 1000000);
			if (!pcpDtlsList.isEmpty()) {
				validatedProviderList = mdoPoolUtils.findArealDistance(member, pcpDtlsList, maxRadiusToPool);
				validatedProviderList = validatedProviderList.stream().filter(pcp -> {
					boolean flag = false;
					if (!StringUtils.isBlank(pcp.getSpcltyDesc()) && null != pcp.getPcpLang() && !pcp.getPcpLang().isEmpty()
							&& !StringUtils.isBlank(pcp.getRgnlNtwkId()) && null != pcp.getGrpgRltdPadrsTrmntnDt()
							&& !StringUtils.isBlank(pcp.getAccNewPatientFlag())
							&& MDOPoolConstants.ZERO <= pcp.getCurntMbrCnt() && MDOPoolConstants.ZERO <= pcp.getMaxMbrCnt()
							&& MDOPoolConstants.ZERO <= pcp.getTierLvl()) {
						flag = true;
					}
					return flag;
				}).collect(Collectors.toList());
				logger.info(
						"Validations are added on providers for skipping null values,after validation providers size is {} ",
						validatedProviderList.size());

			} else {
				validatedProviderList = new ArrayList<>();
				logger.info("No valid PCPs are returned from target table {}", "");
			}
			return validatedProviderList;
		}
		return new ArrayList<>();
	}

	@Bean
	public MDOPoolUtils getMDOPoolUtils() {
		return new MDOPoolUtils();
	}

}
