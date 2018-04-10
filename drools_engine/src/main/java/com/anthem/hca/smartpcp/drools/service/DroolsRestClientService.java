/**
 * 
 */
package com.anthem.hca.smartpcp.drools.service;



import java.util.Date;
import java.sql.Timestamp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.drools.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.drools.util.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author AF74173
 *
 */
@Service
public class DroolsRestClientService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private HttpHeaders headers;
	
	@Autowired
	private Tracer tracer;

	
	@Value("${transactions.service.url}")
	private String transactionsServiceUrl;
	
	private static final Logger logger = LogManager.getLogger(DroolsRestClientService.class);
	
	/**
	 * @param outputPayload
	 * @param requestFor
	 * @throws JsonProcessingException
	 * Method to insert transaction flow table data
	 */
	@Async
	public void insertOperationFlow(String requestFor) throws JsonProcessingException  {
			TransactionFlowPayload payload = new TransactionFlowPayload();
			payload.setTransactionId(Long.toString(tracer.getCurrentSpan().getTraceId()));
			payload.setServiceName("Drools-Service");
			payload.setOperationStatus(ResponseCodes.SUCCESS.toString());
			payload.setOperationOutput("Drools successfully logged for " + requestFor);
			payload.setResponseCode(ResponseCodes.SUCCESS.toString());
			payload.setResponseMessage("Success");
			payload.setServiceRequestTimestamp(new Timestamp(new Date().getTime()));

			insertIntoTransactionMS(payload);

	}

	/**
	 * @param payload
	 * @throws JsonProcessingException
	 * method to call transaction table micro service and insert the flow payload
	 */
	public void insertIntoTransactionMS(TransactionFlowPayload payload) throws JsonProcessingException {
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
		logger.info("request is " + mapper.writeValueAsString(payload));
		//restTemplate.post
		String response = restTemplate.postForObject(transactionsServiceUrl, request, String.class );
		logger.info("response " + response);
	}

	@Bean 
	public HttpHeaders headers(){
		HttpHeaders httpHeaders =  new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}
	
}
