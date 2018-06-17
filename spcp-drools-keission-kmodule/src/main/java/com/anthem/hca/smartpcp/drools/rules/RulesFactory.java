package com.anthem.hca.smartpcp.drools.rules;

import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;

public class RulesFactory {

	private RulesFactory() {
		throw new IllegalStateException("Cannot instantiate RulesFactory");
	}

	public static Rules createRule(AgendaGroup group) {
		Rules rules = null;

		switch (group) {
		case SMARTPCP:
			rules = new SmartPCPRules();
			break;
		case AFFINITY_PROVIDER_VALIDATION:
			rules = new ProviderValidationRules(AgendaGroup.AFFINITY_PROVIDER_VALIDATION);
			break;
		case MDO_PROVIDER_VALIDATION:
			rules = new ProviderValidationRules(AgendaGroup.MDO_PROVIDER_VALIDATION);
			break;
		case MDO_POOLING:
			rules = new MDOPoolingRules(AgendaGroup.MDO_POOLING);
			break;
		case DUMMYPCP:
			rules = new MDOPoolingRules(AgendaGroup.DUMMYPCP);
			break;
		case MDO_SCORING:
			rules = new MDOScoringRules();
			break;
		}

		return rules;
	}

}
