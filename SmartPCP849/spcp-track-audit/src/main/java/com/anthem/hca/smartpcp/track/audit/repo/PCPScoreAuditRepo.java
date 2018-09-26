package com.anthem.hca.smartpcp.track.audit.repo;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.anthem.hca.smartpcp.track.audit.payload.PCPScoringDataPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfo;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfoPayload;
import com.anthem.hca.smartpcp.track.audit.utils.LoggerUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RefreshScope
public class PCPScoreAuditRepo {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${smartpcp.schema}")
	private String schema;

	@Value("${pcp.score.audit.insert.qry}")
	private String pcpScorePersistUrl;

	@Value("${pcp.score.audit.details.qry}")
	private String pcpScoreJsonUrl;

	@Value("${pcp.score.audit.assignment.name.pcpid.query}")
	private String pcpScoreAssignmentUrlWithNameAndPcpId;

	@Value("${pcp.score.audit.assignment.name.query}")
	private String pcpScoreAssignmentUrlWithName;

	@Value("${pcp.score.audit.assignment.pcpid.query}")
	private String pcpScoreAssignmentUrlWithPcpId;

	@Value("${pcp.score.audit.assignment.qry}")
	private String pcpAssignmentDtlsQuery;

	@Autowired
	private JdbcTemplate jdbcTemplate;



	@Transactional
	public int savePCPScoreData(PCPScoringDataPayload pcpScoreVerifyPayload) {
		int insertCount = 0;
		try {
			insertCount = jdbcTemplate.update(pcpScorePersistUrl, pcpScoreVerifyPayload.getTraceId(),
					pcpScoreVerifyPayload.getProviderData(), new Timestamp(System.currentTimeMillis()));
		} catch (DataAccessException e) {
			String cleanErrMsg = LoggerUtils.cleanMessage("Error occurred while inserting scoring data");
			logger.error(cleanErrMsg, e);
			insertCount = 0;
		}
		return insertCount;
	}

	public JsonNode getPCPScoreDataAsJson(String traceId) {
		JsonNode pcpScoreDataJson = null;
		try {

			String jsonString = jdbcTemplate.queryForObject(pcpScoreJsonUrl, new Object[] {traceId}, String.class);
			if(!StringUtils.isEmpty(jsonString)) {
				pcpScoreDataJson =   new ObjectMapper().readTree(jsonString);
			}
		} catch (DataAccessException | IOException e) {
			String cleanErrMsg = LoggerUtils.cleanMessage("Error occurred while fetching scoring data");
			logger.error(cleanErrMsg, e);
		}
		return pcpScoreDataJson;
	}

	public String getPCPAssignmentDataAsJson(PcpAssignmentInfoPayload pcpAssignmentInfoPayload) {
		List<PcpAssignmentInfo> pcpAssignmentInfoList = null;
		String pcpAssignmentDataJson = null;
		try {
			if(pcpAssignmentInfoPayload.getFirstName() != null && pcpAssignmentInfoPayload.getPcpId() != null) {
				pcpAssignmentInfoList = jdbcTemplate.query(pcpScoreAssignmentUrlWithNameAndPcpId, new Object[] {pcpAssignmentInfoPayload.getFirstName(), pcpAssignmentInfoPayload.getPcpId(), pcpAssignmentInfoPayload.getPcpAssignmentDate()}, new BeanPropertyRowMapper<PcpAssignmentInfo>(PcpAssignmentInfo.class));
			}else if(pcpAssignmentInfoPayload.getFirstName() != null && pcpAssignmentInfoPayload.getPcpId() == null) {
				pcpAssignmentInfoList = jdbcTemplate.query(pcpScoreAssignmentUrlWithName, new Object[] {pcpAssignmentInfoPayload.getFirstName(), pcpAssignmentInfoPayload.getPcpAssignmentDate()}, new BeanPropertyRowMapper<PcpAssignmentInfo>(PcpAssignmentInfo.class));
			}else if(pcpAssignmentInfoPayload.getFirstName() == null && pcpAssignmentInfoPayload.getPcpId() != null) {
				pcpAssignmentInfoList = jdbcTemplate.query(pcpScoreAssignmentUrlWithPcpId, new Object[] {pcpAssignmentInfoPayload.getPcpId(), pcpAssignmentInfoPayload.getPcpAssignmentDate()}, new BeanPropertyRowMapper<PcpAssignmentInfo>(PcpAssignmentInfo.class));
			}
			if(null != pcpAssignmentInfoList) {
				pcpAssignmentDataJson =   new ObjectMapper().writeValueAsString(pcpAssignmentInfoList);
			}
		} catch (DataAccessException | IOException e) {
			String cleanErrMsg = LoggerUtils.cleanMessage("Error occurred while fetching member pcp assignment data");
			logger.error(cleanErrMsg, e);
		}
		return pcpAssignmentDataJson;
	}

	public String getAssignedPCPDtlsTraceId(String traceId, String pcpAssignmentDate) {
		List<PcpAssignmentInfo> pcpAssignmentInfoList = null;
		String pcpAssignmentDataJson = null;
		try {

			pcpAssignmentInfoList = jdbcTemplate.query(pcpAssignmentDtlsQuery, new Object[] {traceId, pcpAssignmentDate}, new BeanPropertyRowMapper<PcpAssignmentInfo>(PcpAssignmentInfo.class));

			if(null != pcpAssignmentInfoList) {
				pcpAssignmentDataJson =   new ObjectMapper().writeValueAsString(pcpAssignmentInfoList);
			}
		} catch (DataAccessException | IOException e) {
			String cleanErrMsg = LoggerUtils.cleanMessage("Error occurred while fetching member pcp assignment data");
			logger.error(cleanErrMsg, e);
		}
		return pcpAssignmentDataJson;
	}

}
