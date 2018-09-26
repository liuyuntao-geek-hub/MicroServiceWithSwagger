/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validations
 * 
 * 
 * @author AF70315
 */ 
package com.anthem.hca.smartpcp.providervalidation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.providervalidation.utility.OAuthAccessTokenConfig;
import com.anthem.hca.smartpcp.providervalidation.vo.TransactionPayloadInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class RestClientService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Value("${spcp.operations.audit.url}")
	private String transactionServiceUrl;
	
	@Autowired
	private OAuthAccessTokenConfig oauthTokenConfig;

	private static final Logger LOGGER =LoggerFactory.getLogger(RestClientService.class);

	/**
	 * Asynchronous Method Call to Transaction Micro service
	 * 
	 * @param TransactionPayloadInfo
	 */
	@Async
	public void insertOperationFlow(TransactionPayloadInfo payload) throws JsonProcessingException {
		try {
			HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
			LOGGER.debug("Invoking the transaction service oAuthheaders =="+ oAuthheaders);
			
			HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(payload) ,oAuthheaders);
		        ResponseEntity<String> outputPayload = restTemplate.exchange(
		        		transactionServiceUrl,
		                    HttpMethod.POST,
		                    request,
		                    String.class
		                   );
		    LOGGER.debug("Invoking the transaction service to insert the data into flow table{}", outputPayload.getBody());
		} catch (Exception exception) {
			LOGGER.error("Error occured while inserting into transaction table =="+ exception);
		}
	}
	
	public RestClientService()
	{
	}

}
