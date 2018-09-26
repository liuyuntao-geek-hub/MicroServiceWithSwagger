package com.anthem.hca.smartpcp.track.audit.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;
import com.anthem.hca.smartpcp.track.audit.constants.ResponseCodes;
import com.anthem.hca.smartpcp.track.audit.payload.GetScoringExcelPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PCPScoringDataPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfoPayload;
import com.anthem.hca.smartpcp.track.audit.payload.ResponsePayload;
import com.anthem.hca.smartpcp.track.audit.service.PCPScoreAuditService;
import com.anthem.hca.smartpcp.track.audit.utils.DateUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= PCPScoreAuditControllerTest.class,
properties = { "spring.cloud.config.enabled:false"}) 
public class PCPScoreAuditControllerTest extends Mockito{

	@InjectMocks
	private PCPScoreAuditController pcpScoreAuditController;
	@MockBean
	PCPScoreAuditService pcpScoreAuditService;
	@MockBean
	DateUtility dateUtility;
	@MockBean
	private ObjectMapper mapper;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(pcpScoreAuditController)
				.build();
	} 

	@Test
	public void savePCPScoreDataPayloadTest() throws Exception {
		mockMvc.perform(
				post("/savePCPScoreData")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPCPScoringDataPayload())
				.accept(MediaType.APPLICATION_JSON)
				);
	}

	@Test
	public void savePCPScoreDataPositiveTest() throws Exception {

		PCPScoringDataPayload pcpScoreVerifyPayload = new PCPScoringDataPayload();
		pcpScoreVerifyPayload.setTraceId("TESTfdsfsfsdf3213123123");	
		pcpScoreVerifyPayload.setProviderData("{\"name\":\"Jane\"}");
		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.SUCCESS);
		responsePayload.setResponseMessage(ErrorMessages.SCORE_INSERT_SUCCESSFUL);

		Mockito.when(pcpScoreAuditService.savePCPScoreData(pcpScoreVerifyPayload)).thenReturn(responsePayload);

		ResponsePayload expectedResponsePayload = pcpScoreAuditController.savePCPScoreData(pcpScoreVerifyPayload);
		assertEquals(responsePayload.getResponseCode(),expectedResponsePayload.getResponseCode());
	}

	@Test
	public void savePCPScoreDataNegativeTest(){

		PCPScoringDataPayload pcpScoreVerifyPayload = new PCPScoringDataPayload();
		pcpScoreVerifyPayload.setTraceId("TESTfdsfsfsdf3213123123");	
		pcpScoreVerifyPayload.setProviderData("{\"name\":\"Jane\"}");
		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		responsePayload.setResponseMessage(ErrorMessages.SCORE_INSERT_ERR);

		Mockito.when(pcpScoreAuditService.savePCPScoreData(pcpScoreVerifyPayload)).thenReturn(responsePayload);

		ResponsePayload expectedResponsePayload = pcpScoreAuditController.savePCPScoreData(pcpScoreVerifyPayload);
		assertEquals(responsePayload.getResponseCode(),expectedResponsePayload.getResponseCode());
	}

	/*	@Test
	public void getPCPScoreDataAsJsonPayloadTest() throws Exception {

		mockMvc.perform(
				get("/getPCPScoreDataAsJson"))
		.andExpect(view().name("146e023797418ee9"))
		.andExpect(status().isOk());
	}
	 */	
	@Test
	public void getPCPScoreDataAsJsonTest() throws Exception {

		GetScoringExcelPayload scoringDataPayload = new GetScoringExcelPayload(); 
		scoringDataPayload.setTraceId("146e023797418ee9");
		JsonNode pcpScoreDataJson = new ObjectMapper().readTree("{\"scoredSortedPoolPcps\":[{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY898\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY899\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}],\"drivingDistScoredPcps\":[{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY898\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY899\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}],\"tieOnDrivingPcps\":null,\"tieOnVBPPcps\":null,\"tieOnPanelCapacityPcps\":null,\"tieOnLastNamePcps\":null,\"selectedPcp\":{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}}");

		Mockito.when(pcpScoreAuditService.getPCPScoreDataAsJson(scoringDataPayload.getTraceId())).thenReturn(pcpScoreDataJson);

		JsonNode expectedPCPScoreDataJson = pcpScoreAuditController.getPCPScoreDataAsJson(scoringDataPayload);
		assertEquals(pcpScoreDataJson,expectedPCPScoreDataJson);
	}

	/*	@Test
	public void getPCPScoreDataAsXLSXPayloadTest() throws Exception {

		mockMvc.perform(
				get("/getPCPScoreDataAsXLSX"))
		.andExpect(view().name("146e023797418ee9"))
		.andExpect(status().isOk());
	}
	 */

	@Test
	public void getPCPScoreDataAsXLSXTest() throws JsonProcessingException, IOException {

		GetScoringExcelPayload scoringDataPayload = new GetScoringExcelPayload(); 
		scoringDataPayload.setTraceId("146e023797418ee9");
		String traceId = scoringDataPayload.getTraceId();
		HttpServletResponse response = mock(HttpServletResponse.class);
		response.addHeader("Content-disposition", "attachment;filename=" +traceId +".xlsx");
		response.setContentType("application/vnd.ms-excel");
		XSSFWorkbook workbook = mock(XSSFWorkbook.class);
		JsonNode pcpScoreDataJson = new ObjectMapper().readTree("{\"scoredSortedPoolPcps\":[{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY898\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY899\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}],\"drivingDistScoredPcps\":[{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY898\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY899\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}],\"tieOnDrivingPcps\":null,\"tieOnVBPPcps\":null,\"tieOnPanelCapacityPcps\":null,\"tieOnLastNamePcps\":null,\"selectedPcp\":{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}}");

		Mockito.when(pcpScoreAuditService.getPCPScoreDataAsJson(scoringDataPayload.getTraceId())).thenReturn(pcpScoreDataJson);
		if(null != pcpScoreDataJson) {
			Mockito.when(pcpScoreAuditService.getPCPScoreDataAsXLSX(pcpScoreDataJson)).thenReturn(workbook);
		} 
		
	}	

	@Test
	public void getPCPAssignmentDataAsJsonPayloadTest() throws Exception {
		mockMvc.perform(
				post("/getPCPAssignmentDataAsJson")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getPcpAssignmentInfoPayload())
				.accept(MediaType.APPLICATION_JSON)
				);
	}
	
