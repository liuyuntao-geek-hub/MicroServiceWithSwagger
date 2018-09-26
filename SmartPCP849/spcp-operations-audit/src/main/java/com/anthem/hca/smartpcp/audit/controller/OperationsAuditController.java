package com.anthem.hca.smartpcp.audit.controller;

import java.sql.SQLException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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

import com.anthem.hca.smartpcp.audit.constants.ResponseCodes;
import com.anthem.hca.smartpcp.audit.payload.FlowOprPayload;
import com.anthem.hca.smartpcp.audit.payload.ResponsePayload;
import com.anthem.hca.smartpcp.audit.service.OperationsAuditService;
import com.anthem.hca.smartpcp.audit.utils.LoggerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			OperationsAuditController contains the end point to receive FlowOprPayload as a request from all micro services except for smart pcp 
 * 			and returns the responsePayload containing the response code and response message
 * 
 * @author AF56159 
 */
@RestController
@Api(value = "transactionLogger", consumes="application/json", produces="application/json")
@RefreshScope
public class OperationsAuditController {

	private final Logger logger = LoggerFactory.getLogger(OperationsAuditController.class);

	@Value ("${spcp.operations.audit.logdatabase}")
	private boolean logToDatabase;
	@Value ("${spcp.operations.audit.logsplunk}")
	private boolean logToSplunk;
	@Value("${operationsAudit.errorMsg}")
	private String errorMsg;
	@Value("${operationsAudit.successMsg}")
	private String successMsg;
 
	@Autowired
	OperationsAuditService operationsAuditService;

	/**
	 * @param flowOprPayload
	 * @return ResponsePayload
	 * 
	 * logSPCPFlowOprtn is used to log the request payload in to database and splunk logs based on configurable properties
	 * 
	 */
	@ApiOperation(value = "Log operation details from all other services ", response = ResponsePayload.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = ResponsePayload.class),
			@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR", response = ResponsePayload.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = ResponsePayload.class) 
	})
	@RequestMapping(value = "logSPCPFlowOprtn", method = RequestMethod.POST)
	public ResponsePayload logFlowOprtn(@ApiParam(value = "All other services operation payload", required = true) @Validated @RequestBody FlowOprPayload flowOprPayload) {
		ResponsePayload responsePayload = new ResponsePayload();
		String logMsg = "";
		try {
			if(logToDatabase && logToSplunk) {
				logMsg = LoggerUtils.cleanMessage( "INSERT SPCP FLOW " +  flowOprPayload.toString());
				logger.info(logMsg);
				operationsAuditService.logFlowOprtn(flowOprPayload);
			}else if(logToDatabase) {
				operationsAuditService.logFlowOprtn(flowOprPayload);
			}else if(logToSplunk) {
				logMsg = LoggerUtils.cleanMessage( "INSERT SPCP FLOW " +  flowOprPayload.toString());
				logger.info(logMsg);
			}
			responsePayload.setResponseCode(ResponseCodes.SUCCESS);
			responsePayload.setResponseMessage(successMsg);
		}catch(Exception e) {
			String errMsg = LoggerUtils.cleanMessage(e.getMessage() + " while inserting " + flowOprPayload.toString());
			logger.error(errMsg, e);
			responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			responsePayload.setResponseMessage(errorMsg);
		}

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
	public ResponsePayload handleException(MethodArgumentNotValidException exception) {

		ResponsePayload responsePayload = new ResponsePayload();
		responsePayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		Iterator<FieldError> iterator = exception.getBindingResult().getFieldErrors().iterator();

		if (null != iterator && iterator.hasNext()) {
			responsePayload.setResponseMessage(iterator.next().getDefaultMessage());
		} else {
			responsePayload.setResponseMessage(errorMsg);
		}
		String errMsg = LoggerUtils.cleanMessage(exception.getMessage() + " Exception in handleException() MANVE");
		logger.error(errMsg);
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
		responsePayload.setResponseMessage(errorMsg);
		String errMsg = LoggerUtils.cleanMessage(exception.getMessage() + " Exception in handleIntServException()");
		logger.error(errMsg);
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
		if(exception.getOriginalMessage().contains("out of range of int")){
			responsePayload.setResponseMessage(exception.getOriginalMessage());
		}else{
			responsePayload.setResponseMessage(errorMsg);
		}
		String errMsg = LoggerUtils.cleanMessage(exception.getMessage() + " handleException() JPE ");
		logger.error(errMsg);
		return responsePayload;
	}

}
