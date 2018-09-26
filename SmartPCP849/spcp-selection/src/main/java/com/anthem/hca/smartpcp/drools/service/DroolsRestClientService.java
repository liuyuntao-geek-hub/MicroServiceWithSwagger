package com.anthem.hca.smartpcp.drools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.drools.io.TransactionFlowPayload;
import com.anthem.hca.smartpcp.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The DroolsRestClientService class is the Service Layer implementation of writing audit
 * log flow to Transaction Flow table.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.6
 */

@Service
@RefreshScope
public class DroolsRestClientService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private Tracer tracer;

	@Autowired
	private OAuthAccessTokenConfig oAuthAccessTokenConfig;

	@Value("${spcp.operations.audit.url}")
	private String transactionsServiceUrl;

	/*@Value("${spring.application.name}")
	private String applicationName;*/

	/**
	 * This method creates a Transaction Flow Payload and forwards that to another method
	 * 'insertIntoTransactionMS' to invoke the Transaction Service Micro Service.
	 * 
	 * @param  agendaGroup             Agenda Group String to append in the Message
	 * @return                         None
	 * @throws JsonProcessingException When error calling the Transaction Service
	 * @see    TransactionFlowPayload
	 */
	@Async
	public void insertOperationFlow(String agendaGroup) throws JsonProcessingException {
		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId(tracer.getCurrentSpan().traceIdString());
		payload.setServiceName("DROOLS");
		payload.setOperationStatus("SUCCESS");
		payload.setOperationOutput("Rules Extracted" + "|" + agendaGroup);
		payload.setResponseCode(ResponseCodes.SUCCESS.toString());
		payload.setResponseMessage("SUCCESS");

		//insertIntoTransactionMS(payload);
	}

	/**
	 * This method invokes the Transaction Service Microservice with a payload to Insert
	 * the data in FLOW_OPERATIONS_AUDIT table.
	 * 
	 * @param  payload                 The Transaction Service Object Payload
	 * @return                         None
	 * @throws JsonProcessingException When error calling the Transaction Service
	 * @see    HttpEntity
	 */
	/*public void insertIntoTransactionMS(TransactionFlowPayload payload) throws JsonProcessingException {
		HttpHeaders headers = oAuthAccessTokenConfig.getHeaderWithAccessToken();
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
		restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class);
	}*/

}