package com.anthem.hca.smartpcp.drools.io;

import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;

public class MDOScoringRulesOutputPayload extends RulesOutputPayload {

	private MDOScoringRules rules;

	public MDOScoringRules getRules() {
		return rules;
	}

	public void setRules(MDOScoringRules rules) {
		this.rules = rules;
	}

}
