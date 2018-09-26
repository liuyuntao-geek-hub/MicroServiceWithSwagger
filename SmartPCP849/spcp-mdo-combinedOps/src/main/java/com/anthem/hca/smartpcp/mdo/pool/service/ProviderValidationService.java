/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author AF74173
 *
 *         Calls Provider validation service with all the providers filtered in
 *         batches.
 *
 */
@Service
public class ProviderValidationService {

	private static final Logger logger = LoggerFactory.getLogger(ProviderValidationService.class);

	@Autowired
	private MDOPoolService mdoPoolService;

	public MDOPoolingOutputPayload getPCPValidation(Member member, JsonNode rules, List<PCP> pcpList, int poolSize,
			String dummyPCP) {

		logger.info("Pool of pcp's to be validated: {} ", pcpList.size());

		MDOPoolingOutputPayload outputPayload = new MDOPoolingOutputPayload();
		ProviderValidationHelper providerValidationHelper = null;
		List<PCP> validPCPPool = null;
		providerValidationHelper = new ProviderValidationHelper();
		long prov5 = System.nanoTime();
		validPCPPool = providerValidationHelper.getPCPListMDO(member, pcpList, rules, poolSize);
		long prov6 = System.nanoTime();
		logger.info("Time taken for validating  providers {} ms",(prov6 - prov5) / 1000000);
		if (null == validPCPPool ) {
			return formErrorOutputPayload();
		} else if (validPCPPool.isEmpty()){
			outputPayload = mdoPoolService.createDummyProvider(dummyPCP);
			outputPayload.setResponseMessage(MDOPoolConstants.PROVIDER_DUMMY_MESSAGE);
		}
		if (!outputPayload.isDummyFlag()) {
			outputPayload.setPcps(validPCPPool);
			outputPayload.setResponseCode(ResponseCodes.SUCCESS);
			outputPayload.setResponseMessage(MDOPoolConstants.SUCCESS);
		}
		return outputPayload;
	}

	private MDOPoolingOutputPayload formErrorOutputPayload() {

		MDOPoolingOutputPayload outputPayload = new MDOPoolingOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(MDOPoolConstants.INVALID_VALIDATION);
		return outputPayload;

	}

}
