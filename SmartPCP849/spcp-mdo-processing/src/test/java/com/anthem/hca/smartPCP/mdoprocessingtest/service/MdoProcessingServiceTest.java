package com.anthem.hca.smartPCP.mdoprocessingtest.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.anthem.hca.smartpcp.mdoprocessing.model.DroolsInputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOPoolingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
import com.anthem.hca.smartpcp.mdoprocessing.model.PCP;
import com.anthem.hca.smartpcp.mdoprocessing.model.TransactionFlowPayload;
import com.anthem.hca.smartpcp.mdoprocessing.service.AsyncClientService;
import com.anthem.hca.smartpcp.mdoprocessing.service.ProcessingService;
import com.anthem.hca.smartpcp.mdoprocessing.service.RestClientService;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ProcessingHelper;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.anthem.hca.smartpcp.mdoprocessing.validator.DateValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)

public class MdoProcessingServiceTest {

	@InjectMocks
	private ProcessingService service;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private ObjectMapper mapper;

	@MockBean
	private HttpHeaders headers;

	@Mock
	private RestClientService clientService;

	@Mock
	private AsyncClientService asyncService;
	
	@Mock
	private DateValidator dateValidator;
	@Mock
	private ProcessingHelper helper;
	
	@MockBean
	private Tracer tracer;
	
	
	

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPCPList() throws Exception {

		Member member = createMember();
		MDOPoolingOutputPayload mdoPoolingResponse = createMdoPoolingResponse();
		MDOProcessingOutputPayload mdoProcessingresponse = createMdoProcessingResponse();
		MDOProcessingOutputPayload mdoProcessingDummyOutput=createDummyResponse();

		JsonNode droolsResponse = createDroolsResponse();
		JsonNode scoringRules= droolsResponse.get(Constants.RULES);
		
		List<PCP> pcpList=mdoPoolingResponse.getPcps();
		Mockito.when(dateValidator.checkFuture(member.getMemberDob())).thenReturn(false);
		Mockito.when(clientService.getPCPList(member)).thenReturn(mdoPoolingResponse);
		
		Mockito.when(helper.createDummyPayload(mdoPoolingResponse.getPcps().get(0).getProvPcpId())).thenReturn(mdoProcessingDummyOutput);
		
		Mockito.when(clientService.getRules(any(DroolsInputPayload.class))).thenReturn(droolsResponse);
		Mockito.when(helper.getAssignedPCP(scoringRules, member, pcpList)).thenReturn(mdoProcessingresponse);
		Span spanMock = Span.builder().name("mock").traceId(123456).spanId(123456).build();
        Mockito.when(tracer.getCurrentSpan()).thenReturn(spanMock);
        doNothing().when(asyncService).insertOperationFlow(any(TransactionFlowPayload.class));

		MDOProcessingOutputPayload outputPayload = service.getPCP(member);
		
		assertEquals("0PN161", outputPayload.getPcpId());

	}

	private MDOProcessingOutputPayload createDummyResponse() {
		MDOProcessingOutputPayload mdoProcessingOutputPayload=new MDOProcessingOutputPayload();
		mdoProcessingOutputPayload.setPcpId("DUM111");
		mdoProcessingOutputPayload.setDummyFlag(true);
		mdoProcessingOutputPayload.setResponseCode(ResponseCodes.SUCCESS);
		mdoProcessingOutputPayload.setResponseMessage("Assigned the Dummy PCP");
		return mdoProcessingOutputPayload;
	
	}

	public Member createMember() {

		Member membObj = new Member();

		/*membObj.setMemberFirstName("John");
		membObj.setMemberLastName("Chavez");*/
		membObj.setMemberDob("1991-01-23");
		return membObj;
	}

	public MDOProcessingOutputPayload createMdoProcessingResponse() {

		MDOProcessingOutputPayload response = new MDOProcessingOutputPayload();
		
		response.setPcpId("0PN161");
		response.setDummyFlag(false);
		response.setMdoScore(225);
		response.setPcpNtwrkId("CAHCLA00");
		response.setDrivingDistance(3.0);
		response.setResponseCode("200");
		response.setResponseMessage("SUCCESS");
		return response;
	}

	public MDOPoolingOutputPayload createMdoPoolingResponse() {

		MDOPoolingOutputPayload response = new MDOPoolingOutputPayload();
		List<PCP> pcpList = new ArrayList<PCP>();
		PCP p1 = new PCP();
		p1.setProvPcpId("0PN161");
		PCP p2 = new PCP();
		p2.setProvPcpId("0UR097");
		PCP p3 = new PCP();
		p3.setProvPcpId("0UR189");
		pcpList.add(p1);
		response.setPcps(pcpList);
		response.setResponseCode("200");
		response.setResponseMessage("SUCCESS");
		response.setDummyFlag(false);
		return response;
	}
	
	public JsonNode createDroolsResponse() throws JSONException, JsonProcessingException, IOException{
		
		
		String output="{\"rules\":{\"market\":\"ALL\",\"lob\":\"ALL\",\"product\":\"ALL\",\"assignmentType\":\"ALL\",\"assignmentMethod\":\"ALL\",\"fallbackRequired\":true,\"mdorankScoreList\":[{\"key\":5,\"value\":75},{\"key\":4,\"value\":65},{\"key\":3,\"value\":30},{\"key\":2,\"value\":20},{\"key\":1,\"value\":10},{\"key\":0,\"value\":30}],\"proximityScoreList\":[{\"key\":\"0-5\",\"value\":50},{\"key\":\"5-10\",\"value\":40},{\"key\":\"10-15\",\"value\":30},{\"key\":\"15-20\",\"value\":20},{\"key\":\"20+\",\"value\":0}],\"languageMatchScoreList\":[{\"key\":true,\"value\":10},{\"key\":false,\"value\":0}],\"ageSpecialtyMatchScoreList\":[{\"key\":true,\"value\":10},{\"key\":false,\"value\":0}],\"vbaparticipationScoreList\":[{\"key\":true,\"value\":20},{\"key\":false,\"value\":0}],\"limitedTime\":365,\"panelCapacityPercent\":25,\"limitedTimeScore\":15,\"restrictedAgeSpecialties\":[\"Pediatrics\",\"Geriatrics\"],\"mdoRank\":0,\"mdoRankScore\":[],\"proximity\":null,\"proximityScore\":[],\"languageMatch\":false,\"languageMatchScore\":[],\"ageSpecialtyMatch\":false,\"ageSpecialtyMatchScore\":[],\"restrictedSpecialties\":null,\"vbaParticipation\":false,\"vbaParticipationScore\":[],\"panelCapacity\":0},\"responseCode\":200,\"responseMessage\":\"SUCCESS\"}";
		
		ObjectMapper mapper=new ObjectMapper();
		JsonNode droolsResponse=mapper.readTree(output);
		return droolsResponse;
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemp = new RestTemplate();
		return restTemp;
	}

}