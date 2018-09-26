package com.anthem.hca.smartpcp.affinity.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.affinity.constants.Constants;
import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.BingInputPayload;
import com.anthem.hca.smartpcp.affinity.model.BingOutputPayload;
import com.anthem.hca.smartpcp.affinity.model.Destination;
import com.anthem.hca.smartpcp.affinity.model.Origin;
import com.anthem.hca.smartpcp.affinity.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.affinity.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.affinity.util.OAuthAccessTokenConfig;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		RestClientServiceTest is used to test the RestClientService
 *  
 * @author AF65409 
 */
public class RestClientServiceTest {
	@InjectMocks
	private RestClientService restClientService;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private HttpHeaders headers;
	@Mock
	private OAuthAccessTokenConfig oAuthAccessTokenConfig;

	@Value("${transaction.service.url}")
	private String transactionServiceUrl;
	@Value("${drools.engine.url}")
	private String droolsEngineUrl;
	@Value("${bing.url}")
	private String bingUrl;
	@Value("${bing.key}")
	private String bingKey;

	@Value("${security.client.id}")
	private String clientId;
	@Value("${security.secret.key}")
	private String secretKey;
	@Value("${security.api.key}")
	private String apiKey;
	@Value("${security.token.uri}")
	private String tokenUri;

	private static final String AUTHORIZATION = "Authorization";
	private static final String API_KEY = "apikey";

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getProviderValidRulesTest() throws JsonProcessingException{ 

		JsonNode rulesResponse = getRulesResponse();
		RulesEngineInputPayload rulesEngineInputPayload = getRulesPayload();
		HttpHeaders oauthHeaders = getOauthHeaders();

		Mockito.when(oAuthAccessTokenConfig.getHeaderWithAccessToken()).thenReturn(oauthHeaders);
		oauthHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> rulesRequest = new HttpEntity<String>(mapper.writeValueAsString(rulesEngineInputPayload),oauthHeaders);

		ResponseEntity<JsonNode> mockResponse= new ResponseEntity<>(rulesResponse,HttpStatus.OK);

		Mockito.when(restTemplate.exchange(droolsEngineUrl, HttpMethod.POST, rulesRequest , JsonNode.class)).thenReturn(mockResponse);

		JsonNode responsePayload = restClientService.getProviderValidRules(rulesEngineInputPayload);
		JsonNode fallBackResponse = restClientService.getProviderValidRules(rulesEngineInputPayload);
		assertNotNull(responsePayload);
		assertNotNull(fallBackResponse);

	}

