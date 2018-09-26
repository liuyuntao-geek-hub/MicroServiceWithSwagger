package com.anthem.hca.smartpcp.track.audit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;
import com.anthem.hca.smartpcp.track.audit.constants.ResponseCodes;
import com.anthem.hca.smartpcp.track.audit.payload.PCPScoringDataPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfoPayload;
import com.anthem.hca.smartpcp.track.audit.payload.ResponsePayload;
import com.anthem.hca.smartpcp.track.audit.repo.PCPScoreAuditRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PCPScoreAuditServiceTest {

	@InjectMocks
	PCPScoreAuditService pcpScoreAuditService;
	
	@Mock
	PCPScoreAuditRepo pcpScoreAuditRepo;

	@Before 
	public void setupMock() { 
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(pcpScoreAuditService, "selectedPcpSheetName", "SELECTED_PCP");
		ReflectionTestUtils.setField(pcpScoreAuditService, "mdoPoolSheetName", "SCORED_MDO_POOL");
		ReflectionTestUtils.setField(pcpScoreAuditService, "top50DrivingSheetName", "TOP50_DRIVING_MDO_POOL");
		ReflectionTestUtils.setField(pcpScoreAuditService, "tieDrivingSheetName", "TIE_DRIVING");
		ReflectionTestUtils.setField(pcpScoreAuditService, "tieVbpSheetName", "TIE_VBP");
		ReflectionTestUtils.setField(pcpScoreAuditService, "tiePanelSheetName", "TIE_PANEL");
		ReflectionTestUtils.setField(pcpScoreAuditService, "tieLastNameSheetName", "TIE_LAST_NAME");
		ReflectionTestUtils.setField(pcpScoreAuditService, "orderedColumnNames", "provPcpId,rgnlNtwkId,lastName,speciality,grpgRltdPadrsEfctvDt,distance,rank,pcpLang,vbpFlag,pcpScore,panelCapacity,vbpScore,distanceScore,limitedTimeBonusScore,specialityScore,languageScore,rankScore");
	}

	@Test
	public void savePCPScoreDataTestPositive(){

		PCPScoringDataPayload pcpScoreVerifyPayload = new PCPScoringDataPayload();
		pcpScoreVerifyPayload.setTraceId("TESTfdsfsfsdf3213123123");	
		pcpScoreVerifyPayload.setProviderData("{\"name\":\"Jane\"}");
		int insertCount = 1;
		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.SUCCESS);
		responsePayload.setResponseMessage(ErrorMessages.SCORE_INSERT_SUCCESSFUL);

		Mockito.when(pcpScoreAuditRepo.savePCPScoreData(pcpScoreVerifyPayload)).thenReturn(insertCount);

		ResponsePayload expectedResponsePayload = pcpScoreAuditService.savePCPScoreData(pcpScoreVerifyPayload);
		assertEquals(responsePayload.getResponseCode(),expectedResponsePayload.getResponseCode());
	}

	@Test
	public void savePCPScoreDataTestNegative(){

		PCPScoringDataPayload pcpScoreVerifyPayload = new PCPScoringDataPayload();
		pcpScoreVerifyPayload.setTraceId("TESTfdsfsfsdf3213123123");	
		pcpScoreVerifyPayload.setProviderData("{\"name\":\"Jane\"}");
		int insertCount = 0;
		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		responsePayload.setResponseMessage(ErrorMessages.SCORE_INSERT_ERR);

		Mockito.when(pcpScoreAuditRepo.savePCPScoreData(pcpScoreVerifyPayload)).thenReturn(insertCount);

		ResponsePayload expectedResponsePayload = pcpScoreAuditService.savePCPScoreData(pcpScoreVerifyPayload);
		assertEquals(responsePayload.getResponseCode(),expectedResponsePayload.getResponseCode());
	}
	
	@Test
	public void getPCPScoreDataAsXLSX() throws JsonProcessingException, IOException {
		JsonNode pcpScoreDataJson = new ObjectMapper().readTree("{\"scoredSortedPoolPcps\":[{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}],\"drivingDistScoredPcps\":[{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}],\"tieOnDrivingPcps\":[{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}],\"tieOnVBPPcps\":[{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}],\"tieOnPanelCapacityPcps\":[{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}],\"tieOnLastNamePcps\":[{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}],\"selectedPcp\":{\"provPcpId\":\"GY899\",\"lastName\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"rank\":\"0\",\"speciality\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"pcpScore\":25,\"panelCapacity\":28.0,\"distance\":46.0,\"vbpScore\":0,\"distanceScore\":0,\"limitedTimeBonusScore\":0,\"specialityScore\":0,\"languageScore\":0,\"rankScore\":0}}");
		XSSFWorkbook workbook = pcpScoreAuditService.getPCPScoreDataAsXLSX(pcpScoreDataJson);
		assertNotNull(workbook.getSheetAt(0));
		assertEquals("SELECTED_PCP", workbook.getSheetAt(0).getSheetName());
		assertNotNull(workbook.getSheetAt(0).getRow(1).getCell(0));
		assertNotNull(workbook.getSheetAt(1));
		assertEquals("SCORED_MDO_POOL", workbook.getSheetAt(1).getSheetName());
		assertNotNull(workbook.getSheetAt(1).getRow(1).getCell(0));
		assertNotNull(workbook.getSheetAt(2));
		assertEquals("TOP50_DRIVING_MDO_POOL", workbook.getSheetAt(2).getSheetName());
		assertNotNull(workbook.getSheetAt(2).getRow(1).getCell(0));
		assertNotNull(workbook.getSheetAt(3));
		assertEquals("TIE_DRIVING", workbook.getSheetAt(3).getSheetName());
		assertNotNull(workbook.getSheetAt(3).getRow(1).getCell(0));
		assertNotNull(workbook.getSheetAt(4));
		assertEquals("TIE_VBP", workbook.getSheetAt(4).getSheetName());
		assertNotNull(workbook.getSheetAt(4).getRow(1).getCell(0));
		assertNotNull(workbook.getSheetAt(5));
		assertEquals("TIE_PANEL", workbook.getSheetAt(5).getSheetName());
		assertNotNull(workbook.getSheetAt(5).getRow(1).getCell(0));
		assertNotNull(workbook.getSheetAt(6));
		assertEquals("TIE_LAST_NAME", workbook.getSheetAt(6).getSheetName());
		assertNotNull(workbook.getSheetAt(6).getRow(1).getCell(0));
		workbook.close();
	}
	
	@Test
	public void getPCPScoreDataAsJsonTest() throws JsonProcessingException, IOException {

		String traceId = "146e023797418ee9";
		JsonNode pcpScoreDataJson = new ObjectMapper().readTree("{\"scoredSortedPoolPcps\":[{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY898\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY899\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":null,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}],\"drivingDistScoredPcps\":[{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY898\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0},{\"provPcpId\":\"GY899\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"N\",\"aerialDistance\":46.0,\"mdoScore\":25,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}],\"tieOnDrivingPcps\":null,\"tieOnVBPPcps\":null,\"tieOnPanelCapacityPcps\":null,\"tieOnLastNamePcps\":null,\"selectedPcp\":{\"provPcpId\":\"GY808\",\"pcpLastNm\":\"kent\",\"latdCordntNbr\":17.38714,\"lngtdCordntNbr\":78.491684,\"rgnlNtwkId\":\"GGH788\",\"pcpRankgId\":\"0\",\"spcltyDesc\":\"Internal medicine\",\"grpgRltdPadrsEfctvDt\":1507507200000,\"maxMbrCnt\":2500,\"curntMbrCnt\":700,\"pcpLang\":[\"ENG\",\"SPA\"],\"vbpFlag\":\"Y\",\"aerialDistance\":46.0,\"mdoScore\":35,\"panelCapacity\":28.0,\"drivingDistance\":46.0,\"aerialDistanceScore\":0,\"vbpScore\":0,\"drivingDistanceScore\":0,\"limitedTimeBonusScore\":0,\"spcltyDescScore\":0,\"languageScore\":0,\"pcpRankgIdScore\":0}}");

		Mockito.when(pcpScoreAuditRepo.getPCPScoreDataAsJson(traceId)).thenReturn(pcpScoreDataJson);

		JsonNode expectedPCPScoreDataJson = pcpScoreAuditService.getPCPScoreDataAsJson(traceId);

		assertEquals(pcpScoreDataJson,expectedPCPScoreDataJson);
	}

	@Test
	public void getPCPAssignmentDataAsJsonTest() {

		PcpAssignmentInfoPayload pcpAssignmentInfoPayload = new PcpAssignmentInfoPayload();
		//pcpAssignmentInfoPayload.setMemberDob("1995-11-04");
		pcpAssignmentInfoPayload.setPcpAssignmentDate("2018-08-24");

		String Json = "{\"traceId\":\"TESTfdsfsfsdf3213123123\",\"providerData\":\"{\"name\":\"Jane\"}\"}"; 

		Mockito.when(pcpScoreAuditRepo.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload)).thenReturn(Json);

		String expectedJson = pcpScoreAuditService.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload);

		assertEquals(Json,expectedJson);
	}
}