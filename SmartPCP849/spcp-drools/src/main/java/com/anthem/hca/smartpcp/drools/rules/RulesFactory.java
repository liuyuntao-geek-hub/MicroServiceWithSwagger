package com.anthem.hca.smartpcp.drools.rules;

/**
 * The RulesFactory class is a Factory to create a custom Rules object based on the
 * AgendaGroup provided in it's input. It creates a specific sub-class of Rules object
 * and returns it to the caller.
 * 
 * @author  Saptarshi Dey (AF66853)
 * @version 1.2
 */

public class RulesFactory {

	private RulesFactory() {
		throw new IllegalStateException("Cannot instantiate RulesFactory");
	}

	/**
	 * This method is creates a specific Rule object sub-class based on the
	 * AgendaGroup parameter and returns it to the caller 
	 * 
	 * @param AgendaGroup
	 * @return Rules
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

	/*public static AbstractRules createRule(AgendaGroup group, boolean treatPoolingAndPVAsOne) {
		AbstractRules rules = null;

		switch (group) {
		case SMARTPCP:
			rules = new SmartPCPRules();
			break;
		case AFFINITY_PROVIDER_VALIDATION:
			rules = new ProviderValidationRules(AgendaGroup.AFFINITY_PROVIDER_VALIDATION);
			break;
		case MDO_PROVIDER_VALIDATION:
			if (treatPoolingAndPVAsOne) {
				rules = new MDOPoolingAndPVRules(AgendaGroup.MDO_PROVIDER_VALIDATION);
			}
			else {
				rules = new ProviderValidationRules(AgendaGroup.MDO_PROVIDER_VALIDATION);
			}
			break;
		case INVOCATION_SYSTEM_MAPPING:
			if (treatPoolingAndPVAsOne) {
				rules = new MDOPoolingAndPVRules(AgendaGroup.INVOCATION_SYSTEM_MAPPING);
			}
			else {
				rules = new ProviderValidationRules(AgendaGroup.INVOCATION_SYSTEM_MAPPING);
			}
			break;
		case MDO_POOLING:
			if (treatPoolingAndPVAsOne) {
				rules = new MDOPoolingAndPVRules(AgendaGroup.MDO_POOLING);
			}
			else {
				rules = new MDOPoolingRules(AgendaGroup.MDO_POOLING);
			}
			break;
		case DUMMYPCP:
			if (treatPoolingAndPVAsOne) {
				rules = new MDOPoolingAndPVRules(AgendaGroup.DUMMYPCP);
			}
			else {
				rules = new MDOPoolingRules(AgendaGroup.DUMMYPCP);
			}
			break;
		case MDO_SCORING:
			rules = new MDOScoringRules();
			break;
		}

		return rules;
	}*/

}