/*	@Test
	public void getPCPAssignmentDataAsJsonTest() throws Exception {
		
		PcpAssignmentInfoPayload pcpAssignmentInfoPayload = new PcpAssignmentInfoPayload();
		pcpAssignmentInfoPayload.setMemberDob("1995-11-04");
		pcpAssignmentInfoPayload.setPcpAssignmentDate("2018-08-24");
		String Json = "{\"traceId\":\"TESTfdsfsfsdf3213123123\",\"providerData\":\"{\"name\":\"Jane\"}\"}"; 
		
		Mockito.when(pcpScoreAuditService.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload)).thenReturn(Json);
		
		String expectedJson = pcpScoreAuditController.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload);
		assertEquals(Json,expectedJson);

	}
*/
	public String getPCPScoringDataPayload() throws JsonProcessingException {

		PCPScoringDataPayload pcpScoreVerifyPayload = new PCPScoringDataPayload();
		pcpScoreVerifyPayload.setTraceId("TESTfdsfsfsdf3213123123");	
		pcpScoreVerifyPayload.setProviderData("{\"name\":\"Jane\"}");

		mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter(); 
		String requestJson = ow.writeValueAsString(pcpScoreVerifyPayload);

		return requestJson;
	}

	public GetScoringExcelPayload getScoringExcelPayload() throws JsonProcessingException {

		GetScoringExcelPayload getScoringExcelPayloadString = new GetScoringExcelPayload(); 
		getScoringExcelPayloadString.setTraceId("146e023797418ee9");

		return getScoringExcelPayloadString;
	}

	public String getPcpAssignmentInfoPayload() throws JsonProcessingException {

		PcpAssignmentInfoPayload pcpAssignmentInfoPayload = new PcpAssignmentInfoPayload();
		//pcpAssignmentInfoPayload.setMemberDob("1995-11-04");
		pcpAssignmentInfoPayload.setPcpAssignmentDate("2018-08-24");

		mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter(); 
		String requestJson = ow.writeValueAsString(pcpAssignmentInfoPayload);

		return requestJson;
	}
}