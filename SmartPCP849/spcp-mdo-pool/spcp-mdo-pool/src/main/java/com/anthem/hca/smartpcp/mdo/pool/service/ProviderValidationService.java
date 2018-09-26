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
import com.anthem.hca.smartpcp.mdo.pool.model.OutputPayload;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderValidationService.class);

	@Autowired
	private MDOPoolService mdoPoolService;

	

	public OutputPayload getPCPValidation(Member member, JsonNode rules, List<PCP> pcpList, int poolSize,
			String dummyPCP,boolean specialityFlag) {

		OutputPayload outputPayload = new OutputPayload();
		ProviderValidationHelper providerValidationHelper = null;
		List<PCP> validPCPPool = null;
		providerValidationHelper = new ProviderValidationHelper();
		long prov5 = System.nanoTime();
		validPCPPool = providerValidationHelper.getPCPListMDO(member, pcpList, rules, poolSize);
		long prov6 = System.nanoTime();
		LOGGER.debug("Time taken for validating  providers {} ms",(prov6 - prov5) / 1000000);
		if (null == validPCPPool || (validPCPPool.isEmpty() && specialityFlag)) {
			outputPayload = mdoPoolService.createDummyProvider(dummyPCP);
		}
		if (!outputPayload.isDummyFlag()) {
			outputPayload.setPcps(validPCPPool);
			outputPayload.setResponseCode(ResponseCodes.SUCCESS);
			outputPayload.setResponseMessage(MDOPoolConstants.SUCCESS);
		}
		return outputPayload;
	}

}
