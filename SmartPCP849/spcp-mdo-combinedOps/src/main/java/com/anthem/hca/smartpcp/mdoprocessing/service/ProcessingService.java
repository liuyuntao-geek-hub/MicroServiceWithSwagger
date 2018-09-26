
package com.anthem.hca.smartpcp.mdoprocessing.service;

import java.util.List;

import org.slf4j. Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.service.MDOPoolService;
import com.anthem.hca.smartpcp.mdo.pool.validator.DateValidator;
import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;



/**
 * @author AF53723 Calls method to get PCP list from MDO Pooling mvc Calls
 *         method to validate payload and get outputpayload.
 * 
 */

@Service
public class ProcessingService {

	private static final Logger logger = LoggerFactory.getLogger(ProcessingService.class);

	@Autowired
	private RestClientService clientService;

	@Autowired
	private ProcessingHelper helper;

	@Autowired
	private DateValidator dateValidator;
	
	@Autowired
	MDOPoolService mdoPoolService;

	/**
	 * @param member
	 * @return MDOProcessingOutputPayload
	 */
	public MDOProcessingOutputPayload getPCP(Member member) {

		MDOProcessingOutputPayload outputPayload = null;

		try {
			long processingTime = System.currentTimeMillis();
			logger.info("In ProcessingService, Forming MDO Build Pool payload to get list of valid providers{}","");
			if (null != member) {
				if (dateValidator.checkFuture(member.getMemberDob())) {
					return helper.createErrorPayload(ResponseCodes.JSON_VALIDATION_ERROR,
							"memberDOB should not be future date");
				}
				long time = System.currentTimeMillis();
				MDOPoolingOutputPayload mdoPoolingPayload = mdoPoolService.getPool(member);
				time = System.currentTimeMillis() - time;
		        logger.info("MDO Pooling processing time: {} ms ",  time);

				if (null != mdoPoolingPayload && mdoPoolingPayload.getResponseCode().equals(ResponseCodes.SUCCESS)) {

					if (mdoPoolingPayload.isDummyFlag() && null != mdoPoolingPayload.getPcps()
							&& mdoPoolingPayload.getPcps().size() == 1) {
						outputPayload = helper.createDummyPayload(mdoPoolingPayload.getPcps().get(0).getProvPcpId());
					} else if (null != mdoPoolingPayload.getPcps() && !mdoPoolingPayload.getPcps().isEmpty()) {
						logger.info("Valid List of PCP's obtained from MDO Build Pool {}"
								, mdoPoolingPayload.getPcps().size());
						outputPayload = getValidPCP(member, mdoPoolingPayload.getPcps());
						processingTime = System.currentTimeMillis() - processingTime;
				        logger.info("MDO processing time: {} ms ",  processingTime);
					}
				} else if (null != mdoPoolingPayload) {
					outputPayload = helper.createErrorPayload(mdoPoolingPayload.getResponseCode(),
							mdoPoolingPayload.getResponseMessage());
				} else {
					outputPayload = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS,
							Constants.EXCEPTION_OCCURED);
				}
			}
		} catch (Exception ex) {
			outputPayload = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, ex.getMessage());
		}

		return outputPayload;
	}

	private MDOProcessingOutputPayload getValidPCP(Member member, List<PCP> pcpList) throws JsonProcessingException {

		MDOProcessingOutputPayload output;
		DroolsInputPayload droolsInputPayload = new DroolsInputPayload();
		droolsInputPayload.setMember(member);
		long time = System.currentTimeMillis();
		JsonNode droolsScoringRules = clientService.getRules(droolsInputPayload);
		time = System.currentTimeMillis() - time;
        logger.info("Drools processing time: {} ms ",  time);
		String responseCode = droolsScoringRules.get(Constants.RESPONSE_CODE).asText();
		String responseMessage = droolsScoringRules.get(Constants.RESPONSE_MESSAGE).asText();
		JsonNode scoringRules = droolsScoringRules.get(Constants.RULES);
		if (ResponseCodes.SUCCESS.equalsIgnoreCase(responseCode) && null != scoringRules) {
			output = helper.getAssignedPCP(scoringRules, member, pcpList);
		} else if (null == scoringRules) {
			output = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, Constants.INVALID_RULES);
		} else {
			output = helper.createErrorPayload(responseCode, responseMessage);
		}
		return output;
	}
	
	@Bean
	public DateValidator dateValidator(){
		return new DateValidator();
	}

}
