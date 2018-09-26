package com.anthem.hca.smartpcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.bing.model.BingInputPayload;
import com.anthem.hca.smartpcp.bing.model.BingOutputPayload;
import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.drools.io.TransactionFlowPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;



/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - RestClientService contains logic to connect to other services and get
 *               the required parameters. Contains Circuit Breaker pattern implemented
 *         		 to continue operating when a related service fails, preventing the
 *         		 failure from cascading and giving the failing service time to
 *         		 recover.
 * 
 * @author AF71111
 */
@Service
@RefreshScope
public class RestClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper mapper;
	

	private HttpHeaders headers;
	
	private HttpHeaders oAuthheaders;

	/*@Value("${transaction.service.url}")
	private String transactionServiceUrl;*/
	
	@Value("${spcp.bing.routes.url}")
	private String bingRoutesUrl;
	

	@Value("${spcp.bing.key}")
	private String bingKey;

	
	/**
	 * Connects to Bing to fetch the geocodes for a particular member.
	 * 
	 * @param url
	 * @return String
	 */
	@HystrixCommand(fallbackMethod = "getFallBackGeocodes", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000") })
	public String getGeocodes(String url) {
		LOGGER.debug("Calling Bing Service to fetch member geocodes {}", Constants.EMPTY_STRING);
		return restTemplate.getForObject(url, String.class);
	}
	
	/**
	 * Circuit Breaker fallback method for getGeocodes.
	 * 
	 * @param url
	 * @return String
	 */
	public String getFallBackGeocodes(String url) {
		LOGGER.error("Bing is temporarily down with url {}",url);
		return ResponseCodes.SERVICE_NOT_AVAILABLE;
	}
	
	/**
	 * @param bingInputPayload			Bing Input payload
	 * @return BingOutputPayload		Bing Output from Bing Service
	 * @throws JsonProcessingException	Exception when calling Bing.
	 * 
	 * 		getDistanceMatrix is used to distance matrix from Bing service. 
	 * 		Default timeout of Bing is 1sec. For smooth testing changed it to 5sec.
	 * 
	 */
	@HystrixCommand(fallbackMethod = "fallbackGetDistanceMatrix", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
	public BingOutputPayload getDistanceMatrix(BingInputPayload bingInputPayload) throws JsonProcessingException{

		BingOutputPayload bingResponse = null;
		try {

			String bingRequest = mapper.writeValueAsString(bingInputPayload);

			LOGGER.debug("Bing Request: {}" , bingRequest);
			LOGGER.debug("Bing URL: {}" , bingRoutesUrl);
			LOGGER.debug("Bing Key: {}" , bingKey);

			String finalBingURL = bingRoutesUrl + Constants.EQUAL + bingKey;
			LOGGER.debug("Final Bing URL: {}" , finalBingURL);

			HttpEntity<String> request = new HttpEntity<>(bingRequest, headers);
			bingResponse = restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class);

			String bingOutput = mapper.writeValueAsString(bingResponse);
			LOGGER.debug("Bing Response: {}", bingOutput);

		}catch(Exception exception) {

			LOGGER.error(ErrorMessages.BING_SERVICE_ERROR,exception);

			bingResponse = new BingOutputPayload();
			bingResponse.setStatusCode(ResponseCodes.OTHER_EXCEPTIONS);
			bingResponse.setStatusDescription(ErrorMessages.BING_SERVICE_ERROR);
		}

		return bingResponse;
	}

	/**
	 * @param bingInputPayload			Bing Input payload
	 * @return BingOutputPayload		Bing Output from Bing Service
	 *  
	 * 		Circuit Breaker fallback method for getDistanceMatrix.
	 * 
	 */
	public BingOutputPayload fallbackGetDistanceMatrix(BingInputPayload bingInputPayload) {

		LOGGER.error(ErrorMessages.BING_DOWN, bingInputPayload);

		BingOutputPayload bingResponse = new BingOutputPayload();
		bingResponse.setStatusCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		bingResponse.setStatusDescription(ErrorMessages.BING_DOWN);

		return bingResponse;
	}

	/**
	 * @param transactionFlowPayload		The Transaction Service Object Payload
	 * @return void							None
	 * @throws JsonProcessingException 		When error calling the Transaction Service
	 * 
	 * 		insertIntoTransactionMS invokes the Transaction Service Microservice with a transactionFlowPayload 
	 * 		to insert the data in FLOW_OPERATIONS_AUDIT table.
	 * 
	 */
	/*@Async
	public void insertIntoTransactionMS(TransactionFlowPayload transactionFlowPayload, HttpHeaders headers)  throws JsonProcessingException{
		String stringPayload = null;
		try {
			
			stringPayload = mapper.writeValueAsString(transactionFlowPayload);
			HttpEntity<String> insertRequest = new HttpEntity<>(stringPayload, this.oAuthheaders);

			LOGGER.debug("Invoking the transaction service to insert the data into flow table with request {} ",insertRequest);
			restTemplate.exchange(transactionServiceUrl,HttpMethod.POST, insertRequest, String.class);
		} catch (Exception exception) {

			LOGGER.error(ErrorMessages.AUDIT_TX_UPDATE_FAILURE, exception);
		}
	}*/
	
}
