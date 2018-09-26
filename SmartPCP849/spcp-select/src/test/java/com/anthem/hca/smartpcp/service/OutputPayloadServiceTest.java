package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.PCP;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.repository.ProviderInfoRepo;

public class OutputPayloadServiceTest {

	@InjectMocks
	private OutputPayloadService outputPayloadService;

	@Mock
	private AsyncService asyncService;

	@Mock
	private ProviderInfoRepo providerInfoRepo;

	@Mock
	private Tracer tracer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateErrorPaylod1() {

		Member member = createMember();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		OutputPayload out = outputPayloadService.createErrorPayload("Invalid", ResponseCodes.OTHER_EXCEPTIONS, member,
				1);
		assertEquals("Invalid", out.getStatus().getService().getErrorText());
		assertEquals(ResponseCodes.OTHER_EXCEPTIONS, out.getStatus().getService().getErrorCode());
		assertEquals(Constants.OUTPUT_UNSUCCESSFUL, out.getStatus().getService().getStatus());
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());

	}

	@Test
	public void testCreateErrorPaylod2() {

		Member member = createMember();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		String errorText = "Sample error textextending 37 characters length,it should return first 37 characters only.";
		OutputPayload out = outputPayloadService.createErrorPayload(errorText, ResponseCodes.OTHER_EXCEPTIONS, member,
				1);
		assertEquals(errorText.substring(0, 37), out.getStatus().getService().getErrorText());
		assertEquals(ResponseCodes.OTHER_EXCEPTIONS, out.getStatus().getService().getErrorCode());
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals(Constants.OUTPUT_UNSUCCESSFUL, out.getStatus().getService().getStatus());

	}

	@Test
	public void testCreateErrorPaylod3() {

		Member member = createMember();
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		OutputPayload out = outputPayloadService.createErrorPayload("Invalid", ResponseCodes.SERVICE_NOT_AVAILABLE,
				member, 1);
		assertEquals("Invalid", out.getStatus().getService().getErrorText());
		assertEquals(ResponseCodes.SERVICE_NOT_AVAILABLE, out.getStatus().getService().getErrorCode());
		assertEquals(Constants.OUTPUT_UNSUCCESSFUL, out.getStatus().getTransaction().getStatus());
		assertEquals(Constants.OUTPUT_UNSUCCESSFUL, out.getStatus().getService().getStatus());

	}

	@Test
	public void testcreateSuccessPaylodMDO() {

		Member member = createMember();
		ScoringProvider scoringProvider = scoringProviderPCP();
		PCP pcp = new PCP();
		pcp.setProvPcpId("MDO1234");
		pcp.setNetworkId("TEST01");
		pcp.setDummyPCP(Constants.FALSE);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(providerInfoRepo.getAssignedPcp(any(String.class), any(String.class)))
				.thenReturn(pcp);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		OutputPayload out = outputPayloadService.createSuccessPaylodMDO(member, scoringProvider.isDummyFlag(), scoringProvider);
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals("MDO1234", out.getProvider().getProvPcpId());

	}
	
	@Test
	public void testcreateSuccessPaylodMDO2() {

		Member member = createMember();
		ScoringProvider scoringProvider = scoringProviderPCP2();
		PCP pcp = new PCP();
		pcp.setProvPcpId("MDO1234");
		pcp.setContractCode("CC02");
		pcp.setDummyPCP(Constants.TRUE);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(providerInfoRepo.getAssignedPcp(any(String.class), any(String.class)))
				.thenReturn(pcp);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		OutputPayload out = outputPayloadService.createSuccessPaylodMDO(member,scoringProvider.isDummyFlag(), scoringProvider);
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals("MDO1234", out.getProvider().getProvPcpId());

	}


	@Test
	public void testcreateSuccessPaylodAffinity() {

		Member member = createMember();
		Provider pcp = new Provider();
		pcp.setProvPcpId("AFNTY1234");
		pcp.setRgnlNtwkId("12345");
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		OutputPayload out = outputPayloadService.createSuccessPaylodAffinity(member, pcp);
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals("AFNTY1234", out.getProvider().getProvPcpId());
	}
	
	@Test
	public void testcreateSuccessPaylodAffinity2() {

		Member member = createMember();
		Provider pcp = new Provider();
		pcp.setProvPcpId("AFNTY1234");
		pcp.setRgnlNtwkId("CC02");
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		OutputPayload out = outputPayloadService.createSuccessPaylodAffinity(member, pcp);
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals("AFNTY1234", out.getProvider().getProvPcpId());
	}

	private Member createMember() {

		Member member = new Member();
		member.setMemberGroupId(123);
		member.setMemberSubGroupId(123);
		member.setMemberEid("ASD");
		member.setMemberSequenceNumber("ASD123");
		Address address = new Address();
		address.setAddressLine1("STREET");
		address.setCity("Sacramento");
		address.setState("CA");
		address.setZipCode("1234");
		address.setZipFour("1234");
		member.setAddress(address);
		return member;

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
	
	public ScoringProvider scoringProviderPCP2() {

		ScoringProvider pcp = new ScoringProvider();
		pcp.setProvPcpId("MDO1234");
		pcp.setRgnlNtwkId("HARSH01");
		pcp.setLastName("MDO_LAST");
		pcp.setDistance(7.0680473834351885);
		pcp.setDummyFlag(true);
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
}