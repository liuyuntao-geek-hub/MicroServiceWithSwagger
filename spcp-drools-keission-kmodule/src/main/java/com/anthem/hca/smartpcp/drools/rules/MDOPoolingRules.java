package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;

import com.anthem.hca.smartpcp.drools.model.Member;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MDOPoolingRules extends Rules {

	private int poolSize;
	private int maxRadiusToPool;
	private String dummyProviderId;

	private boolean fallbackForPooling;
	private boolean fallbackForDummy;

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

	@JsonIgnore
	public boolean getFallbackForPooling() { return fallbackForPooling; }

	public void setFallbackForPooling() {
		fallbackForPooling = 
			getMarket().equals(FALLBACK_PARAM)
			&& getLob().equals(FALLBACK_PARAM)
			&& getProduct().equals(FALLBACK_PARAM)
			&& getAssignmentType().equals(FALLBACK_PARAM)
			&& getAssignmentMethod().equals(FALLBACK_PARAM)
		;
	}

	@JsonIgnore
	public boolean getFallbackForDummy() { return fallbackForDummy; }

	public void setFallbackForDummy() {
		fallbackForDummy = getLob().equals(FALLBACK_PARAM);
	}

	@JsonIgnore
	public boolean isFallback(AgendaGroup group) {
		return (group == AgendaGroup.DUMMYPCP) ? getFallbackForDummy() : getFallbackForPooling();
	}

	@Override
	public void setFallback() {
		if (getAgendaGroup() == AgendaGroup.DUMMYPCP) {
			setLob(FALLBACK_PARAM);
		}
		else {
			super.setFallback();
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
			: dummyProviderId == null
		;
	}

}
