package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;

public class MDOPoolingRules extends Rules {

	private int poolSize;
	private int maxRadiusToPool;
	private String dummyProviderId;

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

}
