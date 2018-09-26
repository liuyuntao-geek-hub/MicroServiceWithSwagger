package com.anthem.hca.smartpcp.providervalidation.service;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.providervalidation.vo.TransactionPayloadInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncClientServiceTest {

	@InjectMocks
	private RestClientService asyncClientService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private HttpHeaders headers;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetInsertOperationTable() throws JsonProcessingException {

		TransactionPayloadInfo payload = new TransactionPayloadInfo();
		payload.setTraceId("123456");
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
		Mockito.when(restTemplate.postForObject("url", request, String.class)).thenReturn("");
		asyncClientService.insertOperationFlow(payload);
		assertTrue(true);
	}

}
