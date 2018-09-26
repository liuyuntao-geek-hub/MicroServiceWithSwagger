/**
 * @author AF53723 Calls method to get PCP list from MDO Pooling mvc Calls
 *         method to validate payload and get outputpayload.
 * 
 */
package com.anthem.hca.smartpcp.mdoprocessing.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
import com.anthem.hca.smartpcp.mdoprocessing.model.PCP;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ErrorMessages;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ProcessingHelper;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;





@Service
public class ProcessingService {

	private static final Logger  LOGGER = LoggerFactory.getLogger(ProcessingService.class);

	@Autowired
	private RestClientService clientService;

	@Autowired
	private ProcessingHelper helper;

	/**
	 * @param member
	 * @return MDOProcessingOutputPayload
	 */
	public MDOProcessingOutputPayload getPCP(Member member) {

		MDOProcessingOutputPayload outputPayload = null;

		try {
			long processingTime = System.currentTimeMillis();
			LOGGER.debug("In ProcessingService, Forming MDO Build Pool payload to get list of valid providers{}","");
				long time = System.currentTimeMillis();
				MDOPoolingOutputPayload mdoPoolingPayload = clientService.getPCPList(member);
				time = System.currentTimeMillis() - time;
		        LOGGER.debug("MDO Pooling processing time: {} ms ",  time);

				if (null != mdoPoolingPayload && mdoPoolingPayload.getResponseCode().equals(ResponseCodes.SUCCESS)) {
					if (mdoPoolingPayload.isDummyFlag() && null != mdoPoolingPayload.getPcps()
							&& mdoPoolingPayload.getPcps().size() == 1) {
						outputPayload = helper.createDummyPayload(mdoPoolingPayload.getPcps().get(0).getProvPcpId());
						LOGGER.info("MDO Pool PCP final={}, dummyFlag={}"
								, mdoPoolingPayload.getPcps().size(),mdoPoolingPayload.isDummyFlag() );
					} else if (null != mdoPoolingPayload.getPcps() && !mdoPoolingPayload.getPcps().isEmpty()) {
						LOGGER.info("MDO Pool PCP final={}, dummyFlag={}"
								, mdoPoolingPayload.getPcps().size(),mdoPoolingPayload.isDummyFlag() );
						outputPayload = getValidPCP(member, mdoPoolingPayload.getPcps());
						processingTime = System.currentTimeMillis() - processingTime;
				        LOGGER.debug("MDO processing time: {} ms ",  processingTime);
					}
				} else if (null != mdoPoolingPayload) {
					outputPayload = helper.createErrorPayload(mdoPoolingPayload.getResponseCode(),
							mdoPoolingPayload.getResponseMessage());
				} else {
					outputPayload = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS,
							ErrorMessages.INTERNAL_PROCS_ERR);
				}
		} catch (Exception exception) {
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			outputPayload = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, exception.getClass().getSimpleName());
		}

		return outputPayload;
	}

	private MDOProcessingOutputPayload getValidPCP(Member member, List<PCP> pcpList) throws JsonProcessingException {

		MDOProcessingOutputPayload output;
		DroolsInputPayload droolsInputPayload = new DroolsInputPayload();
		
		BeanUtils.copyProperties(member,droolsInputPayload);

		long time = System.currentTimeMillis();
		JsonNode droolsScoringRules = clientService.getRules(droolsInputPayload);
		time = System.currentTimeMillis() - time;
        LOGGER.debug("Drools processing time: {} ms ",  time);
		String responseCode = droolsScoringRules.get(Constants.RESPONSE_CODE).asText();
		String responseMessage = droolsScoringRules.get(Constants.RESPONSE_MESSAGE).asText();
		JsonNode scoringRules = droolsScoringRules.get(Constants.RULES);
		if (ResponseCodes.SUCCESS.equalsIgnoreCase(responseCode) && null != scoringRules) {
			output = helper.getAssignedPCP(scoringRules, member, pcpList);
		} else if (null == scoringRules) {
			output = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, ErrorMessages.INVALID_DROOLS_RESPONSE);
		} else {
			output = helper.createErrorPayload(responseCode, responseMessage);
		}
		return output;
	}
}
