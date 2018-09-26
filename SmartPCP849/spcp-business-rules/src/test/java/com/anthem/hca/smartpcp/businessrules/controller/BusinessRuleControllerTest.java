package com.anthem.hca.smartpcp.businessrules.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusinessRuleControllerTest.class)
public class BusinessRuleControllerTest {

	@InjectMocks
	private BusinessRuleController businessRuleController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(businessRuleController)
				.build();
	}

	@Test
	public void testGetFile() throws Exception {
		mockMvc.perform(get("/file/MDO-Dummy-PCP-Rules.xls"))
			   .andExpect(status().isOk())
			   .andExpect(content().contentType("application/octet-stream"));
	}

}
