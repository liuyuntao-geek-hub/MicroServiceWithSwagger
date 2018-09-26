/**
 * @author AF53723
 * Calls async method to store transaction flow payload to transaction table.
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.service;

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

import com.anthem.hca.smartpcp.mdoprocessing.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdoprocessing.utils.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RefreshScope
public class AsyncClientService {
	
	private static final Logger  LOGGER = LoggerFactory.getLogger(AsyncClientService.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Value("${spcp.mdo.pool.url}")
	private String mdoPoolingUrl;
	
	@Value("${spcp.operations.audit.url}")
	private String transactionServiceUrl;
	
	@Autowired
	private OAuthAccessTokenConfig oauthTokenConfig;
	
	/**
	 * @param payload
	 * @throws JsonProcessingException
	 */
	@Async
	public void insertOperationFlow(TransactionFlowPayload payload) throws JsonProcessingException {

		try{
			HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
	        HttpEntity<String> request = new HttpEntity<>( mapper.writeValueAsString(payload) ,oAuthheaders);
	        ResponseEntity<String> outputPayload = restTemplate.exchange(
	        		transactionServiceUrl,
	                    HttpMethod.POST,
	                    request,
	                    String.class
	                   );
	        LOGGER.debug("Invoking the transaction service to insert the data into flow table{}", outputPayload.getBody());
		}		
		catch(Exception exception){
			LOGGER.error("Error inserting into Transaction Table {}" , exception.getMessage(),exception);
			
		}
	}
}
