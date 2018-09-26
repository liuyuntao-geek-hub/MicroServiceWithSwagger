package com.anthem.hca.smartpcp.mdoprocessing.service;

import org.slf4j. Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author AF53723
 * Calls async method to store transaction flow payload to transaction table.
 *
 */
@Service
@RefreshScope
public class AsyncClientService {
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncClientService.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Value("${spcp.mdo.pool.url}")
	private String mdoPoolingUrl;
	
	@Value("${spcp.operations.audit.url}")
	private String transactionServiceUrl;
	
	private  HttpHeaders headers;
	
	public  AsyncClientService() {
	
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.headers = httpHeaders;
	}
	
	/**
	 * @param payload
	 * @throws JsonProcessingException
	 */
	@Async
	public void insertOperationFlow(TransactionFlowPayload payload) throws JsonProcessingException {

		
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
		String req=mapper.writeValueAsString(payload);
		logger.info("request is {} " , req);
		logger.info("Transaction Service Url is: {}" , transactionServiceUrl);
		// restTemplate.post
		try{
		String response = restTemplate.postForObject(transactionServiceUrl,
				request, String.class);
		String res=  mapper.writeValueAsString(response);
		logger.info("response from transactionTableInsertionMethod {}" , res);
		logger.info("Calling transaction table ms after {}" , new java.util.Date());
		}
		catch(Exception e){
			logger.error("Error inserting into Transaction Table {}" , e.getMessage());
			
		}
	}
}
