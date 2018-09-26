/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRules;
import com.anthem.hca.smartpcp.mdo.pool.model.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.model.RulesInputPayload;
import com.anthem.hca.smartpcp.mdo.pool.util.OAuthAccessTokenConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

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
	private OAuthAccessTokenConfig oauthTokenConfig; 

	@Value("${spcp.drools.mdo.url}")
	private String mdoPoolingRulesDroolsEngineUrl;

	@Value("${spcp.drools.provider.validation.url}")
	private String providerRulesDroolsUrl;

	@Value("${spcp.provider.validation.url}")
	private String providerValidationServiceUrl;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPoolInformation() throws JsonProcessingException {
		RulesInputPayload payload = createIpPayload();
		HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), oAuthheaders);
		ResponseEntity<MDOPoolingRulesOutputPayload> mockResponse= new ResponseEntity<>(createOutPayload(),HttpStatus.OK);
		Mockito.when(
				restTemplate
				.exchange(mdoPoolingRulesDroolsEngineUrl, HttpMethod.POST, request, MDOPoolingRulesOutputPayload.class))
				.thenReturn(mockResponse);
		MDOPoolingRulesOutputPayload responsePayload = restClientService.getPoolInformation(payload);
		assertEquals(100, responsePayload.getRules().getPoolSize());
		assertEquals(50, responsePayload.getRules().getMaxRadiusToPool());
	}

	@Test
	public void testGetProviderValidationRules() throws JsonProcessingException {
		RulesInputPayload payload = createIpPayload();
		HttpHeaders oAuthheaders = oauthTokenConfig.getHeaderWithAccessToken();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), oAuthheaders);
		JsonNode response = JsonNodeFactory.instance.objectNode();
		ResponseEntity<JsonNode> mockResponse= new ResponseEntity<>(response,HttpStatus.OK);
		Mockito.when(restTemplate.exchange(providerRulesDroolsUrl,
				HttpMethod.POST, request, JsonNode.class)).thenReturn(mockResponse);
		JsonNode responsePayload = restClientService.getProviderValidationRules(payload);
		JsonNode fallBackResponse = restClientService.getFallBackProviderValidationRules(payload);
		assertNotNull(responsePayload);
		assertNotNull(fallBackResponse);

	}

	public RulesInputPayload createIpPayload() {
		RulesInputPayload payload = new RulesInputPayload();
		
		payload.setInvocationSystem("01");
		payload.setMemberLineOfBusiness("CT0");
		payload.setMemberProcessingState("VA");
		payload.setMemberProductType("HMO");
		payload.setSystemType("O");
		payload.setMemberType("N");
		
		return payload;
	}

	public MDOPoolingRulesOutputPayload createOutPayload() {
		MDOPoolingRulesOutputPayload payload = new MDOPoolingRulesOutputPayload();
		MDOPoolingRules mdoRules = new MDOPoolingRules();
		mdoRules.setPoolSize(100);
		mdoRules.setMaxRadiusToPool(50);
		payload.setRules(mdoRules);
		payload.setResponseCode(ResponseCodes.SUCCESS);
		return payload;
	}
}