package com.anthem.hca.smartpcp.drools.rules;

import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.rules.ProviderValidationRules;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.rules.MDOPoolingRules;
import com.anthem.hca.smartpcp.drools.rules.MDOScoringRules;
import com.anthem.hca.smartpcp.drools.service.DroolsService.AgendaGroup;

public class RulesFactory {

	private static final String DEFAULT_VALUE = "ALL";
	private static final String ISG_SOURCE_SYSTEM = "ISG";
	private static final String WGS_SOURCE_SYSTEM = "WGS";

	private RulesFactory() {
		throw new IllegalStateException("Cannot instantiate RulesFactory");
	}

	public static Rules createRule(AgendaGroup group) {
		Rules rules = null;

		switch (group) {
		case ACTUAL_FALLBACK:
			rules = new Rules();
			break;
		case SMARTPCP:
			rules = new SmartPCPRules();
			break;
		case AFFINITY_PROVIDER_VALIDATION:
			rules = new ProviderValidationRules();
			break;
		case MDO_PROVIDER_VALIDATION:
			rules = new ProviderValidationRules();
			break;
		case MDO_POOLING:
			rules = new MDOPoolingRules();
			break;
		case MDO_SCORING:
			rules = new MDOScoringRules();
			break;
		default:
			rules = new Rules();
			break;
		}

		return rules;
	}

	public static Rules initRule(Rules rules, Member mem) {
		rules.setMarket(mem.getMemberProcessingState());
		rules.setLob(mem.getMemberLineOfBusiness());

		if (ISG_SOURCE_SYSTEM.equalsIgnoreCase(mem.getMemberSourceSystem())) {
			rules.setProduct(mem.getMemberISGProductGroup());
		}
		else if (WGS_SOURCE_SYSTEM.equalsIgnoreCase(mem.getMemberSourceSystem())) {
			rules.setProduct(mem.getMemberWGSGroup());
		}

		rules.setAssignmentType(mem.getMemberType());
		rules.setAssignmentMethod(mem.getSystemType());

		return rules;
	}

	public static Rules initRule(Rules rules, Member mem, boolean fallback) {
		if (fallback) {
			rules.setMarket(DEFAULT_VALUE);
			rules.setLob(DEFAULT_VALUE);
			rules.setProduct(DEFAULT_VALUE);
			rules.setAssignmentType(DEFAULT_VALUE);
			rules.setAssignmentMethod(DEFAULT_VALUE);			
		}
		else {
			rules.setMarket(mem.getMemberProcessingState());
			rules.setLob(mem.getMemberLineOfBusiness());

			if (ISG_SOURCE_SYSTEM.equalsIgnoreCase(mem.getMemberSourceSystem())) {
				rules.setProduct(mem.getMemberISGProductGroup());
			}
			else if (WGS_SOURCE_SYSTEM.equalsIgnoreCase(mem.getMemberSourceSystem())) {
				rules.setProduct(mem.getMemberWGSGroup());
			}

			rules.setAssignmentType(mem.getMemberType());
			rules.setAssignmentMethod(mem.getSystemType());
		}

		rules.setFallbackRequired(fallback);

		return rules;
	}

}
