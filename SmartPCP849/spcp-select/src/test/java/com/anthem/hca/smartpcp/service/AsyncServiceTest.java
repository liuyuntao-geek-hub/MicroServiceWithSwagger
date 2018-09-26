package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.repository.MemberHistRepo;
import com.anthem.hca.smartpcp.repository.PanelCapacityRepo;
import com.anthem.hca.smartpcp.repository.ProviderHistRepo;

public class AsyncServiceTest {
	
	@InjectMocks
	private AsyncService asyncService;
	
	@Mock
	private ProviderHistRepo providerHistRepo;

	@Mock
	private MemberHistRepo memberHistRepo;
	
	@Mock
	private PanelCapacityRepo panelCapacityRepo;

	@Mock
	private Tracer tracer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testAsyncUpdate() {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		doNothing().when(panelCapacityRepo).updatePanelCapacity(any(String.class), any(String.class));
		Mockito.when(memberHistRepo.insertMember(any(Member.class), any(String.class),any(java.sql.Timestamp.class))).thenReturn(1);
		doNothing().when(providerHistRepo).insertPCP(any(String.class),any(String.class), any(String.class));
		asyncService.asyncUpdate( "ASD", "ASD");
		assertTrue(true);
	}
	
	@Test
	public void testAsyncMemberUpdate() {
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
		Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
		Mockito.when(memberHistRepo.insertMember(any(Member.class), any(String.class),any(java.sql.Timestamp.class))).thenReturn(1);
		asyncService.asyncMemberUpdate(createMember(), new Timestamp(new java.util.Date().getTime()));
		assertTrue(true);
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