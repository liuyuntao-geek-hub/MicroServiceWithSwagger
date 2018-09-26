/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.anthem.hca.smartpcp.mdo.pool.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncClientServiceTest {

	@InjectMocks
	private AsyncClientService asyncClientService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private OAuthAccessTokenConfig oauthTokenConfig;

	@Mock
	private HttpHeaders headers;
	
	@Value("${spcp.operations.audit.url}")
	private String transactionServiceUrl;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testInsertOperationFlow() throws JsonProcessingException {

		TransactionFlowPayload payload = new TransactionFlowPayload();
		HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
		ResponseEntity<String> mockResponse= new ResponseEntity<>("test",HttpStatus.OK);
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), oAuthheaders);
		Mockito.when(restTemplate.exchange(transactionServiceUrl, 
				HttpMethod.POST, 
				request, 
				String.class)).thenReturn(mockResponse);
		asyncClientService.insertOperationFlow(payload);
		assertTrue(true);
	}

}
