package com.anthem.hca.smartpcp.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.model.RulesEngineOutputPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
public class RestClientService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private HttpHeaders headers;
	
	@Value("${drools.engine.url}")
    private String droolsEngineUrl;
	
	
	@Value("${affinity.url}")
    private String affinityUrl;
	
	private static final Logger logger = LogManager.getLogger(RestClientService.class);
	
	@HystrixCommand(fallbackMethod = "getFallBackInvocationOrder")
	public RulesEngineOutputPayload getInvocationOrder(RulesEngineInputPayload payload) throws JsonProcessingException{
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload),headers);
			logger.info("Calling Drools Rule Engine");
			return restTemplate.postForObject(droolsEngineUrl, request , RulesEngineOutputPayload.class );
	}
	
	public RulesEngineOutputPayload getFallBackInvocationOrder(RulesEngineInputPayload payload)  throws JsonProcessingException{
       
		logger.error(" Drools Rule Enginee is temporarily down with input payload "+payload);
		RulesEngineOutputPayload response = new RulesEngineOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage("Service is temporarily down. Please try after some time");
		return response;
    }
	
	@HystrixCommand(fallbackMethod = "getFallBackPCPAffinity",commandProperties = {
	        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "30000") })
	public RulesEngineOutputPayload getPCPAffinity(RulesEngineInputPayload payload) throws JsonProcessingException{
		
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload),headers);
			ResponseEntity<String> entity = restTemplate.postForEntity(affinityUrl, request, String.class);
			logger.info("Response from Affinity is "+entity.getBody());
			return null;
	}
	
	public RulesEngineOutputPayload getFallBackPCPAffinity(RulesEngineInputPayload payload) {
		logger.error(" Affinity service is temporarily down with input payload "+payload);
		RulesEngineOutputPayload response = new RulesEngineOutputPayload();
		response.setResponseCode(ResponseCodes.SERVICE_NOT_AVAILABLE);
		response.setResponseMessage("Service is temporarily down. Please try after some time");
		return response;
    }
	
	@Bean 
	public HttpHeaders headers(){
		return new HttpHeaders();
	}
}
