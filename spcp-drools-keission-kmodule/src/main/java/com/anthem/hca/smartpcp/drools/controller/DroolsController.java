package com.anthem.hca.smartpcp.drools.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import com.anthem.hca.smartpcp.drools.service.SmartPCPService;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.ResponseCodes;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.io.RulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.MDOScoringRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.ProviderValidationRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.SmartPCPRulesOutputPayload;

@RestController
@RequestMapping(value = "/rules")
@Api(value = "Rules Engine MicroService API")
public class DroolsController {

	@Autowired
	private SmartPCPService smartPCPService;

	@Autowired
	private AffinityProviderValidationService aProviderValidationService;

	@Autowired
	private MDOProviderValidationService mProviderValidationService;

	@Autowired
	private MDOPoolingService mdoPoolingService;

	@Autowired
	private MDOScoringService mdoScoringService;

	private static final Logger logger = LogManager.getLogger(DroolsController.class);

	@ApiOperation(value = "Get Invocation Order for SmartPCP", response = SmartPCPRulesOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR"),
    		@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE"),
    		@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS") 
        }
    )
	@RequestMapping(value = "/smartpcp", method = RequestMethod.POST)
	public SmartPCPRulesOutputPayload postRequestForSmartPCP(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody RulesInputPayload ipPayload) throws DroolsParseException, JsonProcessingException {
		logger.info("Applying SmartPCP Rules on the Input payload...");

		SmartPCPRulesOutputPayload opPayload = new SmartPCPRulesOutputPayload();
		opPayload.setRules(smartPCPService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);

		String message = opPayload.getRules().isFallback()
			? "SmartPCP Fallback Rules successfully applied on Payload"
			: "SmartPCP Market Rules successfully applied on Payload";

		opPayload.setResponseMessage(message);
		logger.info(message);

		return opPayload;
	}

	@ApiOperation(value = "Get Provider Validation Rules for Affinity", response = ProviderValidationRulesOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR"),
    		@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE"),
    		@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS") 
        }
    )
	@RequestMapping(value = "/provider-validation/affinity", method = RequestMethod.POST)
	public ProviderValidationRulesOutputPayload postRequestForProviderValidationAffinity(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody RulesInputPayload ipPayload) throws DroolsParseException, JsonProcessingException {
		logger.info("Applying Affinity Provider Validation Rules on the Input payload...");

		ProviderValidationRulesOutputPayload opPayload = new ProviderValidationRulesOutputPayload();
		opPayload.setRules(aProviderValidationService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);

		String message = opPayload.getRules().isFallback()
			? "Affinity Provider Validation Fallback Rules successfully applied on Payload"
			: "Affinity Provider Validation Market Rules successfully applied on Payload";

		opPayload.setResponseMessage(message);
		logger.info(message);

		return opPayload;
	}

	@ApiOperation(value = "Get Provider Validation Rules for MDO", response = ProviderValidationRulesOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR"),
    		@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE"),
    		@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS") 
        }
    )
	@RequestMapping(value = "/provider-validation/mdo", method = RequestMethod.POST)
	public ProviderValidationRulesOutputPayload postRequestForProviderValidationMDO(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody RulesInputPayload ipPayload) throws DroolsParseException, JsonProcessingException {
		logger.info("Applying MDO Provider Validation Rules on the Input payload...");

		ProviderValidationRulesOutputPayload opPayload = new ProviderValidationRulesOutputPayload();
		opPayload.setRules(mProviderValidationService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);

		String message = opPayload.getRules().isFallback()
			? "MDO Provider Validation Fallback Rules successfully applied on Payload"
			: "MDO Provider Validation Market Rules successfully applied on Payload";

		opPayload.setResponseMessage(message);
		logger.info(message);

		return opPayload;
	}

	@ApiOperation(value = "Get Pooling Rules for MDO", response = MDOPoolingRulesOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR"),
    		@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE"),
    		@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS") 
        }
    )
	@RequestMapping(value = "/mdo/pooling", method = RequestMethod.POST)
	public MDOPoolingRulesOutputPayload postRequestForMDOPooling(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody RulesInputPayload ipPayload) throws DroolsParseException, JsonProcessingException {
		logger.info("Applying MDO Pooling Rules on the Input payload...");

		MDOPoolingRulesOutputPayload opPayload = new MDOPoolingRulesOutputPayload();
		opPayload.setRules(mdoPoolingService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);

		String message =
			String.format(
				"MDO Pooling %s Rules, DummyPCP %s Rules successfully applied on Payload"
				, opPayload.getRules().isFallback(AgendaGroup.MDO_POOLING) ? "Fallback" : "Market"
				, opPayload.getRules().isFallback(AgendaGroup.DUMMYPCP) ? "Fallback" : "LOB"
			);

		opPayload.setResponseMessage(message);
		logger.info(message);

		return opPayload;
	}

	@ApiOperation(value = "Get Scoring Rules for MDO", response = MDOScoringRulesOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 600, message = "JSON_VALIDATION_ERROR"),
    		@ApiResponse(code = 700, message = "SERVICE_NOT_AVAILABLE"),
    		@ApiResponse(code = 800, message = "OTHER_EXCEPTIONS") 
        }
    )
	@RequestMapping(value = "/mdo/scoring", method = RequestMethod.POST)
	public MDOScoringRulesOutputPayload postRequestForMDOScoring(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody RulesInputPayload ipPayload) throws DroolsParseException, JsonProcessingException {
		logger.info("Applying MDO Scoring Rules on the Input payload...");

		MDOScoringRulesOutputPayload opPayload = new MDOScoringRulesOutputPayload();
		opPayload.setRules(mdoScoringService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);

		String message = opPayload.getRules().isFallback()
			? "MDO Scoring Fallback Rules successfully applied on Payload"
			: "MDO Scoring Market Rules successfully applied on Payload";

		opPayload.setResponseMessage(message);
		logger.info(message);

		return opPayload;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RulesOutputPayload handleException(MethodArgumentNotValidException ex) {
		RulesOutputPayload outputPayload = new RulesOutputPayload();

		outputPayload.setResponseCode(ResponseCodes.JSON_VALIDATION_ERROR);
		outputPayload.setResponseMessage(
				ex
				.getBindingResult()
				.getFieldErrors()
				.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(ex.getMessage())
		);

		return outputPayload;
	}

	@ExceptionHandler(DroolsParseException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RulesOutputPayload handleException(DroolsParseException ex) {
		RulesOutputPayload outputPayload = new RulesOutputPayload();

		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage("Internal Error in Drools: " + ex.getMessage());

		return outputPayload;
	}

	@ExceptionHandler(JsonProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RulesOutputPayload handleException(JsonProcessingException ex) {
		RulesOutputPayload outputPayload = new RulesOutputPayload();

		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage("Internal Error while invoking Transaction Audit Service: " + ex.getMessage());

		return outputPayload;
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RulesOutputPayload handleException(RuntimeException ex) {
		RulesOutputPayload outputPayload = new RulesOutputPayload();

		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage("Internal Error in Drools: " + ex.getMessage());

		return outputPayload;
	}

}
