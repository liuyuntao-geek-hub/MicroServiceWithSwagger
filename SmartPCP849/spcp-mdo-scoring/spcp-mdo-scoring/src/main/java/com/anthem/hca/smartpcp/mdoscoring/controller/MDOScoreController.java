/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 *  Description - MDOScoreController contains the endpoint to receive Member,PCP, Rules
 *               information as request and returns the OutputPayload containing the
 *               assigned pcp id, pcp scoring,Driving Distance,pcp network id,response message and response code.
 * 
 * @author AF70896 
 * 
 *
 */
package com.anthem.hca.smartpcp.mdoscoring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.anthem.hca.smartpcp.mdoscoring.service.MDOScoreService;
import com.anthem.hca.smartpcp.mdoscoring.utility.ResponseCodes;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "MDO Scoring")
@RestController
public class MDOScoreController {

	@Autowired
	private MDOScoreService service;

	private static final Logger logger = LoggerFactory.getLogger(MDOScoreController.class);

	@ApiOperation(value = "MDO scoring to find the best PCP for a member", response = OutputPayloadInfo.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = OutputPayloadInfo.class),
			@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS", response = OutputPayloadInfo.class),
			@ApiResponse(code = 700, message = "SERVICE_DOWN", response = OutputPayloadInfo.class)})
	@RequestMapping(value = "/MDOScoreService", method = RequestMethod.POST)
	public OutputPayloadInfo getMdoScore(
			@ApiParam(value = "input Payload", required = true) @Validated @RequestBody InputPayloadInfo inputPayloadInfo) {
		long time = System.currentTimeMillis();
		PCPAssignmentFlow pcpAssignmentFlow = new PCPAssignmentFlow();
		OutputPayloadInfo outputPayload = service.getFinalPCP(inputPayloadInfo, pcpAssignmentFlow);
		time = System.currentTimeMillis() - time;
		logger.debug("MDO scoring processing time in controller: {} ms ", time);
		logger.info("PCP initial={} {}",inputPayloadInfo.getPcp().size(),outputPayload);
		return outputPayload;
	}

	@ExceptionHandler
	public OutputPayloadInfo handleException(MethodArgumentNotValidException exception) {

		OutputPayloadInfo outputPayload = new OutputPayloadInfo();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(exception.getMessage()));
		return outputPayload;
	}

	@ExceptionHandler
	public OutputPayloadInfo handleException(JsonProcessingException exception) {
		OutputPayloadInfo responsePayload = new OutputPayloadInfo();
		responsePayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		responsePayload.setResponseMessage(exception.getClass().getSimpleName());
		logger.error("Error occured while performing parsing input{}",exception.getMessage());
		return responsePayload;
	}
}
