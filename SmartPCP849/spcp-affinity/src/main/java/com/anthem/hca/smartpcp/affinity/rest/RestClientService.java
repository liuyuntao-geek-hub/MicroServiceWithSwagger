package com.anthem.hca.smartpcp.affinity.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.affinity.constants.Constants;
import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.BingInputPayload;
import com.anthem.hca.smartpcp.affinity.model.BingOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.affinity.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.affinity.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 				RestClientService contains logic to connect to other services and get the required parameters.
 * 				Contains Circuit Breaker pattern implemented to continue operating when a related service fails, 
 * 				preventing the failure from cascading and giving the failing service time to recover.
 * 
 * @author AF65409 
 */
@Service
@RefreshScope
public class RestClientService {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private OAuthAccessTokenConfig oAuthAccessTokenConfig;

	private HttpHeaders headers;

	@Value("${transaction.service.url}")
	private String transactionServiceUrl;
	@Value("${drools.engine.url}")
	private String droolsEngineUrl;
	@Value("${bing.url}")
	private String bingUrl;
	@Value("${bing.key}")
	private String bingKey;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Default constructor
	 */
	public RestClientService() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.headers = httpHeaders;
	}

	/**
	 * @param payload
	 * @return JsonNode
	 * @throws JsonProcessingException
	 * 
	 *             Default timeout of hystrix is 1 sec. For smooth testing
	 *             changed it to 5sec.
	 * 
	 */
	@HystrixCommand(fallbackMethod = "getFallBackProviderValidRules", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "50000") })
	public JsonNode getProviderValidRules(RulesEngineInputPayload payload) throws JsonProcessingException{

		ResponseEntity<JsonNode> rulesResponse = null;
		try {

			HttpHeaders oauthHeaders = oAuthAccessTokenConfig.getHeaderWithAccessToken();
			oauthHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> rulesRequest = new HttpEntity<>(mapper.writeValueAsString(payload),oauthHeaders);

			logger.debug("Calling Drools Rule Engine {} ", rulesRequest);
			long startFetch = System.nanoTime();

			rulesResponse = restTemplate.exchange(droolsEngineUrl, HttpMethod.POST, rulesRequest , JsonNode.class);

			long endFetch = System.nanoTime();
			double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);

			logger.debug("time for query on Drools Response {} milliseconds", fetchTime);
			logger.debug("Drools Rule Engine Response {} ", rulesResponse);
		} catch(Exception exception) {

			logger.error(ErrorMessages.INVALID_DROOLS_RESP , exception);

			ObjectMapper objMapper = new ObjectMapper();
			ObjectNode responseNode = objMapper.createObjectNode();

			responseNode.put(Constants.RESPONSE_CODE, ResponseCodes.OTHER_EXCEPTIONS);
			responseNode.put(Constants.RESPONSE_MESSAGE, ErrorMessages.INVALID_DROOLS_RESP);
		} finally {
			if (rulesResponse == null) {
				ObjectMapper objMapper = new ObjectMapper();
				ObjectNode responseNode = objMapper.createObjectNode();

				responseNode.put(Constants.RESPONSE_CODE, ResponseCodes.OTHER_EXCEPTIONS);
				responseNode.put(Constants.RESPONSE_MESSAGE, ErrorMessages.INVALID_DROOLS_RESP);

			}
		}
		return rulesResponse.getBody();
	}

	/**
	 * Circuit Breaker fallback method for getInvocationOrder.
	 * 
	 * @param payload
	 * @return JsonNode
	 */
	public JsonNode getFallBackProviderValidRules(RulesEngineInputPayload payload) {

		logger.error(ErrorMessages.DROOLS_DOWN , payload);

		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode responseNode = objMapper.createObjectNode();

		responseNode.put(Constants.RESPONSE_CODE, ResponseCodes.SERVICE_NOT_AVAILABLE);
		responseNode.put(Constants.RESPONSE_MESSAGE, ErrorMessages.DROOLS_DOWN);

		return responseNode;
	}

	/**
	 * @param bingInput
	 * @return BingOutputPayload
	 * @throws JsonProcessingException
	 * 
	 *             Default timeout of Bing is 1 sec. For smooth testing
	 *             changed it to 5sec.
	 * 
	 */
	@HystrixCommand(fallbackMethod = "fallbackGetDistanceMatrix", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
	public BingOutputPayload getDistanceMatrix(BingInputPayload bingInput) {

		BingOutputPayload bingResponse = null;
		try {

			String bingRequest = mapper.writeValueAsString(bingInput);

			logger.debug("Bing Request: {}" , bingRequest);
			logger.debug("Bing URL: {}" , bingUrl);
			logger.debug("Bing Key: {}" , bingKey);

			String finalBingURL = bingUrl + Constants.EQUAL + bingKey;
			logger.debug("Final Bing URL: {}" , finalBingURL);

			HttpEntity<String> request = new HttpEntity<>(bingRequest, headers);
			bingResponse = restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class);

			String bingOutput = mapper.writeValueAsString(bingResponse);
			logger.debug("Bing Response: {}", bingOutput);
		}catch(Exception exception) {

			logger.error(ErrorMessages.BING_SERVICE_ERROR,exception);

			bingResponse = new BingOutputPayload();
			bingResponse.setStatusCode(ResponseCodes.OTHER_EXCEPTIONS);
			bingResponse.setStatusDescription(ErrorMessages.BING_SERVICE_ERROR);
		}

		return bingResponse;
	}

	/**
	 * Circuit Breaker fallback method for getDistanceMatrix.
	 * 
	 * @param bingInput
	 * @return BingOutputPayload
	 */
	public BingOutputPayload fallbackGetDistanceMatrix(BingInputPayload bingInput) {

		logger.error(ErrorMessages.BING_DOWN, bingInput);

		BingOutputPayload bingResponse = new BingOutputPayload();
		bingResponse.setStatusCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		bingResponse.setStatusDescription(ErrorMessages.BING_DOWN);

		return bingResponse;
	}

	/**
	 * @param payload
	 * @return void
	 * @throws JsonProcessingException
	 * 
	 *            Invoking Transaction micro-service asynchronously. 
	 * 
	 */
	@Async
	public void insertOperationFlow(TransactionFlowPayload transactionFlowPayload)  throws JsonProcessingException{
		String stringPayload = null;
		try {
			HttpHeaders oauthHeaders = oAuthAccessTokenConfig.getHeaderWithAccessToken();
			stringPayload = mapper.writeValueAsString(transactionFlowPayload);
			HttpEntity<String> insertRequest = new HttpEntity<>(stringPayload, oauthHeaders);
			logger.debug("Invoking the transaction service to insert the data into flow table with request {} ",insertRequest);

			ResponseEntity<String> insertResponse = restTemplate.exchange(transactionServiceUrl,HttpMethod.POST, insertRequest, String.class);
			logger.debug("Response from Transaction microservice {} ", insertResponse);
		} catch (Exception exception) {

			logger.error("Error occurred while inserting into transaction table {} {} .", stringPayload, exception.getMessage());
		}
	}
}