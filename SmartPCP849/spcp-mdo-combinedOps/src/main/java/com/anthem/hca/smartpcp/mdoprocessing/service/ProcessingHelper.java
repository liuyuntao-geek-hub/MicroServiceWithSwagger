package com.anthem.hca.smartpcp.mdoprocessing.service;

import java.io.IOException;
import java.util.List;

import org.slf4j. Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.service.MDOScoreService;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author AF53723 Helper Service which validates payload from MDO Pooling,
 *         checks for dummy PCP and sets values to output payload Creates
 *         transaction payload.
 *
 */
@Service
public class ProcessingHelper {

	private static final Logger logger = LoggerFactory.getLogger(ProcessingHelper.class);

	@Autowired
	private RestClientService restService;
	
	@Autowired
	private Tracer tracer;
	
	@Autowired
	private AsyncClientService asyncService;
	
	@Autowired
	private MDOScoreService mdoScoreService;
	
	
	
	

	
	public MDOProcessingOutputPayload getAssignedPCP(JsonNode scoringRules, Member member, List<PCP> pcpList) throws JsonProcessingException{
		
		MDOProcessingOutputPayload outputPayload ;
		long time = System.currentTimeMillis();
		InputPayloadInfo inputPayloadInfo = new InputPayloadInfo();
		inputPayloadInfo.setMember(member);
		inputPayloadInfo.setPcp(pcpList);
		Rules rules = null;
		try {
			rules = new ObjectMapper().readValue(scoringRules.toString(), Rules.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputPayloadInfo.setRules(rules);
		
		MDOScoringOutputPayload scoringOutputPayload = mdoScoreService.getFinalPCP(inputPayloadInfo);
		time = System.currentTimeMillis() - time;
        logger.info("MDO Scoring processing time: {} ms ",  time);
		if(null != scoringOutputPayload && ResponseCodes.SUCCESS.equalsIgnoreCase(scoringOutputPayload.getResponseCode())){
			outputPayload = createSuccessPayload(scoringOutputPayload.getProvPcpId(), scoringOutputPayload.getRgnlNtwkId(), scoringOutputPayload.getDrivingDistance(), scoringOutputPayload.getMdoScore());
			
		}else if(null != scoringOutputPayload){
			outputPayload = createErrorPayload(scoringOutputPayload.getResponseCode(), scoringOutputPayload.getResponseMessage());
		}else{
			outputPayload = createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, Constants.EXCEPTION_OCCURED);
		}
		
		asyncService.insertOperationFlow(createTransactionPayload(outputPayload));
		return outputPayload;
		
	}
	
	public JsonNode createScoringInput(JsonNode scoringRules, Member member, List<PCP> pcpList){
		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode memberData = objMapper.convertValue(member, ObjectNode.class);
		ObjectNode mdoScoringRules = objMapper.convertValue(scoringRules, ObjectNode.class);
		JsonNode scoringInput = objMapper.createObjectNode();

		((ObjectNode) scoringInput).putObject(Constants.MEMBER).setAll(memberData);
		((ObjectNode) scoringInput).putObject(Constants.RULES).setAll(mdoScoringRules);
		ArrayNode providerData = objMapper.valueToTree(pcpList);
		((ObjectNode) scoringInput).putArray(Constants.PCP_INFO).addAll(providerData);
		
		return scoringInput;

	}

	/**
	 * @param mdoProcessingPayload
	 * @return transactionPayload
	 */
	public TransactionFlowPayload createTransactionPayload(MDOProcessingOutputPayload mdoProcessingPayload) {
		TransactionFlowPayload transactionPayload = new TransactionFlowPayload();
		transactionPayload.setTraceId(tracer.getCurrentSpan().traceIdString());
		transactionPayload.setServiceName("spcp-mdo-processing");
		if (mdoProcessingPayload.getResponseCode().equals(ResponseCodes.SUCCESS)) {
			transactionPayload.setOperationStatus("SUCCESS");
		} else {
			transactionPayload.setOperationStatus("FAILURE");
		}
		if (mdoProcessingPayload.getPcpId() != null) {
			transactionPayload.setOperationOutput("PCP id: " + mdoProcessingPayload.getPcpId());
		} else {
			transactionPayload.setOperationOutput("PCP id: " + null);
		}
		transactionPayload.setResponseCode(Integer.parseInt(mdoProcessingPayload.getResponseCode()));
		transactionPayload.setResponseMessage(mdoProcessingPayload.getResponseMessage());

		return transactionPayload;
	}

	
	public MDOProcessingOutputPayload createErrorPayload(String responseCode,String responseMessage){
		
		logger.info("Forming output payload for error scenario with exception message : {}",responseMessage);
		MDOProcessingOutputPayload output = new MDOProcessingOutputPayload();
		output.setResponseCode(responseCode);
		output.setResponseMessage(responseMessage);
		return output;
	}
	
	public MDOProcessingOutputPayload createDummyPayload(String assignedPcp){
		
		logger.info("Forming output payload for Dummy provider {}","");
		MDOProcessingOutputPayload output = new MDOProcessingOutputPayload();
		output.setPcpId(assignedPcp);
		output.setDummyFlag(true);
		output.setResponseCode(ResponseCodes.SUCCESS);
		output.setResponseMessage("Assigned the Dummy PCP");
		return output;
	}
	
	public MDOProcessingOutputPayload createSuccessPayload(String assignedPcp,String networkId,Double drivingDistance,int score){
		
		logger.info("Forming output payload for provider with pcpId: {} with network id as {}", assignedPcp ,networkId);
		MDOProcessingOutputPayload output = new MDOProcessingOutputPayload();
		output.setPcpId(assignedPcp);
		output.setPcpNtwrkId(networkId);
		output.setDrivingDistance(drivingDistance);
		output.setDummyFlag(false);
		output.setMdoScore(score);
		output.setResponseCode(ResponseCodes.SUCCESS);
		output.setResponseMessage(Constants.PCP_ASSIGNED);
		return output;
	}
	
	
}
