package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.drools.io.TransactionFlowPayload;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OperationAuditFlowServiceTest {

	@InjectMocks
	private OperationAuditFlowService operationAuditFlowService;

	@Mock
	private Tracer tracer;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private OAuthAccessTokenConfig oauthService;

	@Value("${spcp.operations.audit.url}")
	private String transactionsServiceUrl;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInsertOperationFlowDrools() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowDrools("SMARTPCP");
		assertTrue(true);

	}

	@Test
	public void testInsertOperationFlowAffinity() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowAffinity(new ArrayList<>(), null, false);
		assertTrue(true);

	}

	@Test
	public void testInsertOperationFlowAffinity2() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowAffinity(new ArrayList<>(), null, true);
		assertTrue(true);

	}

	@Test
	public void testInsertOperationFlowAffinity3() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowAffinity(getProviderlist(), getProvider(), false);
		assertTrue(true);

	}

	@Test
	public void testInsertOperationFlowAffinity4() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowAffinity(getProviderlist(), null, false);
		assertTrue(true);

	}

	@Test
	public void testInsertOperationFlowMDO() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowMDO(1000, scoringProviderPCP());
		assertTrue(true);

	}

	@Test
	public void testInsertOperationFlowMDO2() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		TransactionFlowPayload payload = createDroolsPayload();
		String output = "responseCode :200, responseMessage: SPCP Flow Operation Logged Successfully";
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),
				oauthService.getHeaders());
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(transactionsServiceUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		operationAuditFlowService.insertOperationFlowMDO(1000, null);
		assertTrue(true);

	}

	public TransactionFlowPayload createDroolsPayload() {
		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId(tracer.getCurrentSpan().traceIdString());
		payload.setServiceName(Constants.DROOLS_APP_NAME);
		payload.setOperationStatus(Constants.SUCCESS);
		payload.setOperationOutput(Constants.RULES_EXTRACTED + " | SMARTPCP");
		payload.setResponseCode(ResponseCodes.SUCCESS);
		payload.setResponseMessage(Constants.SUCCESS);
		return payload;
	}

	public ScoringProvider scoringProviderPCP() {

		ScoringProvider pcp = new ScoringProvider();
		pcp.setProvPcpId("MDO123");
		pcp.setRgnlNtwkId("HARSH01");
		pcp.setLastName("MDO_LAST");
		pcp.setDistance(7.0680473834351885);
		pcp.setDummyFlag(false);
		pcp.setRank(3);
		pcp.setVbpFlag("Y");
		pcp.setPcpScore(115);
		pcp.setVbpScore(20);
		pcp.setDistanceScore(40);
		pcp.setLimitedTimeBonusScore(15);
		pcp.setLanguageScore(10);
		pcp.setRankScore(30);
		List<String> langList = new ArrayList<>();
		langList.add("SPA");
		List<String> spcltyList = new ArrayList<>();
		spcltyList.add("Internal Medicine");
		pcp.setPcpLang(langList);
		pcp.setSpeciality(spcltyList);
		return pcp;
	}

	public Provider getProvider() {
		Provider provider = new Provider();
		provider.setProvPcpId("AFNTY1234");
		return provider;
	}

	public List<Provider> getProviderlist() {
		List<Provider> providerList = new ArrayList<>();
		Provider provider1 = new Provider();
		provider1.setProvPcpId("AFNTY1234");
		Provider provider2 = new Provider();
		provider2.setProvPcpId("AFNTY2345");
		providerList.add(provider1);
		providerList.add(provider1);
		return providerList;
	}
}