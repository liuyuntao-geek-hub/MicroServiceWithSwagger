package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.RulesRegex;

public class SmartPCPRules extends Rules {

	private String invocationOrder;

	public String getInvocationOrder() {
		return invocationOrder;
	}

	public void setInvocationOrder(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.INVOCATION_ORDER)) {
				invocationOrder = param.trim().toUpperCase();
			}
			else {
				throw new DroolsParseException("Invocation Order must be one of these - 'A/M/AM/MA' in SmartPCP-Invocation-Order-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Invocation Order cannot be empty in SmartPCP-Invocation-Order-Rules.xls");
		}
	}

}
