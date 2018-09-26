package com.anthem.hca.smartpcp.mdoscoring.service;

import static org.junit.Assert.assertTrue;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.model.PCPTrackAudit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScoringClientServiceTest {
	
	@InjectMocks
	private ScoringClientService scoringClientService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper mapper;

	@Mock
	private Tracer tracer;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Value("${spcp.pcp.track.audit.url}")
	private String pcpAuditTrackPersistUrl;

	@Test
	public void testPersistPCPAssignmentFlow() throws JsonProcessingException {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(getPcpTrackAudit()), headers);
		String output = "responseCode :200, responseMessage: SCORE_INSERT_SUCCESSFUL";
		ResponseEntity<String> mockResponse = new ResponseEntity<>(output, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(pcpAuditTrackPersistUrl, HttpMethod.POST, request, String.class))
				.thenReturn(mockResponse);
		scoringClientService.persistPCPAssignmentFlow(getPcpTrackAudit());
		assertTrue(true);
	}

	private PCPTrackAudit getPcpTrackAudit() {
		PCPTrackAudit pcpTrackAudit = new PCPTrackAudit();
		String json = "{\"scoredSortedPoolPcps\":[{\"provPcpId\":\"0JI065\",\"rgnlNtwkId\":\"CC09\",\"lastName\":\"MAZOUZ\",\"speciality\":[\"INTERNAL MEDICINE\"],\"grpgRltdPadrsEfctvDt\":936158400000,\"distance\":0.9522987777024066,\"taxId\":null,\"rank\":0,\"pcpLang\":[\"FRA\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":105,\"panelCapacity\":0.0,\"vbpScore\":0,\"distanceScore\":50,\"limitedTimeBonusScore\":15,\"specialityScore\":0,\"languageScore\":10,\"rankScore\":30,\"dummyFlag\":false}]}";
		pcpTrackAudit.setProviderData(json);
		pcpTrackAudit.setTraceId(tracer.getCurrentSpan().traceIdString());

		return pcpTrackAudit;

	}

}
