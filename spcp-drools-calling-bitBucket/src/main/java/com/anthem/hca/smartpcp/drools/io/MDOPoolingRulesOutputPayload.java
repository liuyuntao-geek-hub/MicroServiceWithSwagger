package com.anthem.hca.smartpcp.drools.io;

import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;

public class MDOPoolingRulesOutputPayload extends RulesOutputPayload {

	private MDOPoolingRules rules;

	public MDOPoolingRules getRules() {
		return rules;
	}

	public void setRules(MDOPoolingRules rules) {
		this.rules = rules;
	}

}
