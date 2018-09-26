package com.anthem.hca.smartPCP.mdoprocessingtest.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.mdoprocessing.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdoprocessing.service.AsyncClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MdoAsyncClientServiceTest {

	@Mock
	private AsyncClientService asyncClientService;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private HttpHeaders headers;
	
	
	
	@MockBean
	private Tracer tracer;
	
	@Value("${transactionTable.service.url}")
	private String transactionServiceUrl;
	
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInsertOperationFlow() throws JsonProcessingException{
		
		TransactionFlowPayload transPayload = createTransactionPayload();
        doNothing().when(asyncClientService).insertOperationFlow(any(TransactionFlowPayload.class));		
	    asyncClientService.insertOperationFlow(transPayload);
	    
	}
	
	public TransactionFlowPayload createTransactionPayload() {

		TransactionFlowPayload transactionPayload = new TransactionFlowPayload();
		transactionPayload.setTraceId("1234554321");
		transactionPayload.setServiceName("MDO-PROCESSING");
		transactionPayload.setOperationStatus("SUCCESS");
		transactionPayload.setOperationOutput("PCP id: " + 12345);
		transactionPayload.setResponseCode("200");
		transactionPayload.setResponseMessage("Returned PCP");
		
		return transactionPayload;
	}
}
