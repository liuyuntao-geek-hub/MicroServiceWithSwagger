package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;
import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

/**
 * The MDOPoolingRules class is used to encapsulate all the properties and behaviors of
 * a MDO Pooling and Dummy PCP Rule. It extends the base Rules class and also provides a
 * custom implementation of the isFallbackRequired() method.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.4
 */

public class MDOPoolingRules extends AbstractRules {

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Pool Size based on which Providers will be searched")
	private int poolSize;

	@ApiModelProperty(required = true, dataType = "int", notes = "Contains the Distance Radius within which the Providers will be searched")
	private int maxRadiusToPool;

	@ApiModelProperty(required = true, dataType = "String", notes = "Contains the Dummy PCP Id to consider if no Provider is found")
	private String dummyProviderId;

	public MDOPoolingRules(AgendaGroup ag) {
		setAgendaGroup(ag);
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				poolSize = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Pool Size cannot be empty in MDO-Provider-Pooling-Rules.xls");
		}
	}

	public int getMaxRadiusToPool() {
		return maxRadiusToPool;
	}

	public void setMaxRadiusToPool(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			try {
				maxRadiusToPool = Integer.parseInt(param.trim());
			} catch (NumberFormatException nfe) {
				throw new DroolsParseException(nfe.getMessage());
			}
		}
		else {
			throw new DroolsParseException("Maximum Radius cannot be empty in MDO-Provider-Pooling-Rules.xls");
		}
	}

	public String getDummyProviderId() {
		return dummyProviderId;
	}

	public void setDummyProviderId(String param) throws DroolsParseException {
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			dummyProviderId = param.trim();
		}
		else {
			throw new DroolsParseException("Dummy PCP cannot be empty in MDO-Provider-Pooling-Dummy-Pcp-Rules");
		}
	}

	@Override
	public void setMarketParams(Member m) {
		/*
		 * Business Requirement:
		 * 
		 * For Dummy PCP Rule: Use the LOB that is received in the Input Payload
		 * For All other Rules: Ignore the LOB from the Input Payload. Use 'Commercial' for Phase-1
		 * Also, for Dummy PCP Rule, we just need the LOB to fire the rule. Other Market parameters are not required.
		 */
		if (getAgendaGroup() == AgendaGroup.DUMMYPCP) {
			setLob(m.getMemberLineOfBusiness());
		}
		else {
			super.setMarketParams(m);
		}
	}

	@Override
	@JsonIgnore
	public boolean isFallbackRequired() {
		return (getAgendaGroup() == AgendaGroup.MDO_POOLING)
			? poolSize == 0 || maxRadiusToPool == 0
			: false		// For Dummy PCP, Fallback rules are never required
		;
	}

}
