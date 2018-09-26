package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

/**
 * The SmartPCPRules class is used to encapsulate all the properties and behaviors of
 * a Smart PCP Rule. It extends the base AbstractRules class and also provides a custom
 * implementation of the isFallbackRequired() method.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.4
 */

public class SmartPCPRules extends AbstractRules {

	@ApiModelProperty(required = true, dataType = "String", notes = "Contains the Invocation Order (A/M/AM/MA) of Affinity and MDO")
	private String invocationOrder;

	public SmartPCPRules() {
		setAgendaGroup(AgendaGroup.SMARTPCP);
	}

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

	@Override
	@JsonIgnore
	public boolean isFallbackRequired() {
		return invocationOrder == null;
	}

}
