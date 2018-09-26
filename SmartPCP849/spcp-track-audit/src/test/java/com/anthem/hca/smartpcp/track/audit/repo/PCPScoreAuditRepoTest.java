package com.anthem.hca.smartpcp.track.audit.repo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.anthem.hca.smartpcp.track.audit.payload.PCPScoringDataPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfo;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfoPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PCPScoreAuditRepoTest {

	@Mock
	PCPScoreAuditRepo pcpScoreAuditRepo;
	@Mock
	private JdbcTemplate jdbcTemplate;

	@Value("${smartpcp.schema}")
	private String schema;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void savePCPScoreDataTest() throws JsonProcessingException {

		PCPScoringDataPayload pcpScoreVerifyPayload = getPCPScoringDataPayload();
		int insertCount = 1;
		String sql = "INSERT INTO " + schema
				+ ".SCORE_OPERATIONS_AUDIT (TRACE_ID,PROVIDER_DATA,CREATED_DATE) VALUES(?,?,?)";

		try {
			Mockito.when(pcpScoreAuditRepo.savePCPScoreData(pcpScoreVerifyPayload)).thenReturn(insertCount);
			Mockito.when(jdbcTemplate.update(sql, pcpScoreVerifyPayload.getTraceId(),
					pcpScoreVerifyPayload.getProviderData(), new Timestamp(System.currentTimeMillis()))).thenReturn(insertCount);
		} catch (DataAccessException e) {
			logger.error("Error occurred while inserting scoring data", e);
			insertCount = 0;
		}

		int expectedInsertCount = pcpScoreAuditRepo.savePCPScoreData(pcpScoreVerifyPayload);

		assertEquals(insertCount, expectedInsertCount);		
	}

	@Test
	public void getPCPScoreDataAsJsonTest() {

		String traceId = "146e023797418ee9";
		JsonNode pcpScoreDataJson = null;
		String jsonString = "";
		String sql = "SELECT PROVIDER_DATA FROM DV_PDPSPCP_XM.SCORE_OPERATIONS_AUDIT WHERE TRACE_ID = ?";
		try {
			Mockito.when(pcpScoreAuditRepo.getPCPScoreDataAsJson(traceId)).thenReturn(pcpScoreDataJson);
			Mockito.when(jdbcTemplate.queryForObject(sql, new Object[] {traceId}, String.class)).thenReturn(jsonString);
			if(!StringUtils.isEmpty(jsonString)) {
				pcpScoreDataJson =   new ObjectMapper().readTree(jsonString);
			}
		} catch (DataAccessException | IOException e) {
			logger.error("Error occurred while fetching scoring data", e);
		}

		JsonNode expectedPCPScoreDataJson = pcpScoreAuditRepo.getPCPScoreDataAsJson(traceId);

		assertEquals(pcpScoreDataJson, expectedPCPScoreDataJson);	
	}

	@SuppressWarnings("unused")
	@Test
	public void getPCPAssignmentDataAsJsonTest() throws JsonProcessingException {

		PcpAssignmentInfoPayload pcpAssignmentInfoPayload = getPcpAssignmentInfoPayload();
		List<PcpAssignmentInfo> pcpAssignmentInfoList = null;
		String pcpAssignmentDataJson = null;
		String sql = "SELECT O.TRACE_ID, O.MBR_FRST_NM, O.PCP_ID_ASSIGNED, O.MBR_NETWORK_ID, O.MBR_PROCESSING_ST FROM DV_PDPSPCP_XM.OPERATIONS_AUDIT O, DV_PDPSPCP_XM.MBR_TX_HIST H, DV_PDPSPCP_XM.SCORE_OPERATIONS_AUDIT S WHERE S.TRACE_ID=O.TRACE_ID AND O.TRACE_ID=H.TRACE_ID AND CAST(H.BRTH_DT AS DATE) = ? AND CAST(O.CREATED_TIME AS DATE) =?";
		try {
			Mockito.when(pcpScoreAuditRepo.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload)).thenReturn(pcpAssignmentDataJson);
			//Mockito.when(jdbcTemplate.query(sql, new Object[] {pcpAssignmentInfoPayload.getMemberDob(),pcpAssignmentInfoPayload.getPcpAssignmentDate()}, new BeanPropertyRowMapper<PcpAssignmentInfo>(PcpAssignmentInfo.class))).thenReturn(pcpAssignmentInfoList);
			if(null != pcpAssignmentInfoList) {
				pcpAssignmentDataJson =   new ObjectMapper().writeValueAsString(pcpAssignmentInfoList);
			}
		} catch (DataAccessException | IOException e) {
			logger.error("Error occurred while fetching member pcp assignment data", e);
		}

		String expectedPCPAssignmentDataJson = pcpScoreAuditRepo.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload);

		assertEquals(pcpAssignmentDataJson, expectedPCPAssignmentDataJson);	
	}

	public PCPScoringDataPayload getPCPScoringDataPayload(){

		PCPScoringDataPayload pcpScoreVerifyPayload = new PCPScoringDataPayload();
		pcpScoreVerifyPayload.setTraceId("TESTfdsfsfsdf3213123123");	
		pcpScoreVerifyPayload.setProviderData("{\"name\":\"Jane\"}");

		return pcpScoreVerifyPayload;
	}

	public PcpAssignmentInfoPayload getPcpAssignmentInfoPayload(){

		PcpAssignmentInfoPayload pcpAssignmentInfoPayload = new PcpAssignmentInfoPayload();
		//pcpAssignmentInfoPayload.setMemberDob("1995-11-04");
		pcpAssignmentInfoPayload.setPcpAssignmentDate("2018-08-24");

		return pcpAssignmentInfoPayload;
	}
}