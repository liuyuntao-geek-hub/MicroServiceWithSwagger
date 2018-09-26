package com.anthem.hca.smartpcp.drools.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.anthem.hca.smartpcp.drools.io.MDOPoolingProviderValidationRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesMatrix;
import com.anthem.hca.smartpcp.drools.preprocessor.RulesPreprocessor;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.service.OperationAuditFlowService;

public class MDOPoolingProviderValidationServiceTest {
	
	@InjectMocks
	private MDOPoolingProviderValidationService mdoPoolingProviderValidationService;
	
	@Mock
	private DroolsService drService;

	@Mock
	private RulesPreprocessor preProcessor;
	
	@Mock
	private OperationAuditFlowService operationFlowService;
	
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(mdoPoolingProviderValidationService, "approach", 1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetRules() throws Exception {
		Member m = createMember();
		mdoPoolingProviderValidationService.headers();
		Mockito.when(preProcessor.getData()).thenReturn(createData1(), createData3());
		doNothing().when(drService).fireRules(any(AbstractRules.class), any(Boolean.class));
		doNothing().when(operationFlowService).insertOperationFlowDrools(any(String.class));
		
		MDOPoolingProviderValidationRulesOutputPayload out = new MDOPoolingProviderValidationRulesOutputPayload();
		out.setRules(mdoPoolingProviderValidationService.getRules(m));
		
		assertEquals(m.getInvocationSystem(), out.getRules().getInvocationSystem());
		assertEquals("N", out.getRules().getRolloverFlag());
	}

	public Member createMember() {
		Member m = new Member();

		m.setInvocationSystem("06");
		m.setMemberProcessingState("NY");
		m.setMemberLineOfBusiness("CT0");
		m.setMemberProductType("ALL");
		m.setMemberType("N");
		m.setSystemType("O");

		return m;
	}

	public Map<String, RulesMatrix> createData1() {
		Map<String, RulesMatrix> data = new HashMap<>(5);
		RulesMatrix matrix = new RulesMatrix(5);
		data.put("MDO-POOLING", matrix);
		return data;
	}
	
	public Map<String, RulesMatrix> createData3() {
		Map<String, RulesMatrix> data = new HashMap<>(5);
		RulesMatrix matrix = new RulesMatrix(5);
		data.put("MDO", matrix);
		return data;
	}
	
}
