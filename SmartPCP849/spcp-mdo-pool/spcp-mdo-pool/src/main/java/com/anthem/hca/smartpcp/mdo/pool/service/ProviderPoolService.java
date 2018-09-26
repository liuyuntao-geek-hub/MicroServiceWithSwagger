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
import com.anthem.hca.smartpcp.mdo.pool.model.ProviderPoolOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;

@Service
public class ProviderPoolService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderPoolService.class);

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
	public ProviderPoolOutputPayload poolBuilder(int maxRadiusToPool, Member member) {
		List<PCP> validatedProviderList = null;
		List<PCP> pcpsFromDBasList = null;
		List<String> memberNtwrkIds = new ArrayList<>();
		ProviderPoolOutputPayload providerPoolOutput = new ProviderPoolOutputPayload();
		if (null != member.getMemberNetworkId() && !member.getMemberNetworkId().isEmpty()) {
			memberNtwrkIds.addAll(member.getMemberNetworkId());
		}
		if (null != member.getMemberContractCode() && !member.getMemberContractCode().isEmpty()) {
			memberNtwrkIds.addAll(member.getMemberContractCode());
		}
		LOGGER.debug("Connecting data base to fetch provider data from Target table {} ", "");
		long prov1 = System.nanoTime();

		if (!memberNtwrkIds.isEmpty()) {
			pcpsFromDBasList = dbService.getPCPDetails(memberNtwrkIds, member);
			long prov2 = System.nanoTime();
			LOGGER.debug("Time taken to fetch data from DB {} ms", (prov2 - prov1) / 1000000);
			if (null != pcpsFromDBasList && !pcpsFromDBasList.isEmpty()) {
				List<PCP> pcpListFilteredOnAD = mdoPoolUtils.findAerialDistance(member, pcpsFromDBasList,
						maxRadiusToPool);
				providerPoolOutput.setPcpsFromDB(pcpsFromDBasList.size());
				validatedProviderList = pcpListFilteredOnAD.stream().filter(pcp -> {
					boolean flag = false;
					if (!StringUtils.isBlank(pcp.getSpcltyDesc()) && null != pcp.getPcpLang()
							&& !pcp.getPcpLang().isEmpty() && !StringUtils.isBlank(pcp.getRgnlNtwkId())
							&& null != pcp.getGrpgRltdPadrsTrmntnDt()
							&& !StringUtils.isBlank(pcp.getAccNewPatientFlag())
							&& MDOPoolConstants.ZERO <= pcp.getCurntMbrCnt()
							&& MDOPoolConstants.ZERO <= pcp.getMaxMbrCnt()) {
						flag = true;
					}
					return flag;
				}).collect(Collectors.toList());
				LOGGER.info("MDO pool-[initial={}, mileRange={}, final={}]", pcpsFromDBasList.size(), maxRadiusToPool,
						validatedProviderList.size());

			} else {
				validatedProviderList = new ArrayList<>();
				LOGGER.debug("No valid PCPs are returned from target table {}", "");
			}
		}
		providerPoolOutput.setPcps(validatedProviderList);
		return providerPoolOutput;
	}
	
	@Bean
	public MDOPoolUtils getMDOPoolUtils() {
		return new MDOPoolUtils();
	}

}