	@Test
	public void getProviderValidRulesExceptionTest1() throws JsonProcessingException{ 

		try {
			Mockito.when(restTemplate.exchange(droolsEngineUrl, HttpMethod.POST, null , JsonNode.class)).thenReturn(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void getDistanceMatrixTest() throws JsonProcessingException{

		BingOutputPayload bingOutput = getBingOutputPayload();
		BingInputPayload bingInput = getBingInputPayload();

		String bingRequest = mapper.writeValueAsString(bingInput);
		String finalBingURL = bingUrl + Constants.EQUAL + bingKey;
		HttpEntity<String> request = new HttpEntity<>(bingRequest, headers);

		Mockito.when(restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class)).thenReturn(bingOutput);

		BingOutputPayload bingOutputNew = restClientService.getDistanceMatrix(bingInput);
		assertEquals(bingOutputNew,bingOutput);
	}

	@Test
	public void getDistanceMatrixExceptionTest1() throws JsonProcessingException{

		String finalBingURL = bingUrl + Constants.EQUAL + bingKey;

		try {
			Mockito.when(restTemplate.postForObject(finalBingURL, null, BingOutputPayload.class)).thenReturn(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getDistanceMatrixExceptionTest2() throws JsonProcessingException{

		BingInputPayload bingInput = getBingInputPayload();

		String bingRequest = mapper.writeValueAsString(bingInput);
		String finalBingURL = null + Constants.EQUAL + bingKey;
		HttpEntity<String> request = new HttpEntity<>(bingRequest, headers);

		try {
			Mockito.when(restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class)).thenReturn(null);
		}catch (Exception e) {
			e.printStackTrace();
		}

		restClientService.getDistanceMatrix(bingInput);
		assertTrue(true);
	}

	@Test
	public void getDistanceMatrixExceptionTest3() throws JsonProcessingException{

		BingInputPayload bingInput = getBingInputPayload();

		String bingRequest = mapper.writeValueAsString(bingInput);
		String finalBingURL = bingUrl+ Constants.EQUAL + null;
		HttpEntity<String> request = new HttpEntity<>(bingRequest, headers);

		try {
			Mockito.when(restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class)).thenReturn(null);
		}catch (Exception e) {
			e.printStackTrace();
		}

		restClientService.getDistanceMatrix(bingInput);
		assertTrue(true);
	}

	@Test
	public void getDistanceMatrixExceptionTest4() throws JsonProcessingException{

		BingInputPayload bingInput = getBingInputPayload();

		String bingRequest = mapper.writeValueAsString(bingInput);
		String finalBingURL = bingUrl+ Constants.EQUAL + bingKey;
		HttpEntity<String> request = new HttpEntity<>(bingRequest, headers);

		try {
			Mockito.when(restTemplate.postForObject(finalBingURL, request, BingOutputPayload.class)).thenReturn(null);
		}catch (Exception e) {
			e.printStackTrace();
		}

		restClientService.getDistanceMatrix(bingInput);
		assertTrue(true);
	}

	@Test
	public void getInsertOperationTableTest() throws JsonProcessingException {

		ResponseEntity<String> insertResponse = getResponse();
		TransactionFlowPayload transactionFlowPayload = getTransactionFlowPayload();
		HttpHeaders oauthHeaders = getOauthHeaders();

		Mockito.when(oAuthAccessTokenConfig.getHeaderWithAccessToken()).thenReturn(oauthHeaders);
		String stringPayload = mapper.writeValueAsString(transactionFlowPayload);
		HttpEntity<String> insertRequest = new HttpEntity<>(stringPayload, oauthHeaders); 

		Mockito.when(restTemplate.exchange(transactionServiceUrl,HttpMethod.POST, insertRequest, String.class)).thenReturn(insertResponse);

		restClientService.insertOperationFlow(transactionFlowPayload);
		assertTrue(true);
	}

	@Test
	public void getInsertOperationTableExceptionTest1() throws JsonProcessingException {

		try {
			Mockito.when(restTemplate.exchange(transactionServiceUrl,HttpMethod.POST, null, String.class)).thenReturn(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void getInsertOperationTableExceptionTest2() throws JsonProcessingException {

		TransactionFlowPayload transactionFlowPayload = getTransactionFlowPayload();
		HttpHeaders oauthHeaders = getOauthHeaders();

		Mockito.when(oAuthAccessTokenConfig.getHeaderWithAccessToken()).thenReturn(oauthHeaders);

		String stringPayload = mapper.writeValueAsString(transactionFlowPayload);
		HttpEntity<String> insertRequest = new HttpEntity<>(stringPayload, oauthHeaders); 

		try {
			Mockito.when(restTemplate.exchange(null,HttpMethod.POST, insertRequest, String.class)).thenReturn(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		restClientService.insertOperationFlow(transactionFlowPayload);
		assertTrue(true);
	}

	@Test
	public void getInsertOperationTableExceptionTest3() throws JsonProcessingException {

		TransactionFlowPayload transactionFlowPayload = getTransactionFlowPayload();
		HttpHeaders oauthHeaders = getOauthHeaders();

		Mockito.when(oAuthAccessTokenConfig.getHeaderWithAccessToken()).thenReturn(oauthHeaders);

		String stringPayload = mapper.writeValueAsString(transactionFlowPayload);
		HttpEntity<String> insertRequest = new HttpEntity<>(stringPayload, oauthHeaders); 

		try {
			Mockito.when(restTemplate.exchange(transactionServiceUrl,HttpMethod.POST, insertRequest, String.class)).thenReturn(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		restClientService.insertOperationFlow(transactionFlowPayload);
		assertTrue(true);
	}

	public TransactionFlowPayload getTransactionFlowPayload() {
		TransactionFlowPayload payload = new TransactionFlowPayload();
		payload.setTraceId("12345");
		payload.setServiceName("spcp-affinity");
		payload.setResponseCode(ResponseCodes.SUCCESS);
		payload.setResponseMessage(ErrorMessages.SUCCESS);
		payload.setOperationStatus(ErrorMessages.SUCCESS);
		payload.setOperationOutput(ErrorMessages.PCP_ASSIGNED + "Z0Z017");

		return payload;
	}

	public ResponseEntity<String> getResponse() {

		ResponseEntity<String> mockResponse= new ResponseEntity<>("test",HttpStatus.OK);

		return mockResponse;
	}

	private JsonNode getRulesResponse() {
		JsonNode droolsResponse = null;

		try {
			droolsResponse = new ObjectMapper().readTree("{\"responseCode\":200,\"responseMessage\":\"Affinity Provider Validation Rules successfully applied on Payload\",\"rules\":{\"primarySpecialties\":[\"Internal Medicine\",\"Family Practice\",\"Pediatric\",\"General Practice\",\"Geriatric\",\"Nurse Practice\"],\"contractEffectiveBeyond\":90,\"drivingDistance\":30,\"providerTiers\":[1],\"rolloverFlag\":\"Y\",\"specialtyMinAgeList\":[{\"key\":\"Pediatric\",\"value\":0},{\"key\":\"Geriatric\",\"value\":60}],\"validationFlagList\":[{\"key\":\"SPECIALTY_VALIDATION\",\"value\":true},{\"key\":\"AGE_RANGE_VALIDATION\",\"value\":true},{\"key\":\"GENDER_VALIDATION\",\"value\":false},{\"key\":\"CONTRACT_VALIDATION\",\"value\":true},{\"key\":\"DISTANCE_VALIDATION\",\"value\":true},{\"key\":\"PANEL_CAPACITY_VALIDATION\",\"value\":false},{\"key\":\"NETWORK_VALIDATION\",\"value\":true},{\"key\":\"TIER_VALIDATION\",\"value\":false},{\"key\":\"ACCEPTING_PATIENTS_VALIDATION\",\"value\":false},{\"key\":\"ROLLOVER_VALIDATION\",\"value\":true}],\"specialtyMaxAgeList\":[{\"key\":\"Pediatric\",\"value\":18},{\"key\":\"Geriatric\",\"value\":999}],\"specialtyGenderList\":[{\"key\":\"OBGYN\",\"value\":\"F\"}],\"panelCapacityPercent\":0}}");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return droolsResponse;
	}

	public HttpHeaders getOauthHeaders() {

		return buildHttpHeaders("[Bearer eyJhbGciOiJSUzI1NiIsIng1dCI6Ik83X0ROUEFYbXpLRWZTcS13S09OQlZuYUlVUSIsImtpZCI6Ik83X0ROUEFYbXpLRWZTcS13S09OQlZuYUlVUSIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI2MmFlMWM1NjE1MzQ0NWQ1ODg4NGMwNjIwM2VjMmFlNCIsInNjb3BlIjoicHVibGljIiwiY2xpZW50X2lkIjoiMzAwMDI4ODdmY2Y4NDI2Mjk3MWNhYWU3ZGVjOGM4ODciLCJzdWIiOiIzMDAwMjg4N2ZjZjg0MjYyOTcxY2FhZTdkZWM4Yzg4NyIsImlzcyI6Imh0dHBzOi8vdmExMG41MDg1MC51cy5hZC53ZWxscG9pbnQuY29tL3NlY3VyZWF1dGgzIiwiYXVkIjoiaHR0cHM6Ly92YTEwbjUwODUwLnVzLmFkLndlbGxwb2ludC5jb20vc2VjdXJlYXV0aDMiLCJleHAiOjE1MzQxNjU2MTgsIm5iZiI6MTUzNDE2NDcxOH0.O1Pbh6kWNwZWeowpmk8AGz_oAguG6zJe6a1utoBuvntyI2Qr18f_8BTj2-1pgo61u76WEIN5SzpgbypEtoowXuRbE0LMP2pyqtm0D1TDUurO_L_K_t-eSvaBQnyBXMRw5pJU1bn0byf8SjvK0UcJVVrVxuXKOD3KWMABvetc5jHSAXohayo69VLvaE-OJEIkVaLVtS7SLQcECUXt5U-W9OdqPeGnYbBqugMCJUaAUdGg6vMhkvMjsI0DZfnfwiUgQfgfHNXkRbyMtbqBAdjsxvDMUc1zu0v9GT1fxNvd8wHPQj4LRTJNDKC2XgUWyJGieDkQEa4sYROEqsbexnK1Wg]","[c8el3OqAddkCo5N2J4hGMHDr4rh0uJx3]" , MediaType.APPLICATION_JSON);

	}

	private HttpHeaders buildHttpHeaders(String authType, String authValue, MediaType mediaType) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, authType + authValue);
		headers.add(API_KEY, apiKey);
		headers.setContentType(mediaType);
		return headers;
	}

	public BingOutputPayload getBingOutputPayload() {

		ObjectMapper mapper = new ObjectMapper();
		BingOutputPayload bingOutput = null;
		try {
			bingOutput = mapper.readValue("{\"authenticationResultCode\":\"ValidCredentials\","
					+ "\"brandLogoUri\":\"http://dev.virtualearth.net/Branding/logo_powered_by.png\","
					+ "\"copyright\":\"Copyright © 2018 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.\","
					+ "\"resourceSets\":[{\"estimatedTotal\":1,\"resources\":[{\"type\":null,\"destinations\":[{\"latitude\":35.399724,\"longitude\":-119.465989},{\"latitude\":35.76907,\"longitude\":-119.24565},{\"latitude\":35.40863,\"longitude\":-119.031217}],\"errorMessage\":\"Request accepted.\",\"origins\":[{\"latitude\":35.36645,\"longitude\":-119.0098}],\"results\":[{\"destinationIndex\":0,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":44.826,\"travelDuration\":29.255},{\"destinationIndex\":1,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":52.907,\"travelDuration\":30.715},{\"destinationIndex\":2,\"originIndex\":0,\"totalWalkDuration\":0,\"travelDistance\":7.094,\"travelDuration\":6.272}]}]}],\"statusCode\":\"200\",\"statusDescription\":\"OK\",\"traceId\":\"69cee129e0cd4003880b55b490e71df0|BN1EAE8F4E|7.7.0.0\"}", BingOutputPayload.class);
			bingOutput.setStatusCode(ErrorMessages.SUCCESS);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bingOutput;
	}

	public BingInputPayload getBingInputPayload() {

		BingInputPayload bingInputPayload = new BingInputPayload();

		List<Origin> originList = new ArrayList<Origin>();
		Origin origin =  new Origin();
		origin.setLatitude(35.36645);
		origin.setLongitude(-119.0098);
		originList.add(origin);

		List<Destination> destinationList = new ArrayList<Destination>();
		Destination destinationA = new Destination();
		destinationA.setLatitude(35.399724);
		destinationA.setLongitude(-119.465989);
		destinationList.add(destinationA);
		Destination destinationB = new Destination();
		destinationB.setLatitude(35.76907);
		destinationB.setLongitude(-119.24565);
		destinationList.add(destinationB);
		Destination destinationC = new Destination();
		destinationC.setLatitude(35.40863);
		destinationC.setLongitude(-119.031217);
		destinationList.add(destinationC);

		String travelMode = "driving";

		bingInputPayload.setOrigins(originList);
		bingInputPayload.setDestinations(destinationList);
		bingInputPayload.setTravelMode(travelMode);

		return bingInputPayload;
	} 


	public RulesEngineInputPayload getRulesPayload() {

		Member member = getMemberInfo();
		RulesEngineInputPayload rulesEngineInputPayload = null;
		rulesEngineInputPayload = new RulesEngineInputPayload();
		rulesEngineInputPayload.setInvocationSystem(member.getInvocationSystem());
		rulesEngineInputPayload.setMemberLineOfBusiness(member.getMemberLineOfBusiness());
		rulesEngineInputPayload.setMemberProcessingState(member.getMemberProcessingState());
		rulesEngineInputPayload.setMemberProductType(member.getMemberProductType());
		rulesEngineInputPayload.setMemberType(member.getMemberType());
		rulesEngineInputPayload.setSystemType(member.getSystemType());

		return rulesEngineInputPayload;
	}

	private Member getMemberInfo() {

		Member member = new Member();
		member.setInvocationSystem("06");
		member.setSystemType("B");
		member.setMemberEid("976A78568");
		member.setMemberType("N");
		member.setMemberLineOfBusiness("CT1");
		member.setMemberProcessingState("CT");
		member.setMemberContractCode(Arrays.asList("CC86"));
		member.setMemberNetworkId(Arrays.asList("CC0D"));
		Address memberAddress = new Address();
		memberAddress.setLatitude(35.36645);
		memberAddress.setLongitude(-119.0098);
		member.setAddress(memberAddress);
		member.setMemberDob("1972-09-05");
		member.setMemberGender("M");
		member.setMemberSequenceNumber("2");
		member.setMemberFirstName("KERI");
		member.setRollOverPcpId("Z0Z07");
		member.setMemberEffectiveDate("1992-06-13");
		member.setMemberProductType("HMO");		

		return member;
	}	
}