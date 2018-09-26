package com.anthem.hca.smartpcp.drools.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.drools.io.MDOPoolingRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.MDOScoringRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.ProviderValidationRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.RulesOutputPayload;
import com.anthem.hca.smartpcp.drools.io.SmartPCPRulesOutputPayload;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.service.AffinityProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOPoolingService;
import com.anthem.hca.smartpcp.drools.service.MDOProviderValidationService;
import com.anthem.hca.smartpcp.drools.service.MDOScoringService;
import com.anthem.hca.smartpcp.drools.service.SmartPCPService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.ResponseCodes;
import com.anthem.hca.smartpcp.drools.util.StaticLoggerMessages;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The DroolsController class exposes 5 endpoints of the Drools Rules Engine for other Services to invoke.
 * 
 * 1. /rules/smartpcp - Returns the SmartPCP Rules.
 * 2. /rules/provider-validation/affinity - Returns the Affinity Provider Validation Rules. 
 * 3. /rules/provider-validation/mdo - Returns the MDO Provider Validation Rules.
 * 4. /rules/mdo/pooling - Returns the MDO Pooling Rules.
 * 5. /rules/mdo/scoring - Returns the MDO Scoring Rules.
 * 
 * @author  Saptarshi Dey (AF66853)
 * @version 1.7
 */

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

	private static final Logger logger = LoggerFactory.getLogger(DroolsController.class);

	/**
	 * This method is used to accept Member request from SmartPCP and run Rules on the Payload.
	 * The Member JSON String is converted to Member POJO first and then a SmartPCPRules Object
	 * is created from the Member object. After the Rules are executed, SmartPCPRules gets updated
	 * with the Drools configured attributes and this is returned to the Caller.
	 * 
	 * @param  ipPayload               The Member JSON Body
	 * @return                         OutputPayload containing SmartPCP Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    Member
	 * @see    SmartPCPService
	 * @see    SmartPCPRules
	 * @see    SmartPCPRulesOutputPayload 
	 */
	@ApiOperation(value = "Get Invocation Order for SmartPCP", response = SmartPCPRulesOutputPayload.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS"),
    		@ApiResponse(code = 700, message = "SERVICE_DOWN"), 
        }
    )
	@RequestMapping(value = "/smartpcp", method = RequestMethod.POST)
	public SmartPCPRulesOutputPayload postRequestForSmartPCP(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody Member ipPayload) throws DroolsParseException, JsonProcessingException {
		SmartPCPRulesOutputPayload opPayload = new SmartPCPRulesOutputPayload();
		opPayload.setRules(smartPCPService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);
		opPayload.setResponseMessage(StaticLoggerMessages.RULES_EXIT_MSG_SMARTPCP);

		logger.info(StaticLoggerMessages.RULES_EXIT_MSG_SMARTPCP);

		return opPayload;
	}

	/**
	 * This method is used to accept Member request from Affinity and run Rules on the Payload.
	 * The Member JSON String is converted to Member POJO first and then a ProviderValidationRules Object
	 * is created from the Member object. After the Rules are executed, ProviderValidationRules gets updated
	 * with the Drools configured attributes and this is returned to the Caller.
	 * 
	 * @param  ipPayload               The Member JSON Body
	 * @return                         OutputPayload containing Provider Validation Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    Member
	 * @see    AffinityProviderValidationService
	 * @see    ProviderValidationRules
	 * @see    ProviderValidationRulesOutputPayload  
	 */
	@ApiOperation(value = "Get Provider Validation Rules for Affinity", response = ProviderValidationRulesOutputPayload.class)
    @ApiResponses(
        value = {
        	@ApiResponse(code = 200, message = "SUCCESS"),
        	@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS"),
        	@ApiResponse(code = 700, message = "SERVICE_DOWN"), 
        }
    )
	@RequestMapping(value = "/provider-validation/affinity", method = RequestMethod.POST)
	public ProviderValidationRulesOutputPayload postRequestForProviderValidationAffinity(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody Member ipPayload) throws DroolsParseException, JsonProcessingException {
		ProviderValidationRulesOutputPayload opPayload = new ProviderValidationRulesOutputPayload();
		opPayload.setRules(aProviderValidationService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);
		opPayload.setResponseMessage(StaticLoggerMessages.RULES_EXIT_MSG_PROVAL_AFFINITY);

		logger.info(StaticLoggerMessages.RULES_EXIT_MSG_PROVAL_AFFINITY);

		return opPayload;
	}

	/**
	 * This method is used to accept Member request from MDO and run Rules on the Payload.
	 * The Member JSON String is converted to Member POJO first and then a ProviderValidationRules Object
	 * is created from the Member object. After the Rules are executed, ProviderValidationRules gets updated
	 * with the Drools configured attributes and this is returned to the Caller.
	 * 
	 * @param  ipPayload               The Member JSON Body
	 * @return                         OutputPayload containing Provider Validation Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    Member
	 * @see    MDOProviderValidationService
	 * @see    ProviderValidationRules
	 * @see    ProviderValidationRulesOutputPayload 
	 */
	@ApiOperation(value = "Get Provider Validation Rules for MDO", response = ProviderValidationRulesOutputPayload.class)
    @ApiResponses(
        value = {
        	@ApiResponse(code = 200, message = "SUCCESS"),
        	@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS"),
        	@ApiResponse(code = 700, message = "SERVICE_DOWN"), 
        }
    )
	@RequestMapping(value = "/provider-validation/mdo", method = RequestMethod.POST)
	public ProviderValidationRulesOutputPayload postRequestForProviderValidationMDO(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody Member ipPayload) throws DroolsParseException, JsonProcessingException {
		ProviderValidationRulesOutputPayload opPayload = new ProviderValidationRulesOutputPayload();
		opPayload.setRules(mProviderValidationService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);
		opPayload.setResponseMessage(StaticLoggerMessages.RULES_EXIT_MSG_PROVAL_MDO);

		logger.info(StaticLoggerMessages.RULES_EXIT_MSG_PROVAL_MDO);

		return opPayload;
	}

	/**
	 * This method is used to accept Member request from MDO Build Pool and run Rules on the Payload.
	 * The Member JSON String is converted to Member POJO first and then a MDOPoolingRules Object
	 * is created from the Member object. After the Rules are executed, MDOPoolingRules gets updated
	 * with the Drools configured attributes and this is returned to the Caller.
	 * 
	 * @param  ipPayload               The Member JSON Body
	 * @return                         OutputPayload containing MDO Pooling Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    Member
	 * @see    MDOPoolingService
	 * @see    MDOPoolingRules
	 * @see    MDOPoolingRulesOutputPayload
	 */
	@ApiOperation(value = "Get Pooling Rules for MDO", response = MDOPoolingRulesOutputPayload.class)
    @ApiResponses(
        value = {
        	@ApiResponse(code = 200, message = "SUCCESS"),
        	@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS"),
        	@ApiResponse(code = 700, message = "SERVICE_DOWN"), 
        }
    )
	@RequestMapping(value = "/mdo/pooling", method = RequestMethod.POST)
	public MDOPoolingRulesOutputPayload postRequestForMDOPooling(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody Member ipPayload) throws DroolsParseException, JsonProcessingException {
		MDOPoolingRulesOutputPayload opPayload = new MDOPoolingRulesOutputPayload();
		opPayload.setRules(mdoPoolingService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);
		opPayload.setResponseMessage(StaticLoggerMessages.RULES_EXIT_MSG_MDOPOOL);

		logger.info(StaticLoggerMessages.RULES_EXIT_MSG_MDOPOOL);

		return opPayload;
	}

	/**
	 * This method is used to accept Member request from MDO Scoring and run Rules on the Payload.
	 * The Member JSON String is converted to Member POJO first and then a MDOScoringRules Object
	 * is created from the Member object. After the Rules are executed, MDOScoringRules gets updated
	 * with the Drools configured attributes and this is returned to the Caller.
	 * 
	 * @param  ipPayload               The Member JSON Body
	 * @return                         OutputPayload containing MDO Scoring Rules
	 * @throws DroolsParseException    When Parsing error found in Drools Rule files
	 * @throws JsonProcessingException When problem found while converting Transaction payload to JSON String
	 * @see    Member
	 * @see    MDOScoringService
	 * @see    MDOScoringRules
	 * @see    MDOScoringRulesOutputPayload
	 */
	@ApiOperation(value = "Get Scoring Rules for MDO", response = MDOScoringRulesOutputPayload.class)
    @ApiResponses(
        value = {
        	@ApiResponse(code = 200, message = "SUCCESS"),
        	@ApiResponse(code = 600, message = "OTHER_EXCEPTIONS"),
        	@ApiResponse(code = 700, message = "SERVICE_DOWN"), 
        }
    )
	@RequestMapping(value = "/mdo/scoring", method = RequestMethod.POST)
	public MDOScoringRulesOutputPayload postRequestForMDOScoring(@ApiParam(value = "Member Input Payload for Business Rules Engine", required = true) @Validated @RequestBody Member ipPayload) throws DroolsParseException, JsonProcessingException {
		MDOScoringRulesOutputPayload opPayload = new MDOScoringRulesOutputPayload();
		opPayload.setRules(mdoScoringService.getRules(ipPayload));
		opPayload.setResponseCode(ResponseCodes.SUCCESS);
		opPayload.setResponseMessage(StaticLoggerMessages.RULES_EXIT_MSG_MDOSCORE);

		logger.info(StaticLoggerMessages.RULES_EXIT_MSG_MDOSCORE);

		return opPayload;
	}

	/**
	 * This method is used to handle Exception when an Input Payload Validation error occurs.
	 * 
	 * @param  ex Exception body
	 * @return    OutputPayload containing null body with Error code and message
	 * @see       MethodArgumentNotValidException
	 * @see       ResponseCodes
	 * @see       RulesOutputPayload
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RulesOutputPayload handleException(MethodArgumentNotValidException ex) {
		logger.error(ex.getMessage(), ex);

		RulesOutputPayload outputPayload = new RulesOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
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

	/**
	 * This method is used to handle Exception when a Drools Parse Error occurs.
	 * 
	 * @param  ex Exception body
	 * @return    OutputPayload containing null body with Error code and message
	 * @see       DroolsParseException
	 * @see       ResponseCodes
	 * @see       RulesOutputPayload
	 */
	@ExceptionHandler(DroolsParseException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RulesOutputPayload handleException(DroolsParseException ex) {
		logger.error(ex.getMessage(), ex);

		RulesOutputPayload outputPayload = new RulesOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(ex.getClass().getSimpleName());

		return outputPayload;
	}

	/**
	 * This method is used to handle Exception when a problem occurs during Transaction Audit Service.
	 * 
	 * @param  ex Exception body
	 * @return    OutputPayload containing null body with Error code and message
	 * @see       JsonProcessingException
	 * @see       ResponseCodes
	 * @see       RulesOutputPayload
	 */
	@ExceptionHandler(JsonProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RulesOutputPayload handleException(JsonProcessingException ex) {
		logger.error(ex.getMessage(), ex);

		RulesOutputPayload outputPayload = new RulesOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(ex.getClass().getSimpleName());

		return outputPayload;
	}

	/**
	 * This method is used to handle Exception when any Runtime error occurs.
	 * 
	 * @param  ex Exception body
	 * @return    OutputPayload containing null body with Error code and message
	 * @see       RuntimeException
	 * @see       ResponseCodes
	 * @see       RulesOutputPayload
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public RulesOutputPayload handleException(RuntimeException ex) {
		logger.error(ex.getMessage(), ex);

		RulesOutputPayload outputPayload = new RulesOutputPayload();
		outputPayload.setResponseCode(ResponseCodes.OTHER_EXCEPTIONS);
		outputPayload.setResponseMessage(ex.getClass().getSimpleName());

		return outputPayload;
	}

}
