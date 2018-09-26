package com.anthem.hca.smartpcp.mdoprocessing.service;

import org.slf4j. Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.mdo.pool.constants.MDOPoolConstants;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.RulesInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * @author AF53723 Sends request to MDOPooling Receives response which contains
 *         PCP list.
 *
 */
@Service
@RefreshScope
public class RestClientService {

	private static final Logger logger = LoggerFactory.getLogger(RestClientService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Value("${spcp.mdo.pool.url}")
	private String mdoPoolingUrl;

	@Value("${spcp.drools.url}")
	private String droolsRuleEngineUrl;

	@Value("${spcp.mdo.scoring.url}")
	private String mdoScoringUrl;
	
	@Value("${spcp.drools.mdo.url}")
	private String mdoPoolingRulesDroolsEngineUrl;

	@Value("${spcp.drools.provider.validation.url}")
	private String providerRulesDroolsUrl;

	private HttpHeaders headers;

	public RestClientService() {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.headers = httpHeaders;
	}

	/**
	 * @param member
	 * @return MDOPoolingOutputPayload
	 * @throws JsonProcessingException
	 */
	@HystrixCommand(fallbackMethod = "fallbackGetPcpList", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public MDOPoolingOutputPayload getPCPList(Member member) throws JsonProcessingException {	
		logger.info("Calling MDO Build pool service to fetch valid pool of PCP's {}"," ");
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(member), headers);
		return restTemplate.postForObject(mdoPoolingUrl, request, MDOPoolingOutputPayload.class);

	}

	public MDOPoolingOutputPayload fallbackGetPcpList(Member member) {

		logger.error("MDO Build pool  is temporarily down for Member with first name: {} " , member.getMemberFirstName());
		MDOPoolingOutputPayload response = new MDOPoolingOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage("MDO Pooling Service is temporarily down. Please try after some time");

		return response;
	}


	@HystrixCommand(fallbackMethod = "fallbackGetRules", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public JsonNode getRules(DroolsInputPayload droolsInputPayload) throws JsonProcessingException { 
		logger.info("Calling Drools Engine to fetch scoring rules {}", "");
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(droolsInputPayload), headers);
		return restTemplate.postForObject(droolsRuleEngineUrl, request, JsonNode.class);
	}

	public JsonNode fallbackGetRules(DroolsInputPayload droolsInputPayload)
			{
		logger.error("Drools Rule Engine is temporarily down with input payload {}" , droolsInputPayload);
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.createObjectNode().put(Constants.RESPONSE_CODE, ResponseCodes.SERVICE_NOT_AVAILABLE)
				.put(Constants.RESPONSE_MESSAGE, Constants.SERVICE_DOWN);
	}

	@HystrixCommand(fallbackMethod = "fallbackGetPCPafterScoring", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public MDOScoringOutputPayload getPCPafterScoring(JsonNode mdoScoringInput) throws JsonProcessingException {
		logger.info("Calling MDO Scoring to retrieve the assigned PCP {}","");
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(mdoScoringInput), headers);
		return restTemplate.postForObject(mdoScoringUrl, request, MDOScoringOutputPayload.class);
	}

	public MDOScoringOutputPayload fallbackGetPCPafterScoring(JsonNode mdoScoringInput)
			 {

		logger.error("MDO Scoring Service is temporarily down with input payload {}",mdoScoringInput);
		MDOScoringOutputPayload response = new MDOScoringOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage("MDO Scoring Service is temporarily down. Please try after some time");
		return response;
	}
	
	@HystrixCommand(fallbackMethod = "getFallBackPoolInformation", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "6000") })
	public MDOPoolingRulesOutputPayload getPoolInformation(RulesInputPayload payload) throws JsonProcessingException {
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
		logger.info("In RestClientService, Calling Drools Rule Engine for mdo pooling rules {} ", "");
		return restTemplate.postForObject(mdoPoolingRulesDroolsEngineUrl, request, MDOPoolingRulesOutputPayload.class);
	}

	/**
	 * Circuit Breaker fall back method for getPoolInformation.
	 * 
	 * @param payload
	 * @returnRulesOutputPayload
	 * @throws JsonProcessingException
	 */
	public MDOPoolingRulesOutputPayload getFallBackPoolInformation(RulesInputPayload payload) {
		logger.error(" Drools Rule Enginee is temporarily down while fetching MDO Pool rules with input payload {} ",
				"");
		MDOPoolingRulesOutputPayload response = new MDOPoolingRulesOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage(MDOPoolConstants.SERVICE_DOWN);
		return response;
	}

	/**
	 * @param payload
	 * @return RulesOutputPayload
	 * @throws JsonProcessingException
	 */
	@HystrixCommand(fallbackMethod = "getFallBackProviderValidationRules", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
	public JsonNode getProviderValidationRules(RulesInputPayload payload) throws JsonProcessingException {
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
		logger.info("In RestClientService, calling Drools Rule Engine for  provider validation rules{}", "");
		return restTemplate.postForObject(providerRulesDroolsUrl, request, JsonNode.class);
	}

	/**
	 * Circuit Breaker fall back method for getPoolInformation.
	 * 
	 * @param payload
	 * @returnRulesOutputPayload
	 * @throws JsonProcessingException
	 */
	public JsonNode getFallBackProviderValidationRules(RulesInputPayload payload) {
		logger.error(" Drools Rule Engine is temporarily down to get ProviderValidation rules with input payload {}",
				"");
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.createObjectNode().put(MDOPoolConstants.RESPONSE_CODE, ResponseCodes.SERVICE_NOT_AVAILABLE)
				.put(MDOPoolConstants.RESPONSE_MESSAGE, MDOPoolConstants.SERVICE_DOWN);
	}
}
