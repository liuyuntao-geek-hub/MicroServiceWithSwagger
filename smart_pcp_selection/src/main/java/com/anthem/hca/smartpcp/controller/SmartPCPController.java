package com.anthem.hca.smartpcp.controller;

import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.MDC;
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

import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.service.SmartPCPService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
//Added single Line comment
@RestController
@Api(value = "Smart PCP API")
public class SmartPCPController {

	@Autowired
	private SmartPCPService service;

	private static final String LOG_ID = "logid";

	private static final Logger logger = LogManager.getLogger(SmartPCPController.class);

	@ApiOperation(value = "Smart Select PCP for the received Member", response = OutputPayload.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OutputPayload.class), })
	@RequestMapping(value = "smartSelection", method = RequestMethod.POST)
	public OutputPayload getPCP(
			@ApiParam(value = "Member input Payload", required = true) @Validated @RequestBody Member member)
			throws JsonProcessingException {
		try {
			String transactionId = UUID.randomUUID().toString();
			MDC.put(LOG_ID, transactionId);
			logger.info("SmartPCP Module started");
			if (null != member) {
				OutputPayload outputPayload = service.getPCP(member, transactionId);
				logger.info("PCP assigned to the member " + outputPayload);
				return outputPayload;
			}
		} finally {
			MDC.remove(LOG_ID);
		}
		return null;
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public OutputPayload handleException(MethodArgumentNotValidException exception) {

		OutputPayload outputPayload = new OutputPayload();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		Iterator<FieldError> iterator = exception.getBindingResult().getFieldErrors().iterator();
		if (null != iterator && iterator.hasNext()) {
			outputPayload.setResponseMessage(iterator.next().getDefaultMessage());
		} else {
			outputPayload.setResponseMessage(exception.getMessage());
		}
		return outputPayload;
	}

	@ExceptionHandler
	public OutputPayload handleException(JsonProcessingException exception) {

		OutputPayload outputPayload = new OutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(exception.getMessage());
		return outputPayload;
	}
}
