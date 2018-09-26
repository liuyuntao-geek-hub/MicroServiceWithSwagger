package com.anthem.hca.smartpcp.drools.rules;

import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;

/**
 * The RulesFactory class is a Factory to create a custom Rules object based on the
 * AgendaGroup provided in it's input. It creates a specific sub-class of Rules object
 * and returns it to the caller.
 * 
 * @author  Saptarshi Dey (AF66853)
 * @version 1.2
 */

public class RulesFactory {

	/**
	 * Private Constructor for RulesFactory.
	 * 
	 * @param None
	 */
	private RulesFactory() {
		throw new IllegalStateException("Cannot instantiate RulesFactory");
	}

	/**
	 * This method is creates a specific Rule object sub-class based on the
	 * AgendaGroup parameter and returns it to the caller.
	 * 
	 * @param  group The Agenda Group of the Rule
	 * @return       Abstract Rules
	 */
	public static AbstractRules createRule(AgendaGroup group) {
		AbstractRules rules = null;

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
		case INVOCATION_SYSTEM_MAPPING:
			rules = new ProviderValidationRules(AgendaGroup.INVOCATION_SYSTEM_MAPPING);
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
