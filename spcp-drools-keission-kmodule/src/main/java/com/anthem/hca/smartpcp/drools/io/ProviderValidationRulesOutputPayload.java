package com.anthem.hca.smartpcp.drools.io;

import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;

public class ProviderValidationRulesOutputPayload extends RulesOutputPayload {

	private ProviderValidationRules rules;

	public ProviderValidationRules getRules() {
		return rules;
	}

	public void setRules(ProviderValidationRules rules) {
		this.rules = rules;
	}

}
