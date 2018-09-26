package com.anthem.hca.smartpcp.mdoprocessing.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j. Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.mdoprocessing.model.MDOProcessingOutputPayload;
import com.anthem.hca.smartpcp.mdoprocessing.service.ProcessingHelper;
import com.anthem.hca.smartpcp.mdoprocessing.service.ProcessingService;
import com.anthem.hca.smartpcp.mdoprocessing.utils.Constants;
import com.anthem.hca.smartpcp.mdoprocessing.utils.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author AF53723 MDO Processing Microservice receives member info from
 *         SmartPCP Mvc and sends it to SmartPooling mvc to receive PCP. Checks
 *         PCP flag to find whether its dummy PCP or not. Returns PCP to
 *         SmartPCP mvc.
 */
@RestController
@Api(value = "MDO Processing MicroService")
public class MDOProcessingController {

	private static final Logger logger = LoggerFactory.getLogger(MDOProcessingController.class);
	
	@Autowired
	private ProcessingService service;
	
	@Autowired
    private ValidatorFactory validatorFactory;

	@Autowired
	private ProcessingHelper helper;
	/**
	 * @param member
	 * @return mdoprocessingoutputpayload
	 */
	@ApiOperation(value = "Get Member for SmartPCP and PCP from MDO Pooling", response = MDOProcessingOutputPayload.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS", response = MDOProcessingOutputPayload.class),
			@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR", response = MDOProcessingOutputPayload.class),
			@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE ", response = MDOProcessingOutputPayload.class),
			@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS", response = MDOProcessingOutputPayload.class),
			@ApiResponse(code = 400, message = "BAD/NULL REQUEST", response = MDOProcessingOutputPayload.class) })

	@RequestMapping(value = "/MDOProcessing", method = RequestMethod.POST)
	public MDOProcessingOutputPayload getPCP(
			@ApiParam(value = "Member input Payload for MDO Processing", required = true)  @RequestBody Member member) {
		List<String> errList;
        ConstraintViolation<Member> constraint;
        
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
               errList = new ArrayList<>();
               Iterator<ConstraintViolation<Member>> iterator = violations.iterator();
               while (iterator.hasNext()) {
                     constraint = iterator.next();
                     errList.add(constraint.getMessage());
               }
               return helper.createErrorPayload(String.join(Constants.COMMA,errList), ResponseCodes.JSON_VALIDATION_ERROR);
        }

		logger.info("MDO Processing module started {}","");
		return service.getPCP(member);
	}

	/**
	 * @param exception
	 * @return OutputPayload
	 */
	@ExceptionHandler
	public MDOProcessingOutputPayload handleException(MethodArgumentNotValidException exception) {

		MDOProcessingOutputPayload outputPayload = new MDOProcessingOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
        outputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
		 .map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(exception.getMessage()));

		logger.error("Error occured while performing input payload validation {}" , outputPayload.getResponseMessage());
		String out=outputPayload.toString();
		logger.info("Output of MDO Processing is {}" , out);
		return outputPayload;
	}

	/**
	 * @param exception
	 * @return OutputPayload
	 */
	@ExceptionHandler
	public MDOProcessingOutputPayload handleException(JsonProcessingException exception) {

		MDOProcessingOutputPayload outputPayload = new MDOProcessingOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(exception.getOriginalMessage());
		logger.error("Error occured while performing input payload validation {}" , outputPayload.getResponseMessage());
		return outputPayload;
	}

}
