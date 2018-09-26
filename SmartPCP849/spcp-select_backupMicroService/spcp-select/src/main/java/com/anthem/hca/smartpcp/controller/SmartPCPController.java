package com.anthem.hca.smartpcp.controller;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ErrorMessages;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.helper.DateUtils;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.service.OutputPayloadService;
import com.anthem.hca.smartpcp.service.SmartPCPService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - SmartPCPController contains the endpoint to receive Member
 *               information as request and returns the OutputPayload containing the
 *               assigned pcp id, module through which the pcp was assigned, response
 *               code and message.
 * 
 * @author AF71111
 */
@RestController
@Api(value = "Smart PCP API")
public class SmartPCPController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartPCPController.class);
	
	@Autowired
	private SmartPCPService service;

	@Autowired
	private OutputPayloadService payloadHelper;
	
	@Autowired
	private DateUtils dateUtils;

	@Autowired
	private ValidatorFactory validatorFactory;


	/**
	 * @param member
	 * @return OutputPayload
	 */
	@ApiOperation(value = "Smart Select PCP for the received Member", response = OutputPayload.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OutputPayload.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = OutputPayload.class),
			@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE ", response = OutputPayload.class) })
	@RequestMapping(value = "smartSelection", method = RequestMethod.POST)
	public OutputPayload getPCP(@ApiParam(value = "Member input Payload", required = true) @RequestBody Member member) {
		
		LOGGER.debug("SmartPCP Module started {}", Constants.EMPTY_STRING);
		
		ConstraintViolation<Member> constraint;
		OutputPayload outputPayload;
		
		try{
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Member>> violations = validator.validate(member);
		if (!violations.isEmpty()) {
			Iterator<ConstraintViolation<Member>> iterator = violations.iterator();
			if (iterator.hasNext()) {
				constraint = iterator.next();
				return payloadHelper.createErrorPayload(constraint.getMessage(),
						ResponseCodes.OTHER_EXCEPTIONS, member, 1);
			}
		}
		if (dateUtils.checkFuture(member.getMemberDob())) {
			return payloadHelper.createErrorPayload(ErrorMessages.INVALID_MBR_DOB,
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		outputPayload =  service.getPCP(member);
		
		}catch(Exception exception){
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			outputPayload = payloadHelper.createErrorPayload(exception.getClass().getSimpleName(),
					ResponseCodes.OTHER_EXCEPTIONS, member, 1);
		}
		return outputPayload;
	}

	/**
	 * @param exception
	 * @return OutputPayload
	 */
	@ExceptionHandler
	public OutputPayload handleException(JsonProcessingException exception) {
		LOGGER.error("Error occured while parsing {}", exception.getMessage());
		return payloadHelper.createErrorPayload(exception.getClass().getSimpleName(), ResponseCodes.OTHER_EXCEPTIONS,
				null, 1);
	}
	
	@Bean
	public DateUtils dateUtils() {
		return new DateUtils();
	}
}
