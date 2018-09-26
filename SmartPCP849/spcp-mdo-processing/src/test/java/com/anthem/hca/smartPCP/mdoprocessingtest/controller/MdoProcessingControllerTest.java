//package com.anthem.hca.smartPCP.mdoprocessingtest.controller;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.apache.log4j.LogManager;
//import org.apache.log4j.LOGGER;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.anthem.hca.smartpcp.mdoprocessing.controller.MDOProcessingController;
//import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
//import com.anthem.hca.smartpcp.mdoprocessing.service.ProcessingService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes =  MDOProcessingController.class, properties = { "spring.cloud.config.enabled:false" })
//
//public class MdoProcessingControllerTest {
//
//	@InjectMocks
//	private MDOProcessingController mdoProcessingController;
//
//	@MockBean
//	private ProcessingService service;
//
//	@MockBean
//	private ObjectMapper mapper;
//
//
//	private MockMvc mockMvc;
//
//	private static String memberObj;
//
//	private static final Logger  LOGGER = LogManager.getLogger(MdoProcessingControllerTest.class);
//
//	@BeforeClass
//	public static void init() throws Exception{
//		memberObj = createMember();
//	}
//
//	@Before
//	public void setup() {
//		LOGGER.info("Testing MDOProcessingController");
//		MockitoAnnotations.initMocks(this);
//		mockMvc = MockMvcBuilders.standaloneSetup(mdoProcessingController).build();
//	}
//
//	@Test
//	public void testgetPCP() throws Exception {
//		
//		this.mockMvc.perform(post("/MDOProcessing").contentType(MediaType.APPLICATION_JSON).content(memberObj))
//        .andExpect(status().isOk());
//	}
//
//	public static String createMember() throws Exception {
//
//		Member membObj = new Member();
//
//		membObj.setMemberFirstName("John");
//		membObj.setMemberLastName("Andrew");
//		ObjectMapper mapper = new ObjectMapper();
//		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter(); 
//		String rqst=ow.writeValueAsString(membObj);
//
//		return rqst;
//	}
//
//}
