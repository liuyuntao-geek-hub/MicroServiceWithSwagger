package com.anthem.hca.smartpcp.audit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.anthem.hca.smartpcp.audit.payload.FlowOprPayload;
import com.anthem.hca.smartpcp.audit.service.OperationsAuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 		OperationsAuditControllerTest is used to test the OperationsAuditController
 *  
 * @author AF56159 
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OperationsAuditController.class, properties = { "spring.cloud.config.enabled:false" })
public class OperationsAuditControllerTest {

	@InjectMocks
	private OperationsAuditController operationsAuditController;

	@MockBean
	private OperationsAuditService operationsAuditService;

	@MockBean
	private ObjectMapper mapper;

	private MockMvc mockMvc;

	/**
	 *  method that initializes fields annotated with Mockito annotations
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(operationsAuditController).build();

	}

	/**
	 * 
	 * @throws Exception
	 * logFlowOprtn method from Controller class to execute the unit test case
	 */
	@Test
	public void logFlowOprtn() throws Exception {
		String flowTransaction = createFlowTransaction();

		this.mockMvc.perform(post("/logSPCPFlowOprtn").contentType(MediaType.APPLICATION_JSON).content(flowTransaction))
				.andExpect(status().isOk());

	}

	/**
	 * 
	 * @return flowOprPayload
	 * @throws JsonProcessingException
	 * createFlowTransaction method is used to set the dummy data in to flowOprPayload object to test the control flow
	 */
	private String createFlowTransaction() throws JsonProcessingException {

		FlowOprPayload flowOprPayload = new FlowOprPayload();
		flowOprPayload.setTraceId("34547434934983347");
		flowOprPayload.setServiceName("Affinity");
		flowOprPayload.setOperationStatus("SUCCESS");
		flowOprPayload.setOperationOutput("PCP_ID found for given member.");
		flowOprPayload.setResponseCode("200");
		flowOprPayload.setResponseMessage("Assigned the PCP_ID successully");

		mapper = new ObjectMapper();
		String flowRequest = mapper.writeValueAsString(flowOprPayload);

		return flowRequest;
	}

}
