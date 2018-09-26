package com.anthem.hca.smartpcp.mdoscoring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.service.AsyncClientService;
import com.anthem.hca.smartpcp.mdoscoring.helper.MDOScoreHelper;
import com.anthem.hca.smartpcp.mdoscoring.utility.Constant;
import com.anthem.hca.smartpcp.mdoscoring.utility.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.MdoScoringServiceParseException;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;

@Service
public class MDOScoreService {

	private static final Logger logger = LoggerFactory.getLogger(MDOScoreService.class);

	@Autowired
	private AsyncClientService asyncClientService;

	@Autowired
	private MDOScoreHelper mdoHelper;

	@Autowired
	private Tracer tracer;

	public MDOScoringOutputPayload getFinalPCP(InputPayloadInfo inputPayloadInfos) {
		MDOScoringOutputPayload outputPayload = new MDOScoringOutputPayload();
		try {
			if (null != inputPayloadInfos.getMember() && null != inputPayloadInfos.getPcp() 
					&& null != inputPayloadInfos.getRules() ) {
				long mdoScoreBegin = System.nanoTime();
				logger.info("Filtering out the PCP with invalid data{}"," ");

				List<PCP> validatedProviderList = inputPayloadInfos.getPcp().stream().filter(pcp -> {
					boolean flag = false;
					if (null != pcp.getLatdCordntNbr()  && null != pcp.getLngtdCordntNbr()  && !StringUtils.isBlank(pcp.getProvPcpId())
							&& !StringUtils.isBlank(pcp.getRgnlNtwkId()) && null != pcp.getAerialDistance() && null !=pcp.getMaxMbrCnt()) {
						flag = true;
					}
					return flag;
				}).collect(Collectors.toList());
					if (!validatedProviderList.isEmpty()) {
	
						PCP finalPcp = mdoHelper.getHighestScorePCP(validatedProviderList, inputPayloadInfos.getRules(),
								inputPayloadInfos.getMember());
						long mdoScoreEnd = System.nanoTime();
						long timeTaken=(mdoScoreEnd - mdoScoreBegin) / 1000000;
			            logger.info("Time Taken to fetch best PCP is {}{} ",timeTaken,"ms");

						if (null != finalPcp ) {
	
							outputPayload.setDrivingDistance(finalPcp.getDrivingDistance());
							outputPayload.setMdoScore(finalPcp.getMdoScore());
							outputPayload.setProvPcpId(finalPcp.getProvPcpId());
							outputPayload.setRgnlNtwkId(finalPcp.getRgnlNtwkId());
							outputPayload.setResponseCode(ResponseCodes.SUCCESS);
							outputPayload.setResponseMessage(Constant.SUCESS_VALUE);
						} else {
							outputPayload.setDrivingDistance(0.0);
							outputPayload.setMdoScore(0);
							outputPayload.setProvPcpId(null);
							outputPayload.setRgnlNtwkId(null);
							outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
							outputPayload.setResponseMessage(Constant.INPUT_ERROR_MSG);
						}
					}else {
						throw new MdoScoringServiceParseException(Constant.INPUT_ERROR_MSG);
					}
			} else {
				throw new MdoScoringServiceParseException(Constant.INPUT_ERROR_MSG);
			}
		} catch (Exception exception) {
			logger.error("Exception Occured: {}", exception.getCause());
			outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			outputPayload.setResponseMessage(exception.getMessage());
		}
		insertOperationFlow(outputPayload);
		return outputPayload;
	}

	private void insertOperationFlow(MDOScoringOutputPayload outputPayload) {
		try {
			TransactionFlowPayload payload = new TransactionFlowPayload();
			payload.setTraceId(tracer.getCurrentSpan().traceIdString());
			payload.setServiceName(Constant.SERVICE_NAME);
			payload.setResponseCode(Integer.parseInt(outputPayload.getResponseCode()));
			payload.setResponseMessage(outputPayload.getResponseMessage());

			if (Constant.SUCCESS_CODE.equals(outputPayload.getResponseCode())) {
				payload.setOperationStatus(Constant.SUCESS_VALUE);
				payload.setOperationOutput("Best PCP id is " + outputPayload.getProvPcpId()
						+ " and scores of provider is " + outputPayload.getMdoScore());

			} else {
				payload.setOperationStatus(Constant.FAILURE_VALUE);
			}
			asyncClientService.insertOperationFlow(payload);
		} catch (Exception exception) {
			logger.error("Error occured while inserting into transaction table {}" ,exception.getMessage());
		}
	}

}
