package com.anthem.hca.smartpcp.drools.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus; 
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.drools.service.RulesEngineService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.ResponseCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.anthem.hca.smartpcp.drools.model.RulesEngineInputPayload;
import com.anthem.hca.smartpcp.drools.model.RulesEngineOutputPayload;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

/**
 *
 */
@RestController
@Api(value = "Rules Engine MicroService API")
public class DroolsController {

	@Autowired
	private RulesEngineService rService;

	@Autowired
	private RulesEngineOutputPayload opPayload;
	
	private static final Logger logger = LogManager.getLogger(DroolsController.class);

	/**
	 * @param ipPayload
	 * @return
	 * @throws JsonProcessingException 
	 */
	@ApiOperation(value = "Get Invocation Order for SmartPCP and rules for Affinity and MDO", response = RulesEngineOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS", response = RulesEngineOutputPayload.class),
    		@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR", response = RulesEngineOutputPayload.class),
    		@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE ", response = RulesEngineOutputPayload.class),
    		@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS", response = RulesEngineOutputPayload.class) 
        }
    )

	@RequestMapping(value = "/smartPCPSelect", method = RequestMethod.POST)
	public RulesEngineOutputPayload getRequest(@ApiParam(value = "Member input Payload for Business Rules Engine", required = true ) @Validated @RequestBody RulesEngineInputPayload ipPayload)  {

		if (null != ipPayload.getRequestFor() && "" != ipPayload.getRequestFor()) {
			logger.info("Starting to apply Drools Rules engine on the Input payload");

			try {
				opPayload.setRules(rService.getRules(ipPayload));
				opPayload.setResponseCode(ResponseCodes.SUCCESS);
				opPayload.setResponseMessage("Rules successfully applied on Payload");
				logger.info("Rules successfully applied on Payload");
			} catch(DroolsParseException  | JsonProcessingException  e) {
				logger.error("Internal Exception occured in RulesEngineService " + e.getMessage());
				opPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
				opPayload.setResponseMessage("Internal Error in Drools Rules Engine " + e.getMessage());
			}
		}
		else {
			opPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
			opPayload.setResponseMessage("RequestFor is Null or Empty in Input Payload");
			logger.error("RequestFor is Null or Empty in Input Payload. Rules could not be applied.");
		}

		return opPayload;
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RulesEngineOutputPayload handleException(MethodArgumentNotValidException exception) {

		RulesEngineOutputPayload outputPayload = new RulesEngineOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(exception.getMessage()));
		return outputPayload;
	} 
	
	/**
	 * @return
	 */
	@Bean	
	public RulesEngineOutputPayload getOutputPayload() {
		return new RulesEngineOutputPayload();
	}

}
