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

import com.anthem.hca.smartpcp.helper.PayloadHelper;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.AffinityInputPayload;
import com.anthem.hca.smartpcp.model.AffinityOutPayload;
import com.anthem.hca.smartpcp.model.MDOInputPayload;
import com.anthem.hca.smartpcp.model.MDOOutputPayload;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.RulesInputPayload;
import com.anthem.hca.smartpcp.model.SmartPCPRules;
import com.anthem.hca.smartpcp.model.SmartPCPRulesOutputPayload;
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
	
	public PayloadHelper payloadHelper;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
		payloadHelper = new PayloadHelper();
	}

	//@Test
	public void testGetInvocationOrder() throws JsonProcessingException {
		RulesInputPayload payload = createIpPayload();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
		SmartPCPRulesOutputPayload response = createOutPayload();
		Mockito.when(restTemplate.postForObject(droolsEngineUrl, request, SmartPCPRulesOutputPayload.class))
				.thenReturn(response);
		SmartPCPRulesOutputPayload responsePayload = restClientService.getInvocationOrder(payload);
		assertEquals("AM", responsePayload.getRules().getInvocationOrder());
	}

	//@Test
	public void testGetPCPAffinity() throws JsonProcessingException {
		
		AffinityInputPayload payload = payloadHelper.createAffinityPayload(createMember());
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
		AffinityOutPayload response = new AffinityOutPayload();
		response.setPcpId("AB78690");
		Mockito.when(restTemplate.postForObject(droolsEngineUrl, request, AffinityOutPayload.class))
				.thenReturn(response);
		AffinityOutPayload responsePayload = restClientService.getPCPAffinity(payload);
		assertEquals("AB78690", responsePayload.getPcpId());
	}

	//@Test
	public void testGetPCPMDO() throws JsonProcessingException {
		MDOInputPayload payload = payloadHelper.createMDOPayload(createMember());
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(payload), headers);
		MDOOutputPayload response = new MDOOutputPayload();
		response.setPcpId("AB78690");
		response.setMdoScore(890);
		Mockito.when(restTemplate.postForObject(droolsEngineUrl, request, MDOOutputPayload.class)).thenReturn(response);
		MDOOutputPayload responsePayload = restClientService.getPCPMDO(payload);
		assertEquals("AB78690", responsePayload.getPcpId());
	}

	@Test
	public void testGetGeocodes() throws JsonProcessingException {

		String response = "Bing Response";
		Mockito.when(restTemplate.getForObject("url", String.class)).thenReturn(response);
		String responsePayload = restClientService.getGeocodes("url");
		assertEquals("Bing Response", responsePayload);
	}

	public RulesInputPayload createIpPayload() {
		RulesInputPayload payload = new RulesInputPayload();
		Member member = new Member();
		member.setInvocationSystem("IS");
		return payload;
	}

	public SmartPCPRulesOutputPayload createOutPayload() {
		SmartPCPRulesOutputPayload payload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setMarket("Market");
		rules.setInvocationOrder("AM");
		payload.setRules(rules);
		return payload;
	}
	
	public Member createMember() {

		Member member = new Member();
		member.setInvocationSystem("01");
		member.setMemberLineOfBusiness("ASDFG123");
		Address add = new Address();
		add.setLatitude(42.4094389380575);
		add.setLongitude(-71.2310786515669);
		member.setAddress(add);
		return member;
	}
}
