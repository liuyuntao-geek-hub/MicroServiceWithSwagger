package com.anthem.hca.smartpcp.service;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.Rules;
import com.anthem.hca.smartpcp.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.model.RulesEngineOutputPayload;
import com.anthem.hca.smartpcp.service.RestClientService;
import com.anthem.hca.smartpcp.service.SmartPCPService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SmartPCPServiceTest {
	
	@InjectMocks
	private SmartPCPService smartPCPService;

	@Mock
	private RulesEngineInputPayload payload;
	
	@Mock
	private RestClientService restClientService;
	
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getPCP() throws JsonProcessingException{
		Member member=newMember();
		RulesEngineOutputPayload response= createOutPayload();
				
		Mockito.when(restClientService.getInvocationOrder(payload)).thenReturn(response);
		OutputPayload output = smartPCPService.getPCP(member,"123");
		//assertEquals(700, output.getResponseCode());
		//assertEquals(700, output.getResponseCode());
		assertEquals("Service Not Available", output.getResponseMessage());
			
	}
	
	public Member newMember(){
		Member member=new Member();
		member.setLname("Kothuru");
		member.setFname("Rohit");
		member.setGender("M");
		return member;
	}
	
	public RulesEngineOutputPayload createOutPayload(){
		RulesEngineOutputPayload payload=new RulesEngineOutputPayload();
		Rules rules=new Rules();
		rules.setDrivingDistance(30);;
		rules.setMarket("Market");
		rules.setInvocationOrder("AM");
		payload.setRules(rules);
		payload.setResponseCode(700);
		payload.setResponseMessage("Service Not Available");
		return payload;
	}
}

