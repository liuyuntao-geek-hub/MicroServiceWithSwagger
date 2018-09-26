package com.anthem.hca.smartpcp.drools.io;

import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingProviderValidationRules;

/**
 * The MDOPoolingProviderValidationRulesOutputPayload class encapsulates the Output Payload
 * for MDO Pooling and Provider Validation Rules that is returned as a JSON object to the caller Service. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class MDOPoolingProviderValidationRulesOutputPayload extends RulesOutputPayload {

	@ApiModelProperty(required = true, dataType = "MDOPoolingProviderValidationRules", notes = "Contains MDO Pooling and Provider Validation Rules")
	private MDOPoolingProviderValidationRules rules;

	/**
	 * This method is used to get the MDOPoolingProviderValidationRules from the Output Payload.
	 * 
	 * @param  None
	 * @return MDO Pooling and Provider Validation Rules
	 * @see    MDOPoolingProviderValidationRules
	 */
	public MDOPoolingProviderValidationRules getRules() {
		return rules;
	}

	/**
	 * This method is used to set the MDOPoolingProviderValidationRules in the Output Payload.
	 * 
	 * @param  rules MDO Pooling and Provider Validation Rules
	 * @return None
	 * @see    MDOPoolingProviderValidationRules
	 */
	public void setRules(MDOPoolingProviderValidationRules rules) {
		this.rules = rules;
	}

}
