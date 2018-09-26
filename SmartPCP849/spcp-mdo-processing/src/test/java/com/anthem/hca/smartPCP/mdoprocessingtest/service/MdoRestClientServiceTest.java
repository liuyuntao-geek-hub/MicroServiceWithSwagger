/*package com.anthem.hca.smartPCP.mdoprocessingtest.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOScoringRulesOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
import com.anthem.hca.smartpcp.mdoprocessing.model.PCP;
import com.anthem.hca.smartpcp.mdoprocessing.model.Rules;
import com.anthem.hca.smartpcp.mdoprocessing.service.RestClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MdoRestClientServiceTest {

	@InjectMocks
	private RestClientService restClientService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private HttpHeaders headers;

	@Value("${mdo.pooling.url}")
	private String mdoPoolingUrl;

	@Value("${droolsRuleEngine.service.url}")
	private String droolsRuleEngineUrl;

	@Value("${mdo.scoring.url}")
	private String mdoScoringUrl;
	
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPCPList() throws JsonProcessingException {
		Member member = createMember();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(member), headers);
		MDOPoolingOutputPayload response = createMdoPoolingResponse();
		Mockito.when(restTemplate.postForObject(mdoPoolingUrl, request, MDOPoolingOutputPayload.class))
				.thenReturn(response);
		MDOPoolingOutputPayload mdoPoolingResponsePayload = restClientService.getPCPList(member);
		System.out.println(
				"Message from restClientTestClass. Size of PCP list is " + mdoPoolingResponsePayload.getPcps().size());
		assertEquals("123", mdoPoolingResponsePayload.getPcps().get(0).getProvPcpId());
		assertEquals("MDOPoolingServiceOutputPayload", mdoPoolingResponsePayload.getResponseMessage());
	}

	@Test
	public void testGetRules() throws JsonProcessingException {
		DroolsInputPayload droolsRequest = createDroolsRequest();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(droolsRequest), headers);
		MDOScoringRulesOutputPayload response = createDroolsResponse();
		Mockito.when(restTemplate.postForObject(droolsRuleEngineUrl, request, MDOScoringRulesOutputPayload.class))
				.thenReturn(response);
		MDOScoringRulesOutputPayload droolsResponse = restClientService.getRules(droolsRequest);
		System.out.println("Message from restClientTestClass. Drools Response Code is  "
				+ droolsResponse.getResponseCode() + " - " + droolsResponse.getResponseMessage());
		
		assertEquals("SUCCESS", droolsResponse.getResponseMessage());
	}
	
	@Test
	public void testGetPCPafterScoring() throws JsonProcessingException {
		MDOScoringInputPayload mdoScoringRequest = createMdoScoringRequest();
		HttpEntity<String> request = new HttpEntity<String>(mapper.writeValueAsString(mdoScoringRequest), headers);
		MDOScoringOutputPayload response = createMdoScoringResponse();
		Mockito.when(restTemplate.postForObject(mdoScoringUrl, request, MDOScoringOutputPayload.class))
				.thenReturn(response);
		MDOScoringOutputPayload mdoScoringResponse = restClientService.getPCPafterScoring(mdoScoringRequest);
		System.out.println("Message from restClientTestClass. MDO Scoring Response Code is  "
				+ mdoScoringResponse.getResponseCode() + " - " + mdoScoringResponse.getResponseMessage());
		assertEquals("200", mdoScoringResponse.getResponseCode());
		assertEquals("SUCCESS", mdoScoringResponse.getResponseMessage());
	}

	public Member createMember() {

		Member membObj = new Member();
		membObj.setMemberFirstName("John");
		membObj.setMemberLastName("Andrew");
		return membObj;
	}

	public MDOPoolingOutputPayload createMdoPoolingResponse() {

		MDOPoolingOutputPayload response = new MDOPoolingOutputPayload();
		List<PCP> pcpList = new ArrayList<PCP>();
		PCP p1 = new PCP();
		p1.setProvPcpId("123");
		pcpList.add(p1);
		response.setPcps(pcpList);
		response.setResponseCode("999");
		response.setResponseMessage("MDOPoolingServiceOutputPayload");
		return response;
	}

	public DroolsInputPayload createDroolsRequest() {

		DroolsInputPayload droolsReq = new DroolsInputPayload();
		droolsReq.setMember(createMember());
		return droolsReq;

	}

	public MDOScoringRulesOutputPayload createDroolsResponse() {

		MDOScoringRulesOutputPayload droolsResponse = new MDOScoringRulesOutputPayload();
		droolsResponse.setResponseCode(200);
		droolsResponse.setResponseMessage("SUCCESS");
		return droolsResponse;
	}
	
	public MDOScoringInputPayload createMdoScoringRequest() {
		
		MDOScoringInputPayload mdoScoringRequest = new MDOScoringInputPayload();
		mdoScoringRequest.setMember(createMember());
		mdoScoringRequest.setRules(new Rules());
		return mdoScoringRequest;
	}
	
	public MDOScoringOutputPayload createMdoScoringResponse() {

		MDOScoringOutputPayload mdoScoringResponse = new MDOScoringOutputPayload();
		mdoScoringResponse.setResponseCode("200");
		mdoScoringResponse.setResponseMessage("SUCCESS");
		return mdoScoringResponse;
	}
}*/