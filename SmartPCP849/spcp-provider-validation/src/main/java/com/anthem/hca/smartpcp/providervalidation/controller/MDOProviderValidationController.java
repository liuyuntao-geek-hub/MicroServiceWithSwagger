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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.anthem.hca.smartpcp.providervalidation.service.ProviderValidationService;
import com.anthem.hca.smartpcp.providervalidation.utility.ResponseCodes;
import com.anthem.hca.smartpcp.providervalidation.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.providervalidation.vo.MDO;
import com.anthem.hca.smartpcp.providervalidation.vo.MDOOutputPayloadInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Provider Validation Service")
@RestController
public class MDOProviderValidationController {

	@Autowired
	private ProviderValidationService providerService;
	@Autowired
	private ValidatorFactory validatorFactory;
	private static final Logger LOGGER = LoggerFactory.getLogger(MDOProviderValidationController.class);

	/**
	 * @param inputPayloadInfo
	 * @return MDOOutputPayloadInfo that contains a List of PCP
	 */
	@ApiOperation(value = "MDO Provider Validation Service for validating PCP", response = MDOOutputPayloadInfo.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = MDOOutputPayloadInfo.class), })
	@RequestMapping(value = "/validation-mdo", method = RequestMethod.POST)
	public MDOOutputPayloadInfo getPCPFromMDO(
			@ApiParam(value = "input Payload", required = true)  @RequestBody InputPayloadInfo inputPayloadInfo) {

		LOGGER.info("MDO Provider Validation started {}", "");
		LOGGER.info("MDOProviderValidationController - getPCPFromMDO :: pcp list size== {}", inputPayloadInfo.getPcpInfo().size());
		List<String> errList;
		ConstraintViolation<InputPayloadInfo> constraint;
		MDOOutputPayloadInfo mdoOutputPayloadInfo =null;
		
		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<InputPayloadInfo>> violations = validator.validate(inputPayloadInfo, MDO.class, Default.class);
		if (!violations.isEmpty()) {
			mdoOutputPayloadInfo = new MDOOutputPayloadInfo();
			Iterator<ConstraintViolation<InputPayloadInfo>> iterator = violations.iterator();
			while (iterator.hasNext()) {
				constraint = iterator.next();
				mdoOutputPayloadInfo.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
				mdoOutputPayloadInfo.setResponseMessage(constraint.getMessage());
				LOGGER.error("json errList list == {}", constraint.getMessage());	
				break;
			}
			return mdoOutputPayloadInfo;
		}
		return providerService.getFinalPCPMDO(inputPayloadInfo);
	}
	
	@RequestMapping(value = "/getApigeeHeaders", method = RequestMethod.POST)
	public HttpHeaders getApigeeHeaders(@RequestHeader HttpHeaders headers) {
		LOGGER.info("MDO Provider Validation started headers = {}", headers);
		return headers;
	}

	/**
	 * Method to handle exceptions while reading the MDO input payload
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public MDOOutputPayloadInfo handleExceptionMDO(MethodArgumentNotValidException exception) {

		MDOOutputPayloadInfo outputPayload = new MDOOutputPayloadInfo();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(exception.getMessage()));
		return outputPayload;
	}
	
	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public MDOOutputPayloadInfo handleExceptionMDO(JsonProcessingException exception) {

		MDOOutputPayloadInfo outputPayload = new MDOOutputPayloadInfo();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(exception.getOriginalMessage());
		return outputPayload;
	}

}
