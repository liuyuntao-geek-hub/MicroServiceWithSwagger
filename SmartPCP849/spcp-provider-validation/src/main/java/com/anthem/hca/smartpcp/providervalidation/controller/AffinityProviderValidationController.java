/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */ 
package com.anthem.hca.smartpcp.providervalidation.controller;

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
import com.anthem.hca.smartpcp.providervalidation.service.ProviderValidationService;
import com.anthem.hca.smartpcp.providervalidation.utility.ResponseCodes;
import com.anthem.hca.smartpcp.providervalidation.vo.AffinityOutputPayloadInfo;
import com.anthem.hca.smartpcp.providervalidation.vo.InputPayloadInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Api(value = "Provider Validation Service")
@RestController
public class AffinityProviderValidationController {

	@Autowired
	private ProviderValidationService providerService;

	private static final Logger LOGGER = LoggerFactory.getLogger(AffinityProviderValidationController.class);

	/**
	 * @param inputPayloadInfo
	 * @return AffinityOutputPayloadInfo that contains a single PCP or NULL
	 */
	@ApiOperation(value = "Affinity Provider Validation Service for validating PCP", response = AffinityOutputPayloadInfo.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AffinityOutputPayloadInfo.class), })
	@RequestMapping(value = "/validation-affinity", method = RequestMethod.POST)
	public AffinityOutputPayloadInfo getPCPFromAffinity(
			@ApiParam(value = "input Payload", required = true) @Validated @RequestBody InputPayloadInfo inputPayloadInfo) {

		LOGGER.debug("Affinity Provider Validation started {}","");
		LOGGER.info("MDOProviderValidationController - getPCPFromMDO :: input pcp list size== {}", inputPayloadInfo.getPcpInfo().size());
		return providerService.getFinalPCPAffinity(inputPayloadInfo);
		
	}

	/**
	 * Method to handle exceptions while reading the Affinity input payload
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public AffinityOutputPayloadInfo handleExceptionAffinity(MethodArgumentNotValidException exception) {
		AffinityOutputPayloadInfo outputPayload = new AffinityOutputPayloadInfo();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(exception.getMessage()));
		return outputPayload;
	}
	
	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public AffinityOutputPayloadInfo handleExceptionAffinity(JsonProcessingException exception) {
		AffinityOutputPayloadInfo outputPayload = new AffinityOutputPayloadInfo();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(exception.getOriginalMessage());
		return outputPayload;
	}

}