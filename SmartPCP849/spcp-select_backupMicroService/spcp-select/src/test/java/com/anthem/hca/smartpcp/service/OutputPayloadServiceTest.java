package com.anthem.hca.smartpcp.service;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

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
import com.anthem.hca.smartpcp.repository.ProviderInfoRepo;

public class OutputPayloadServiceTest {
	
	@InjectMocks
	private OutputPayloadService outputPayloadService;
	
	@Mock
	private ProviderInfoRepo providerRepo;
	
	
	
	@Mock
	private AsyncService asyncService;
	
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
		OutputPayload out = outputPayloadService.createErrorPayload("Invalid", ResponseCodes.OTHER_EXCEPTIONS, member, 1);
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
		OutputPayload out = outputPayloadService.createErrorPayload("Invalid", ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		assertEquals("Invalid", out.getStatus().getService().getErrorText());
		assertEquals(ResponseCodes.OTHER_EXCEPTIONS, out.getStatus().getService().getErrorCode());
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals(Constants.OUTPUT_UNSUCCESSFUL, out.getStatus().getService().getStatus());
		
	}

	@Test
	public void testCreateSuccessPaylod1() {
		
		Member member = createMember();
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABC123");
		pcp.setDummyPCP(Constants.FALSE);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		Mockito.when(providerRepo.getAssignedPcp(any(String.class),any(String.class))).thenReturn(pcp);
		OutputPayload out = outputPayloadService.createSuccessPaylod(Constants.REPORTING_CODE_MDO,Constants.REPORTING_TEXT_MDO ,member, "ABC123", false, "ASD123", 657);
		assertEquals(Constants.OUTPUT_SUCCESS, out.getStatus().getTransaction().getStatus());
		assertEquals("ABC123", out.getProvider().getProvPcpId());
		assertEquals(Constants.FALSE,out.getProvider().getDummyPCP());
		
	}
	
	@Test
	public void testCreateSuccessPaylod2() {
		
		Member member = createMember();
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABC123");
		pcp.setDummyPCP(Constants.FALSE);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		Mockito.when(providerRepo.getAssignedPcp(any(String.class),any(String.class))).thenReturn(pcp);
		OutputPayload out = outputPayloadService.createSuccessPaylod(Constants.REPORTING_CODE_AFFINITY,Constants.REPORTING_TEXT_AFFINITY, member, "ABC123", false, "ASD123", 657);
		assertEquals(Constants.OUTPUT_SUCCESS,  out.getStatus().getTransaction().getStatus());
		assertEquals("ABC123", out.getProvider().getProvPcpId());
		assertEquals(Constants.FALSE,out.getProvider().getDummyPCP());
		
	}
	
	@Test
	public void testCreateSuccessPaylod3() {
		
		Member member = createMember();
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABC123");
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(asyncService).asyncUpdate(any(String.class), any(String.class));
		Mockito.when(providerRepo.getAssignedPcp(any(String.class),any(String.class))).thenReturn(pcp);
		OutputPayload out = outputPayloadService.createSuccessPaylod(Constants.REPORTING_CODE_MDO,Constants.REPORTING_TEXT_MDO, member, "ABC123", true, "ASD123", 657);
		assertEquals(Constants.OUTPUT_SUCCESS,  out.getStatus().getTransaction().getStatus());
		assertEquals("ABC123", out.getProvider().getProvPcpId());
		assertEquals(Constants.TRUE,out.getProvider().getDummyPCP());
		
	}
	
	private Member createMember(){
		
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

}
