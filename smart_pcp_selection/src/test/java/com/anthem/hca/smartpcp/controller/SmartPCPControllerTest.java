package com.anthem.hca.smartpcp.controller;


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

import com.anthem.hca.smartpcp.controller.SmartPCPController;
import com.anthem.hca.smartpcp.model.ErrorResponse;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.service.SmartPCPService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= SmartPCPController.class,
properties = { "spring.cloud.config.enabled:false"})
public class SmartPCPControllerTest {

	
	
	@InjectMocks
    private SmartPCPController smartPCPController;	
	
	 @MockBean
	private SmartPCPService service;
		
	@MockBean
	 private ObjectMapper mapper;
		
	@MockBean
	private ErrorResponse errorResponse;
	
	 private MockMvc mockMvc;
	    
	    @Before
	    public void setup() {
	       
	    	MockitoAnnotations.initMocks(this);
	    	 
	    	mockMvc = MockMvcBuilders
	                .standaloneSetup(smartPCPController).build();
	    	
	   } 
	    
	  @Test
	  public void getPCP() throws Exception{
		  String member =createMember();
		  this.mockMvc.perform(post("/smartSelection").contentType(MediaType.APPLICATION_JSON).content(member))
	        .andExpect(status().isOk());
	  }
	  
	  public String createMember() throws JsonProcessingException{
			Member member=new Member();
			member.setLname("Kothuru");
			member.setFname("Rohit");
			member.setGender("M");
			member.setMarket("CA");
			member.setSsn("1234");
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter(); 
			String rqst=ow.writeValueAsString(member);
			return rqst ;
		}
	  
	  
}
