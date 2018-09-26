package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

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
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.PCP;
import com.anthem.hca.smartpcp.model.Reporting;
import com.anthem.hca.smartpcp.model.ServiceStatus;
import com.anthem.hca.smartpcp.model.Status;
import com.anthem.hca.smartpcp.model.TransactionStatus;
import com.anthem.hca.smartpcp.repository.OperationsAuditRepo;
import com.fasterxml.jackson.core.JsonProcessingException;

public class OperationAuditServiceTest {
	
	@InjectMocks
	private OperationAuditService operationAuditService;
	
	@Mock
	private Tracer tracer;
	
	@Mock
	private OperationsAuditRepo operationsAuditRepo;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInsertOperationFlow() {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(operationsAuditRepo.logSPCPSelectOpr(any(Member.class), any(String.class))).thenReturn(1);
		int result = operationAuditService.insertOperationFlow(createMember());
		assertEquals(1, result);
	}

	@Test
	public void testUpdateOperationFlow1() throws JsonProcessingException {
		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("AM");
		operationsAudit.setDrivingDistance(2.09);
		Status status = new Status();
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.OUTPUT_SUCCESS);
		status.setTransaction(tranStatus);
		Reporting reporting = new Reporting();
		reporting.setReportingCode("MDO");
		payload.setReporting(reporting);
		payload.setStatus(status);
		PCP pcp =new PCP();
		pcp.setProvPcpId("ABC123");
		payload.setProvider(pcp);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(operationsAuditRepo.updateSPCPSelectOpr(any(OperationsAuditUpdate.class),any(String.class))).thenReturn(1);
		int result = operationAuditService.updateOperationFlow(payload,operationsAudit,"HMO","CA");
		assertEquals(1,result);
	}
	
	@Test
	public void testUpdateOperationFlow2() throws JsonProcessingException {
		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("AM");
		operationsAudit.setDrivingDistance(2.09);
		Status status = new Status();
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.OUTPUT_SUCCESS);
		Reporting reporting = new Reporting();
		reporting.setReportingCode("MDO");
		payload.setReporting(reporting);
		status.setTransaction(tranStatus);
		payload.setStatus(status);
		PCP pcp =new PCP();
		pcp.setProvPcpId("ABC123");
		pcp.setPcpScore(678);
		pcp.setPcpRank(11);
		payload.setProvider(pcp);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(operationsAuditRepo.updateSPCPSelectOpr(any(OperationsAuditUpdate.class),any(String.class))).thenReturn(1);
		int result = operationAuditService.updateOperationFlow(payload,operationsAudit,"HMO","CA");
		assertEquals(1,result);
	}

	@Test
	public void testUpdateOperationFlow3() throws JsonProcessingException {
		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("AM");
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.OUTPUT_UNSUCCESSFUL);
		status.setTransaction(tranStatus);
		payload.setStatus(status);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(operationsAuditRepo.updateSPCPSelectOpr(any(OperationsAuditUpdate.class),any(String.class))).thenReturn(1);
		int result = operationAuditService.updateOperationFlow(payload,operationsAudit,"HMO","CA");
		assertEquals(1,result);
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
