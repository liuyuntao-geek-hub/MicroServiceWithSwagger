/**
* Copyright © 2018 Anthem, Inc.
* 
* MDOPoolController contains the end point to receive Member information as  request and returns the OutputPayload 
* containing the list of PCP's along with the response code and message.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.controller;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.common.am.vo.GroupSequenceForMember;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.mdo.pool.constants.ErrorMessages;
import com.anthem.hca.smartpcp.mdo.pool.constants.ResponseCodes;
import com.anthem.hca.smartpcp.mdo.pool.model.OutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.service.MDOPoolService;
import com.anthem.hca.smartpcp.mdo.pool.service.ProviderPoolService;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "MDO Pool Builder API")
public class MDOPoolController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MDOPoolController.class);

	@Autowired
	public MDOPoolService service;

	@Autowired
	private ValidatorFactory validatorFactory;

	@Autowired
	private ProviderPoolService poolService;
	
	@Autowired
	private MDOPoolUtils utils;

	/**
	 * @param member
	 * @return OutputPayload
	 * @throws JsonProcessingException
	 */
	@ApiOperation(value = "Build the PCP Pool for the received member", response = OutputPayload.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OutputPayload.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = OutputPayload.class),
			@ApiResponse(code = 700, message = "SERVICE_DOWN", response = OutputPayload.class) })
	@RequestMapping(value = "mdo-pool", method = RequestMethod.POST)
	public OutputPayload getPCPPool(
			@ApiParam(value = "Member input Payload", required = true) @RequestBody Member member) {
		long start = System.nanoTime();
		ConstraintViolation<Member> constraint;
		OutputPayload outputpayload;

		try {
			Validator validator = validatorFactory.getValidator();
			Set<ConstraintViolation<Member>> violations = validator.validate(member,GroupSequenceForMember.class);
			if (!violations.isEmpty()) {
				Iterator<ConstraintViolation<Member>> iterator = violations.iterator();
				if (iterator.hasNext()) {
					constraint = iterator.next();
					return utils.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, constraint.getMessage());
				}
			}
			if (utils.checkFuture(member.getMemberDob())) {
				return utils.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS, ErrorMessages.INVALID_MEMBER_DOB);
			}
			LOGGER.debug("In controller, MDO Pool Builder started{} ", "");
			outputpayload = service.getPool(member);
			long end = System.nanoTime();
			LOGGER.debug("Time taken for mdo pool controller is  : {} ms", (end - start) / 1000000);
		} catch (Exception exception) {
			LOGGER.error("Exception occured while processing {}", exception.getMessage(), exception);
			outputpayload = utils.createErrorPayload(ResponseCodes.OTHER_EXCEPTIONS,
					exception.getClass().getSimpleName());
		}
		return outputpayload;

	}

	/**
	 * @param exception
	 * @return OutputPayload
	 */
	@ExceptionHandler
	public OutputPayload handleException(JsonProcessingException exception) {
		OutputPayload outputPayload = new OutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(exception.getClass().getSimpleName());
		LOGGER.error("MDO Pool Service Error | Occured while performing parsing input {} :", exception.getMessage());
		return outputPayload;
	}
	
}
