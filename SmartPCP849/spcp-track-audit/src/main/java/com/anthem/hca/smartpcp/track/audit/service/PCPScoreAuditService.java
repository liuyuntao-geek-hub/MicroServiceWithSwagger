package com.anthem.hca.smartpcp.track.audit.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;
import com.anthem.hca.smartpcp.track.audit.constants.ResponseCodes;
import com.anthem.hca.smartpcp.track.audit.payload.PCPScoringDataPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfoPayload;
import com.anthem.hca.smartpcp.track.audit.payload.ResponsePayload;
import com.anthem.hca.smartpcp.track.audit.repo.PCPScoreAuditRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
@RefreshScope
public class PCPScoreAuditService {
	@Autowired
	PCPScoreAuditRepo pcpScoreAuditRepo;
	@Value("${excel.scoring.selected.pcp.sheetname}") private String selectedPcpSheetName;
	@Value("${excel.scoring.mdo.pool.sheetname}") private String mdoPoolSheetName;
	@Value("${excel.scoring.top50.driving.sheetname}") private String top50DrivingSheetName;
	@Value("${excel.scoring.tie.driving.sheetname}") private String tieDrivingSheetName;
	@Value("${excel.scoring.tie.vbp.sheetname}") private String tieVbpSheetName;
	@Value("${excel.scoring.tie.panel.sheetname}") private String tiePanelSheetName;
	@Value("${excel.scoring.tie.lastname.sheetname}") private String tieLastNameSheetName;
	@Value("${excel.scoring.sheet.ordered.columnnames.csv}") private String orderedColumnNames;



	public ResponsePayload savePCPScoreData(PCPScoringDataPayload pcpScoreVerifyPayload ){
		ResponsePayload responsePayload = new ResponsePayload();

		int insertCount = pcpScoreAuditRepo.savePCPScoreData(pcpScoreVerifyPayload);

		if(1==insertCount) {
			responsePayload.setResponseCode(ResponseCodes.SUCCESS);
			responsePayload.setResponseMessage(ErrorMessages.SCORE_INSERT_SUCCESSFUL);

		}else {
			responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			responsePayload.setResponseMessage(ErrorMessages.SCORE_INSERT_ERR);

		}

		return responsePayload;
	}

	public JsonNode getPCPScoreDataAsJson(String traceId) {
		return pcpScoreAuditRepo.getPCPScoreDataAsJson(traceId);
	}


	public XSSFWorkbook getPCPScoreDataAsXLSX(JsonNode pcpScoreDataJson) {

		XSSFWorkbook workbook = null;
		if(null != pcpScoreDataJson) {
			Map<String, JsonNode> sheetDetailsMap = new LinkedHashMap<>();
			sheetDetailsMap.put(selectedPcpSheetName, pcpScoreDataJson.path("selectedPcp"));
			sheetDetailsMap.put(mdoPoolSheetName, pcpScoreDataJson.path("scoredSortedPoolPcps"));
			sheetDetailsMap.put(top50DrivingSheetName, pcpScoreDataJson.path("drivingDistScoredPcps"));
			sheetDetailsMap.put(tieDrivingSheetName, pcpScoreDataJson.path("tieOnDrivingPcps"));
			sheetDetailsMap.put(tieVbpSheetName, pcpScoreDataJson.path("tieOnVBPPcps"));
			sheetDetailsMap.put(tiePanelSheetName, pcpScoreDataJson.path("tieOnPanelCapacityPcps"));
			sheetDetailsMap.put(tieLastNameSheetName, pcpScoreDataJson.path("tieOnLastNamePcps"));

			workbook = createXLSXSheetFromJson(sheetDetailsMap);
		}

		return workbook;
	}

	private XSSFWorkbook createXLSXSheetFromJson(Map<String, JsonNode> sheetDetailsMap) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		sheetDetailsMap.forEach((sheetName,jsonData)->{

			if(null != jsonData && jsonData.isArray() && 0<((ArrayNode)jsonData).size()) {

				XSSFSheet sheet = workbook.createSheet(sheetName);
				createColumnRow(sheet);
				int rowCount = 1;
				for(JsonNode jsonRow : jsonData) {
					createDataRow(sheet, rowCount++, jsonRow);
				}

			}else if(null != jsonData && jsonData.isObject()){
				XSSFSheet sheet = workbook.createSheet(sheetName);
				createColumnRow(sheet);
				createDataRow(sheet, 1, jsonData);
			}
		});

		return workbook;
	}

	private void createColumnRow(XSSFSheet sheet) {
		Row row = sheet.createRow(0);
		String[] columnNameArr = orderedColumnNames.split(",");
		int columnCount = 0;
		for(String columnName : columnNameArr) {
			Cell cell = row.createCell(columnCount++);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(columnName);
		}
	}

	private void createDataRow(XSSFSheet sheet, int rowCount, JsonNode jsonRow) {
		int columnCount=0;
		String[] columnNameArr = orderedColumnNames.split(",");
		Row row = sheet.createRow(rowCount);
		for(String columnName : columnNameArr) {
			Cell cell = row.createCell(columnCount++);
			cell.setCellType(CellType.STRING);
			if(!jsonRow.path(columnName).isNull() && jsonRow.path(columnName).isArray()) {
				cell.setCellValue(jsonRow.path(columnName).toString());
			} else {
				cell.setCellValue(jsonRow.path(columnName).asText());
			}
		}
	}

	public String getPCPAssignmentDataAsJson(PcpAssignmentInfoPayload pcpAssignmentInfoPayload) {
		return pcpScoreAuditRepo.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload);
	}
	
	public String getAssignedPCPDtlsTraceId(String traceId, String pcpAssignmentDate) {
		return pcpScoreAuditRepo.getAssignedPCPDtlsTraceId(traceId, pcpAssignmentDate);
	}

}
