package com.anthem.hca.smartpcp.audit.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.audit.dao.FlowOprDao;
import com.anthem.hca.smartpcp.audit.model.FlowOprModel;
import com.anthem.hca.smartpcp.audit.payload.FlowOprPayload;;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		OperationsAuditServiceTest is used to test the OperationsAuditService
 *  
 * @author AF56159 
 */

public class OperationsAuditServiceTest {

	@InjectMocks
	private OperationsAuditService operationsAuditService;

	@Mock
	private FlowOprDao flowOprDao;

	/**
	 * method to set the dummy data in to flowOprPayload object to test the control flow
	 */
	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * testLogFlowOprtn method to set the dummy data in to flowOprPayload object to run the unit test cases 
	 */
	@Test
	public void testLogFlowOprtn() {

		FlowOprPayload flowOprPayload = new FlowOprPayload();

		flowOprPayload.setTraceId("23134");
		flowOprPayload.setOperationStatus("SUCCESS");
		flowOprPayload.setResponseCode("200");
		flowOprPayload.setResponseMessage("SUCCESS");
		flowOprPayload.setOperationOutput("P3910");
		flowOprPayload.setServiceName("spcp-affinity");

		operationsAuditService.logFlowOprtn(flowOprPayload);

		FlowOprModel flowOprModel = new FlowOprModel();

		flowOprModel.setTraceId("23134");
		flowOprModel.setOperationStatus("SUCCESS");
		flowOprModel.setResponseCode("200");
		flowOprModel.setResponseMessage("SUCCESS");
		flowOprModel.setOperationOutput("P3910");
		flowOprModel.setServiceName("spcp-affinity");

		Mockito.verify(flowOprDao, Mockito.times(1)).logFlowOprtn(flowOprModel);

	}

}
