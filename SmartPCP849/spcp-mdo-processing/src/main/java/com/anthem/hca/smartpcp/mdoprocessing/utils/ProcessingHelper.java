/**
 * @author AF53723 Helper Service which validates payload from MDO Pooling,
 *         checks for dummy PCP and sets values to output payload Creates
 *         transaction payload.
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
import com.anthem.hca.smartpcp.mdoprocessing.model.PCP;
import com.anthem.hca.smartpcp.mdoprocessing.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdoprocessing.service.AsyncClientService;
import com.anthem.hca.smartpcp.mdoprocessing.service.RestClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class ProcessingHelper {

	private static final Logger  LOGGER = LoggerFactory.getLogger(ProcessingHelper.class);

	@Autowired
	private RestClientService restService;
	
	@Autowired
	private Tracer tracer;
	
	@Autowired
	private AsyncClientService asyncService;
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	
	public MDOProcessingOutputPayload getAssignedPCP(JsonNode scoringRules, Member member, List<PCP> pcpList) throws JsonProcessingException{
		
		MDOProcessingOutputPayload outputPayload ;
		long time = System.currentTimeMillis();
		MDOScoringOutputPayload scoringOutputPayload = restService.getPCPafterScoring(createScoringInput(scoringRules, member, pcpList));
		time = System.currentTimeMillis() - time;
        LOGGER.debug("MDO Scoring processing time: {} ms ",  time);
		if(null != scoringOutputPayload && ResponseCodes.SUCCESS.equalsIgnoreCase(scoringOutputPayload.getResponseCode())){
			outputPayload = createSuccessPayload(scoringOutputPayload.getProvPcpId(), scoringOutputPayload.getRgnlNtwkId(), scoringOutputPayload.getDrivingDistance(), scoringOutputPayload.getMdoScore(), scoringOutputPayload.getPcpRankgId());
			LOGGER.info("MDO Score PCP initial={} final=[PCP={}, drivingDistance={}, mdoScore={}, rgnlNtwkId={}, pcpRank={}]", pcpList.size(), scoringOutputPayload.getProvPcpId(), scoringOutputPayload.getDrivingDistance(), scoringOutputPayload.getMdoScore(),scoringOutputPayload.getRgnlNtwkId(),scoringOutputPayload.getPcpRankgId());
		}else if(null != scoringOutputPayload){
			outputPayload = createErrorPayload(scoringOutputPayload.getResponseCode(), scoringOutputPayload.getResponseMessage());
		}else{
			outputPayload = createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, ErrorMessages.INTERNAL_PROCS_ERR);
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
		transactionPayload.setServiceName(applicationName);
		if (mdoProcessingPayload.getResponseCode().equals(ResponseCodes.SUCCESS)) {
			transactionPayload.setOperationStatus(Constants.SUCCESS_VAL);
		} else {
			transactionPayload.setOperationStatus(Constants.FAILURE_VAL);
		}
		if (mdoProcessingPayload.getPcpId() != null) {
			transactionPayload.setOperationOutput("PCP id: " + mdoProcessingPayload.getPcpId());
		} else {
			transactionPayload.setOperationOutput("PCP id: " + null);
		}
		transactionPayload.setResponseCode(mdoProcessingPayload.getResponseCode());
		transactionPayload.setResponseMessage(mdoProcessingPayload.getResponseMessage());

		return transactionPayload;
	}

	
	public MDOProcessingOutputPayload createErrorPayload(String responseCode,String responseMessage){
		
		LOGGER.debug	("Forming output payload for error scenario with exception message : {}",responseMessage);
		MDOProcessingOutputPayload output = new MDOProcessingOutputPayload();
		output.setResponseCode(responseCode);
		output.setResponseMessage(responseMessage);
		return output;
	}
	
	public MDOProcessingOutputPayload createDummyPayload(String assignedPcp){
		
		LOGGER.debug("Forming output payload for Dummy provider {}","");
		MDOProcessingOutputPayload output = new MDOProcessingOutputPayload();
		output.setPcpId(assignedPcp);
		output.setDummyFlag(true);
		output.setResponseCode(ResponseCodes.SUCCESS);
		output.setResponseMessage("DUMMY_PCP_ASSIGNED");
		return output;
	}
	
	public MDOProcessingOutputPayload createSuccessPayload(String assignedPcp,String networkId,Double drivingDistance,int score,String pcpRankgId){
		
		LOGGER.debug("Forming output payload for provider with pcpId: {} with network id as {}", assignedPcp ,networkId);
		MDOProcessingOutputPayload output = new MDOProcessingOutputPayload();
		output.setPcpId(assignedPcp);
		output.setPcpNtwrkId(networkId);
		output.setDrivingDistance(drivingDistance);
		output.setPcpRank(Integer.parseInt(pcpRankgId));
		output.setDummyFlag(false);
		output.setMdoScore(score);
		output.setResponseCode(ResponseCodes.SUCCESS);
		output.setResponseMessage(Constants.PCP_ASSIGNED);
		return output;
	}
	
	
}
