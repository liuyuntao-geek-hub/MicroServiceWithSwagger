/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - MDOProcessingController contains the endpoint to receive Member
 *               information as request and returns the OutputPayload containing the
 *               assigned pcp id, pcp scoring,Driving Distance,pcp network id,dummy flag.
 * 
 * @author AF53723
 * 
 * 
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.controller;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.model.Member;
import com.anthem.hca.smartpcp.mdoprocessing.service.ProcessingService;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ErrorMessages;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ProcessingHelper;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.anthem.hca.smartpcp.mdoprocessing.validator.DateValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "MDO Processing MicroService")
public class MDOProcessingController {

	private static final Logger  LOGGER = LoggerFactory.getLogger(MDOProcessingController.class);

	@Autowired
	private ProcessingService service;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ProcessingHelper helper;
	
	@Autowired
	private DateValidator dateUtils;

	/**
	 * @param member
	 * @return mdoprocessingoutputpayload
	 */
	@ApiOperation(value = "Get Member for SmartPCP and PCP from MDO Pooling", response = MDOProcessingOutputPayload.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS", response = MDOProcessingOutputPayload.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = MDOProcessingOutputPayload.class),
			@ApiResponse(code = 700, message = "SERVICE_DOWN ", response = MDOProcessingOutputPayload.class) })

	@RequestMapping(value = "/MDOProcessing", method = RequestMethod.POST)
	public MDOProcessingOutputPayload getPCP(
			@ApiParam(value = "Member input Payload for MDO Processing", required = true) @RequestBody Member member) {
		long time = System.currentTimeMillis();
		ConstraintViolation<Member> constraint;
		MDOProcessingOutputPayload outputPayload;
		
		try{
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Member>> violations = validator.validate(member);
		if (!violations.isEmpty()) {
			Iterator<ConstraintViolation<Member>> iterator = violations.iterator();
			if (iterator.hasNext()) {
				constraint = iterator.next();
				return helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS,constraint.getMessage());
			}
		}
		if (dateUtils.checkFuture(member.getMemberDob())) {
			return helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS,ErrorMessages.INVALID_MBR_DOB);
		}
		outputPayload =  service.getPCP(member);
		time = System.currentTimeMillis() - time;
 		LOGGER.debug("MDO Processing processing time in controller: {} ms ", time);
		}catch(Exception exception){
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			outputPayload = helper.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS,exception.getClass().getSimpleName());
		}
		return outputPayload;
	}

	/**
	 * @param exception
	 * @return OutputPayload
	 */
	@ExceptionHandler
	public MDOProcessingOutputPayload handleException(MethodArgumentNotValidException exception) {

		MDOProcessingOutputPayload outputPayload = new MDOProcessingOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(exception.getMessage()));

		LOGGER.error("Error occured while performing input payload validation {}", outputPayload.getResponseMessage());
		String out = outputPayload.toString();
		LOGGER.info("Output of MDO Processing is {}", out);
		return outputPayload;
	}

	/**
	 * @param exception
	 * @return OutputPayload
	 */
	@ExceptionHandler
	public MDOProcessingOutputPayload handleException(JsonProcessingException exception) {

		MDOProcessingOutputPayload outputPayload = new MDOProcessingOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(exception.getClass().getSimpleName());
		LOGGER.error("Error occured while performing input payload validation {}", outputPayload.getResponseMessage());
		return outputPayload;
	}
	
	@Bean
	public DateValidator getDateValidator() {
		return new DateValidator();
	}
}
