package com.anthem.hca.smartpcp.drools.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.anthem.hca.smartpcp.drools.io.TransactionFlowPayload;
import com.anthem.hca.smartpcp.drools.util.ResponseCodes;

@Service
@RefreshScope
public class DroolsRestClientService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private HttpHeaders headers;

	@Autowired
	private Tracer tracer;

	@Value("${spcp.operations.audit.url}")
	private String transactionsServiceUrl;

	private static final Logger logger = LogManager.getLogger(DroolsRestClientService.class);

	@Async
	public void insertOperationFlow(String requestFor) throws JsonProcessingException {
		logger.info("Inserting into Transaction Flow table...");

		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId(tracer.getCurrentSpan().traceIdString());
		payload.setServiceName("Drools-Service");
		payload.setOperationStatus("SUCCESS");
		payload.setOperationOutput("Rules successfully applied for " + requestFor);
		payload.setResponseCode(ResponseCodes.SUCCESS.toString());
		payload.setResponseMessage("Success");

		insertIntoTransactionMS(payload);

		logger.info("Inserted into Transaction Flow table");
	}

	public void insertIntoTransactionMS(TransactionFlowPayload payload) throws JsonProcessingException {
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
		logger.info("Request = " + mapper.writeValueAsString(payload));
		restTemplate.postForObject(transactionsServiceUrl, request, String.class);
	}

}
