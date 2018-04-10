package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.Rules;
import com.anthem.hca.smartpcp.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.model.RulesEngineOutputPayload;
import com.anthem.hca.smartpcp.service.RestClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestClientServiceTest {

	@InjectMocks
	private RestClientService restClientService;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private HttpHeaders headers;
	
	
	@Value("${drools.engine.url}")
    private String droolsEngineUrl;
	
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getInvocationOrder() throws JsonProcessingException{
		RulesEngineInputPayload payload=createIpPayload();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload),headers);
		RulesEngineOutputPayload response= createOutPayload();
		Mockito.when(restTemplate.postForObject(droolsEngineUrl, request , RulesEngineOutputPayload.class )).thenReturn(response);
		RulesEngineOutputPayload responsePayload =restClientService.getInvocationOrder(payload);
		assertEquals("AM", responsePayload.getRules().getInvocationOrder());
	}
	
	public RulesEngineInputPayload createIpPayload(){
		RulesEngineInputPayload payload=new RulesEngineInputPayload();
		Member member=new Member();
		
		member.setAddress(null);
		member.setFname("Rohit");
		member.setLname("Kothuru");
		payload.setMember(member);
		payload.setRequestFor("abcd");
		return payload;
	}
	
	public RulesEngineOutputPayload createOutPayload(){
		RulesEngineOutputPayload payload=new RulesEngineOutputPayload();
		Rules rules=new Rules();
		rules.setDrivingDistance(30);;
		rules.setMarket("Market");
		rules.setInvocationOrder("AM");
		payload.setRules(rules);
		return payload;
	}
}
