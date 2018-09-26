package com.anthem.hca.smartpcp.track.audit.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.track.audit.constants.ErrorMessages;
import com.anthem.hca.smartpcp.track.audit.constants.ResponseCodes;
import com.anthem.hca.smartpcp.track.audit.payload.GetScoringExcelPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PCPScoringDataPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentInfoPayload;
import com.anthem.hca.smartpcp.track.audit.payload.PcpAssignmentPayload;
import com.anthem.hca.smartpcp.track.audit.payload.ResponsePayload;
import com.anthem.hca.smartpcp.track.audit.service.PCPScoreAuditService;
import com.anthem.hca.smartpcp.track.audit.utils.DateUtility;
import com.anthem.hca.smartpcp.track.audit.utils.LoggerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "PCP Score Verification API")
public class PCPScoreAuditController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	PCPScoreAuditService pcpScoreService;
	@Autowired
	DateUtility dateUtility;

	@ApiOperation( value = "Save PCP Score data", response = ResponsePayload.class)
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = ResponsePayload.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = ResponsePayload.class)})
	@RequestMapping(value="/savePCPScoreData",method = RequestMethod.POST)
	public ResponsePayload savePCPScoreData(
			@ApiParam(value = "PCP Verification Data", required = true) @RequestBody @Validated PCPScoringDataPayload pcpScoreVerifyPayload ){
		return pcpScoreService.savePCPScoreData(pcpScoreVerifyPayload);
	}

	@ApiOperation( value = "Get PCP Score data in json format", response = ResponsePayload.class)
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = String.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = String.class)})
	@RequestMapping(value="/getPCPScoreDataAsJson",method = RequestMethod.GET)
	public JsonNode getPCPScoreDataAsJson(
			@ApiParam(value = "PCP Assignment Data", required = true) @Validated GetScoringExcelPayload scoringDataPayload ){
		return pcpScoreService.getPCPScoreDataAsJson(scoringDataPayload.getTraceId());
	}

	@ApiOperation( value = "Get PCP Score data in XLSX format", response = ResponsePayload.class)
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = String.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = String.class)})
	@RequestMapping(value="/getPCPScoreDataAsXLSX",method = RequestMethod.GET)
	public void getPCPScoreDataAsXLSX(
			@ApiParam(value = "Trace Id of transaction", required = true) @Validated GetScoringExcelPayload scoringDataPayload, HttpServletResponse response ) throws IOException{

		XSSFWorkbook workbook = null;
		ServletOutputStream sos = null;
		String traceId = scoringDataPayload.getTraceId();
		try {
			JsonNode pcpScoreDataJson = pcpScoreService.getPCPScoreDataAsJson(traceId);
			if(null != pcpScoreDataJson) {
				workbook = pcpScoreService.getPCPScoreDataAsXLSX(pcpScoreDataJson);
				if(null != workbook) {
					response.addHeader("Content-disposition", "attachment;filename=" +traceId +".xlsx");
					response.setContentType("application/vnd.ms-excel");

					sos = response.getOutputStream();
					workbook.write(sos);

				}else {
					sos = response.getOutputStream();
					sos.print("<!DOCTYPE html><html><body><script>alert('Error occurred while downloading file !');window.close();</script></html></body>");
				}
			}else {
				sos = response.getOutputStream();
				sos.print("<!DOCTYPE html><html><body><script>alert('No data found for the given traceId !');window.close();</script></html></body>");
			}
		} catch (IOException e) {
			sos = response.getOutputStream();
			sos.print("<!DOCTYPE html><html><body><script>alert('Error occurred while downloading file !');window.close();</script></html></body>");
			String cleanErrMsg = LoggerUtils.cleanMessage("Error while writing xlsx data {}");
			logger.error(cleanErrMsg, e.getMessage());
		} finally {

			if(null != sos) {
				sos.flush();
				sos.close();
			}
			if(null != workbook) {
				workbook.close();
			}
		}
	}

	@ApiOperation( value = "Get PCP Assignment data in json format using assignment details like assigned PCP ID, Member First Name and PCP assignment date", response = ResponsePayload.class)
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = String.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = String.class)})
	@RequestMapping(value="/getAssignedPCPDtls",method = RequestMethod.POST)
	public String getAssignedPCPDtls(
			@ApiParam(value = "PCP Assignment Data", required = true) @Validated @RequestBody PcpAssignmentInfoPayload pcpAssignmentInfoPayload ) throws JsonProcessingException{
		if (dateUtility.checkFuture(pcpAssignmentInfoPayload.getPcpAssignmentDate())) {
			ResponsePayload response = new ResponsePayload();
			response.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			response.setResponseMessage("Dates cannot be future date !");
			return new ObjectMapper().writeValueAsString(response);
		} else {
			return pcpScoreService.getPCPAssignmentDataAsJson(pcpAssignmentInfoPayload);
		}
	}

	@ApiOperation( value = "Get PCP Assignment data in json format using assignment details like Trace Id and PCP assignment date", response = ResponsePayload.class)
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = String.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = String.class)})
	@RequestMapping(value="/getAssignedPCPDtlsTraceId",method = RequestMethod.POST)
	public String getAssignedPCPDtlsTraceId(@ApiParam(value = "PCP Assignment Data", required = true) @Validated @RequestBody PcpAssignmentPayload pcpAssignmentPayload) throws JsonProcessingException{
		String pcpAssignmentDate = pcpAssignmentPayload.getPcpAssignmentDate();
		String traceId  = pcpAssignmentPayload.getTraceId();
		if (dateUtility.checkFuture(pcpAssignmentDate)) {
			ResponsePayload response = new ResponsePayload();
			response.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			response.setResponseMessage("Dates cannot be future date !");
			return new ObjectMapper().writeValueAsString(response);
		} else {
			return pcpScoreService.getAssignedPCPDtlsTraceId(traceId, pcpAssignmentDate);
		}
	}

	/**
	 * @param exception
	 * @return ResponsePayload
	 * 
	 * handleException is used to handle exception occured while preparing payload for operations-audit micro-service from input payload received.
	 * 
	 */
	@ExceptionHandler
	public ResponsePayload handleException(MethodArgumentNotValidException exception) {

		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		Iterator<FieldError> iterator = exception.getBindingResult().getFieldErrors().iterator();

		if (null != iterator && iterator.hasNext()) {
			responsePayload.setResponseMessage(iterator.next().getDefaultMessage());
		} else {
			responsePayload.setResponseMessage(ErrorMessages.SOME_ERROR_OCCURED);
		}
		String cleanErrMsg = LoggerUtils.cleanMessage(exception.getMessage() + " Exception in handleException() MANVE");
		logger.error(cleanErrMsg);

		return responsePayload;
	}

	/**
	 * 
	 * @param exception
	 * @return ResponsePayload
	 * 
	 * handleException is used to handle exception occured for any internal server errors.
	 * 
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponsePayload handleIntServException(SQLException exception) {

		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		responsePayload.setResponseMessage(ErrorMessages.SOME_ERROR_OCCURED);
		String cleanErrMsg = LoggerUtils.cleanMessage(exception.getMessage() + " Exception in handleIntServException()");
		logger.error(cleanErrMsg);
		return responsePayload;
	}

	/**
	 * @param exception
	 * @return ResponsePayload
	 * 
	 * handleException is used to handle exception occured while preparing payload for operations-audit micro-service from input payload received.
	 * 
	 */
	@ExceptionHandler
	public ResponsePayload handleException(JsonProcessingException exception) {
		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		responsePayload.setResponseMessage(ErrorMessages.SOME_ERROR_OCCURED);

		String cleanErrMsg = LoggerUtils.cleanMessage(exception.getMessage() + " handleException() JPE ");
		logger.error(cleanErrMsg);
		return responsePayload;
	}



}
