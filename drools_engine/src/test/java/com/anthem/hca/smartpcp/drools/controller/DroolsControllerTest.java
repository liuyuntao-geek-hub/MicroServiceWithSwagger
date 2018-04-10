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
import com.anthem.hca.smartpcp.drools.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.drools.service.DroolsRestClientService;
import com.anthem.hca.smartpcp.drools.service.DroolsService;
import com.anthem.hca.smartpcp.drools.service.RulesEngineService;
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
    private RulesEngineService rService;

    @MockBean
    private DroolsService  droolsService;
    
    @MockBean
    private DroolsRestClientService tservice;

	/**
	 * @throws Exception
	 * will test the connectivity with Drools controller with end point smartPCPSelect
	 */
	@Test
	public void controllerTest() throws Exception {

		mockMvc
			.perform(post("/smartPCPSelect")
			.contentType(MediaType.APPLICATION_JSON)
			.content(getPayload())
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	public String getPayload() {
		RulesEngineInputPayload ipPayload = new RulesEngineInputPayload();

		Member member = new Member();
		member.setMarket("ALL");
		member.setLob("Commercial");
		member.setProduct("ALL");
		member.setAssignmentType("New");
		member.setAssignmentMethod("Batch");
		ipPayload.setRequestFor("SMARTPCP");
		ipPayload.setMember(member);

		return new Gson().toJson(ipPayload);
	}

}
