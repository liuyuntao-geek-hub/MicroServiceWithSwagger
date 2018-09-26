package com.anthem.hca.smartpcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.model.AffinityInputPayload;
import com.anthem.hca.smartpcp.model.AffinityOutPayload;
import com.anthem.hca.smartpcp.model.MDOInputPayload;
import com.anthem.hca.smartpcp.model.MDOOutputPayload;
import com.anthem.hca.smartpcp.model.RulesInputPayload;
import com.anthem.hca.smartpcp.model.SmartPCPRulesOutputPayload;
import com.anthem.hca.smartpcp.util.OAuthAccessTokenConfig;
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

	private HttpHeaders oAuthheaders;

	@Value("${spcp.drools.url}")
	private String droolsEngineUrl;

	@Value("${spcp.affinity.url}")
	private String affinityUrl;

	@Value("${spcp.mdo.processing.url}")
	private String mdoUrl;
	
	@Autowired
	private OAuthAccessTokenConfig oauthTokenConfig;


	/**
	 * @param payload
	 * @return RulesEngineOutputPayload
	 * @throws JsonProcessingException
	 * 
	 *             Default timeout of hystrix is 1 sec. For smooth testing
	 *             changed it to 5sec.
	 * 
	 */

	@HystrixCommand(fallbackMethod = "getFallBackInvocationOrder", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	public SmartPCPRulesOutputPayload getInvocationOrder(RulesInputPayload payload) throws JsonProcessingException {
		  this.oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
	        HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(payload) ,this.oAuthheaders);
	        ResponseEntity<SmartPCPRulesOutputPayload> smartPCPRulesOutputPayload = restTemplate.exchange(
	                    droolsEngineUrl,
	                    HttpMethod.POST,
	                    request,
	                    SmartPCPRulesOutputPayload.class
	                   );
	        return smartPCPRulesOutputPayload.getBody();
	}

	/**
	 * Circuit Breaker fallback method for getInvocationOrder.
	 * 
	 * @param payload
	 * @return RulesEngineOutputPayload
	 */
	public SmartPCPRulesOutputPayload getFallBackInvocationOrder(RulesInputPayload payload) {

		LOGGER.error(" Drools Rule Enginee is temporarily down with input payload having processing state {}",
				payload.getMemberProcessingState());
		SmartPCPRulesOutputPayload response = new SmartPCPRulesOutputPayload();
		response.setResponseCode(Integer.parseInt(ResponseCodes.SERVICE_NOT_AVAILABLE));
		response.setResponseMessage(ErrorMessages.DROOLS_DOWN);
		return response;
	}

	/**
	 * @param payload
	 * @return AffinityOutPayload
	 * @throws JsonProcessingException
	 * 
	 *             Default timeout of hystrix is 1 sec. For smooth testing
	 *             changed it to 5sec.
	 */
	@HystrixCommand(fallbackMethod = "getFallBackPCPAffinity", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
	public AffinityOutPayload getPCPAffinity(AffinityInputPayload payload) throws JsonProcessingException {
		 HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(payload) ,this.oAuthheaders);
	        ResponseEntity<AffinityOutPayload> affinityOutPayload = restTemplate.exchange(
	        			affinityUrl,
	                    HttpMethod.POST,
	                    request,
	                    AffinityOutPayload.class
	                   );
	        return affinityOutPayload.getBody();
	}

	/**
	 * Circuit Breaker fallback method for getPCPAffinity.
	 * 
	 * @param payload
	 * @return AffinityOutPayload
	 */
	public AffinityOutPayload getFallBackPCPAffinity(AffinityInputPayload payload) {
		LOGGER.error(" Affinity service is temporarily down with input payload having date of birth {}",
				payload.getMemberDob());
		AffinityOutPayload response = new AffinityOutPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage(ErrorMessages.AFFINITY_DOWN);
		return response;
	}

	/**
	 * @param payload
	 * @return
	 * @throws JsonProcessingException
	 * 
	 *             Default timeout of hystrix is 1 sec. For smooth testing
	 *             changed it to 5sec.
	 */
	@HystrixCommand(fallbackMethod = "getFallBackPCPMDO", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "20000") })
	public MDOOutputPayload getPCPMDO(MDOInputPayload payload) throws JsonProcessingException {
		HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(payload) ,this.oAuthheaders);
        ResponseEntity<MDOOutputPayload> mdoOutputPayload = restTemplate.exchange(
        			mdoUrl,
                    HttpMethod.POST,
                    request,
                    MDOOutputPayload.class
                   );
        return mdoOutputPayload.getBody();
	}

	/**
	 * Circuit Breaker fallback method for getPCPMDO.
	 * 
	 * @param payload
	 * @return MDOOutputPayload
	 */
	public MDOOutputPayload getFallBackPCPMDO(MDOInputPayload payload) {
		LOGGER.error(" MDO service is temporarily down with input payload having date of birth {}",
				payload.getMemberDob());
		MDOOutputPayload response = new MDOOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage(ErrorMessages.MDOPROC_DOWN);
		return response;
	}

	
	/**
	 * Connects to Bing to fetch the geocodes for a particular member.
	 * 
	 * @param url
	 * @return String
	 */
	@HystrixCommand(fallbackMethod = "getFallBackGeocodes")
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

}
