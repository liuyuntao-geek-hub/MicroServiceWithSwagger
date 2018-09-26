package com.anthem.hca.smartpcp.affinity.controller;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;
import com.anthem.hca.smartpcp.affinity.constants.ResponseCodes;
import com.anthem.hca.smartpcp.affinity.model.AffinityOutputPayload;
import com.anthem.hca.smartpcp.affinity.service.AffinityService;
import com.anthem.hca.smartpcp.affinity.service.RestClientPayloadService;
import com.anthem.hca.smartpcp.affinity.util.FutureDateUtility;
import com.anthem.hca.smartpcp.common.am.vo.Member;


/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			AffinityController contains the end point to receive Member information as request and returns 
 * 			the OutputPayload containing the assigned pcp id, its regional network id, 
 * 			driving distance (aerial distance in case Bing fails), response code and response message.
 * 
 * @author AF65409 
 */
@RestController
@Api(value = "Affinity API")
public class AffinityController {

	@Autowired
	AffinityService affinityService;
	@Autowired
	private ValidatorFactory validatorFactory;
	@Autowired
	RestClientPayloadService restClientPayloadService;
	@Autowired
	private FutureDateUtility futureDateUtility;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param member
	 * @return AffinityOutPayload
	 * 
	 * 			findProvider is used to assign the PCP to received request from member.
	 * 
	 */
	@ApiOperation( value = "Assign the PCP ID for the received member", response = AffinityOutputPayload.class)
	@ApiResponses( value = {
			@ApiResponse(code = 200, message = "SUCCESS", response = AffinityOutputPayload.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = AffinityOutputPayload.class),
			@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE", response = AffinityOutputPayload.class)})
	@RequestMapping(value="/findProvider",method = RequestMethod.POST)
	public AffinityOutputPayload findProvider(
			@ApiParam(value = "Member input Payload", required = true) @RequestBody Member member ){

		long startFetch = System.nanoTime();

		AffinityOutputPayload affinityOutputPayload = null;

		ConstraintViolation<Member> constraint;
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Member>> violations = validator.validate(member);

		if (!violations.isEmpty()) {

			String errorMessage = ErrorMessages.FAILURE;
			Iterator<ConstraintViolation<Member>> iterator = violations.iterator();
			if (iterator.hasNext()) {
				constraint = iterator.next();
				errorMessage = constraint.getMessage();	
			}

			affinityOutputPayload = new AffinityOutputPayload();
			affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			affinityOutputPayload.setResponseMessage(errorMessage);
		}else {
			if(futureDateUtility.isFutureDate(member.getMemberDob())) {
				affinityOutputPayload = new AffinityOutputPayload();
				affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				affinityOutputPayload.setResponseMessage(ErrorMessages.INVALID_DOB);
			}else {
				logger.debug("Affinity microservice called with {}", member);
				affinityOutputPayload = affinityService.getAffinityOutPayload(member);	
				logger.info("PCP Assigned {}",affinityOutputPayload);
			}
		}

		restClientPayloadService.insertOperationFlow(affinityOutputPayload);

		long endFetch = System.nanoTime();
		double fetchTime = ((double) (endFetch - startFetch) / 1000000.0);
		logger.debug("Time taken to complete Affinity Microservice {} milliseconds", fetchTime);

		return affinityOutputPayload;
	}

	/**
	 * @param exception
	 * @return AffinityOutPayload
	 * 
	 * 			handleException is used to handle exception came while preparing payload for 
	 * 			affinity micro-service from input payload received.
	 * 			
	 */
	@ExceptionHandler
	public AffinityOutputPayload handleException(MethodArgumentNotValidException exception) {

		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();

		affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		affinityOutputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.findFirst()
				.orElse(exception.getMessage())); 

		logger.error(exception.getMessage() , exception);

		return affinityOutputPayload;
	}

	/**
	 * @param exception
	 * @return AffinityOutPayload
	 * 
	 * 			handleException is used to handle exception came while preparing payload for 
	 * affinity micro-service from input payload received.
	 * 			
	 */
	@ExceptionHandler
	public AffinityOutputPayload handleException(JsonProcessingException exception) {

		AffinityOutputPayload affinityOutputPayload = new AffinityOutputPayload();

		if(exception.getMessage().contains("Can not parse date")) {
			affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			affinityOutputPayload.setResponseMessage(ErrorMessages.INVALID_DOB);
		}else {
			affinityOutputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
			affinityOutputPayload.setResponseMessage(exception.getClass().getSimpleName());
		} 
		logger.error(exception.getMessage() , exception);
		return affinityOutputPayload;
	}

	@Bean
	FutureDateUtility futureDateUtilityBean() {
		return new FutureDateUtility();
	}
}