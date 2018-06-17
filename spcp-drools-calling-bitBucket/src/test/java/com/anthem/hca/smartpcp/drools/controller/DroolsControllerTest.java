package com.anthem.hca.smartpcp.drools.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.google.gson.Gson;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.DroolsService;
import com.anthem.hca.smartpcp.drools.service.SmartPCPService;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import com.anthem.hca.smartpcp.drools.controller.DroolsController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(DroolsController.class)
public class DroolsControllerTest {

    @InjectMocks
    private DroolsController droolsController;

	@Autowired
    private MockMvc mockMvc;

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

	@Test
	public void testGetRequestForSmartPCP() throws Exception {
		mockMvc
			.perform(post("/rules/smartpcp")
			.contentType(MediaType.APPLICATION_JSON)
			.content(getPayload())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void testGetRequestForProviderValidationAffinity() throws Exception {
		mockMvc
			.perform(post("/rules/provider-validation/affinity")
			.contentType(MediaType.APPLICATION_JSON)
			.content(getPayload())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void testGetRequestForProviderValidationMDO() throws Exception {
		mockMvc
			.perform(post("/rules/provider-validation/mdo")
			.contentType(MediaType.APPLICATION_JSON)
			.content(getPayload())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void testGetRequestForMDOPooling() throws Exception {
		mockMvc
			.perform(post("/rules/mdo/pooling")
			.contentType(MediaType.APPLICATION_JSON)
			.content(getPayload())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void testGetRequestForMDOScoring() throws Exception {
		mockMvc
			.perform(post("/rules/mdo/scoring")
			.contentType(MediaType.APPLICATION_JSON)
			.content(getPayload())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	public String getPayload() {
		Member member = new Member();
		member.setMemberLineOfBusiness("Commercial");
		member.setMemberSourceSystem("ISG");
		member.setMemberProcessingState("ALL");
		member.setMemberISGProductGroup("ALL");
		member.setMemberWGSGroup("ALL");
		member.setMemberType("N");
		member.setSystemType("B");

		RulesInputPayload ipPayload = new RulesInputPayload();
		ipPayload.setMember(member);

		return new Gson().toJson(ipPayload);
	}

}
