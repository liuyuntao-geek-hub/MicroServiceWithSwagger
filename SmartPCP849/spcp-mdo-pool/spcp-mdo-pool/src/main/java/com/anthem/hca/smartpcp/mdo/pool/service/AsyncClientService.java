/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

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

import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AsyncClientService contains logic to connect to connect to the transaction
 * microservice to insert the response into Splice machine in an asynchronous
 * way.
 * 
 * @author AF71111
 * 
 */

@Service
@RefreshScope
public class AsyncClientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncClientService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private OAuthAccessTokenConfig oauthTokenConfig;

	private HttpHeaders headers;

	@Value("${spcp.operations.audit.url}")
	private String transactionServiceUrl;

	public AsyncClientService() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.headers = httpHeaders;
	}

	/**
	 * @param payload
	 * @throws JsonProcessingException
	 */
	@Async
	public void insertOperationFlow(TransactionFlowPayload payload) {
		String stringPayload = null;
		try {
			LOGGER.debug("Invoking the transaction service to insert the data into flow table{}", transactionServiceUrl);
			HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
			HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), oAuthheaders);
			ResponseEntity<String> response  = restTemplate.exchange(
					transactionServiceUrl, 
					HttpMethod.POST, 
					request, 
					String.class
			);
			LOGGER.debug("Response from Transaction microservice {} ", response.getBody());
			
		} catch (Exception exception) {
			LOGGER.error("MDO Pool Service Error | Error occured while inserting data {} into transaction table {}",
					stringPayload,exception.getMessage(), exception);
		}
	}

}
