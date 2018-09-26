/**
 * Copyright © 2018 Anthem, Inc.
 * 
 *  MDOPoolService class handles all the logic needed to build the pcp pool. It
 * connects to Drools Engine and gets the pool size and aerial distance limit.
 * Prepares the pool of pcp's based on the above inputs and sends it to
 * ProviderValidation. The valid pcp's will be returned from Provider Validation
 * and the same will be returned back as response.
 * 
 * @author AF74173
 */

package com.anthem.hca.smartpcp.mdo.pool.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.mdoscoring.helper.MDOScoreHelper;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;


@Service
public class MDOPoolService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MDOPoolService.class);

	@Autowired
	private ProviderPoolService providerPoolService;

	@Autowired
	private MDOScoreHelper mdoHelper;

	@Autowired
	private MDOPoolingProviderValidationService msdoProviderValidationService;
	
	@Autowired
	private OperationAuditFlowService operationAuditFlowService;

	@Autowired
	private MDOScoringService scoringService;

	public ScoringProvider getValidPCP(Member member) throws Exception {

		ScoringProvider pcp = null;
		List<ScoringProvider> validPool =null;
		MDOPoolingProviderValidationRules mdoProvRules = msdoProviderValidationService.getRules(member);

		if (null != mdoProvRules) {
			String dummyPcp = mdoProvRules.getDummyProviderId();
			 validPool = providerPoolService.poolBuilder(member, mdoProvRules);
			LOGGER.info("Valid PCP Pool={}, mileRange={}", validPool.size(), mdoProvRules.getDistance());

			if (validPool.isEmpty() && StringUtils.isNotBlank(dummyPcp)) {
				pcp = createDummyProvider(dummyPcp);
			} else if (validPool.isEmpty() && StringUtils.isBlank(dummyPcp)) {
				throw new DroolsParseException(ErrorMessages.INVALID_MBR_LOB);
			} else {
				pcp = getBestPCP(member, validPool);
			}
		
		}
		operationAuditFlowService.insertOperationFlowMDO((null !=validPool) ?validPool.size():0, pcp);
		return pcp;
	}

	private ScoringProvider getBestPCP(Member member, List<ScoringProvider> pcpList)
			throws Exception {

		ScoringProvider bestPcp = null;
		MDOScoringRules scoringRules = scoringService.getRules(member);
		if (null != scoringRules) {
			bestPcp = mdoHelper.getBestPCP(pcpList, scoringRules, member, new PCPAssignmentFlow());
		}
		return bestPcp;
	}

	private ScoringProvider createDummyProvider(String dummyPCP) {

		LOGGER.debug("Forming output payload for Dummy provider {}", "");
		ScoringProvider dummy = new ScoringProvider();
		dummy.setProvPcpId(dummyPCP);
		dummy.setDummyFlag(true);
		return dummy;
	}
}