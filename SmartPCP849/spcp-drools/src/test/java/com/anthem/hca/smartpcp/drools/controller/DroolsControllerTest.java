package com.anthem.hca.smartpcp.drools.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.DroolsService;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingService;
import com.anthem.hca.smartpcp.drools.service.MDOProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import com.anthem.hca.smartpcp.drools.service.SmartPCPService;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= DroolsControllerTest.class,
properties = { "spring.cloud.config.enabled:false"}) 
public class DroolsControllerTest {

    @InjectMocks
    private DroolsController droolsController;

    @MockBean
    private SmartPCPService spService;

    @MockBean
    private AffinityProviderValidationService aPvService;

    @MockBean
    private MDOProviderValidationService mPvService;

    @MockBean
    private MDOPoolingService mpService;

    @MockBean
    private MDOScoringService msService;

    @MockBean
    private DroolsService drService;

    @MockBean
    private DroolsRestClientService rcService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mockMvc = MockMvcBuilders
                .standaloneSetup(droolsController)
                .build();
   } 
    
	@Test
	public void testPostRequestForSmartPCP() throws Exception {
		mockMvc.perform(
			post("/rules/smartpcp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPayload())
				.accept(MediaType.APPLICATION_JSON)
		);
	}

	@Test
	public void testPostRequestForProviderValidationAffinity() throws Exception {
		mockMvc.perform(
			post("/rules/provider-validation/affinity")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPayload())
				.accept(MediaType.APPLICATION_JSON)
		);
	}

	@Test
	public void testPostRequestForProviderValidationMDO() throws Exception {
		mockMvc.perform(
			post("/rules/provider-validation/mdo")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPayload())
				.accept(MediaType.APPLICATION_JSON)
		);
	}

	@Test
	public void testPostRequestForMDOPooling() throws Exception {
		mockMvc.perform(
			post("/rules/mdo/pooling")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPayload())
				.accept(MediaType.APPLICATION_JSON)
		);
	}

	@Test
	public void testPostRequestForMDOScoring() throws Exception {
		mockMvc.perform(
			post("/rules/mdo/scoring")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPayload())
				.accept(MediaType.APPLICATION_JSON)
		);
	}

	public String getPayload() {
		Member m = new Member();
		m.setInvocationSystem("06");
		m.setMemberLineOfBusiness("CT0");
		m.setMemberProcessingState("NY");
		m.setMemberProductType("HMO");
		m.setMemberType("N");
		m.setSystemType("B");

		return new Gson().toJson(m);
	}

}
