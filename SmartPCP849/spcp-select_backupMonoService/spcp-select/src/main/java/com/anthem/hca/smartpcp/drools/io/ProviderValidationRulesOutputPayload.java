package com.anthem.hca.smartpcp.drools.io;

import io.swagger.annotations.ApiModelProperty;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;

/**
 * The ProviderValidationRulesOutputPayload class encapsulates the Output Payload
 * for Provider Validation Rules that is returned as a JSON object to the caller Service. 
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class ProviderValidationRulesOutputPayload extends RulesOutputPayload {

	@ApiModelProperty(required = true, dataType = "ProviderValidationRules", notes = "Contains Provider Validation Rules")
	private ProviderValidationRules rules;

	/**
	 * This method is used to get the ProviderValidationRules from the Output Payload.
	 * 
	 * @param  None
	 * @return Provider Validation Rules
	 * @see    ProviderValidationRules
	 */
	public ProviderValidationRules getRules() {
		return rules;
	}

	/**
	 * This method is used to set the ProviderValidationRules in the Output Payload.
	 * 
	 * @param  rules Provider Validation Rules
	 * @return None
	 * @see    ProviderValidationRules
	 */
	public void setRules(ProviderValidationRules rules) {
		this.rules = rules;
	}

}
