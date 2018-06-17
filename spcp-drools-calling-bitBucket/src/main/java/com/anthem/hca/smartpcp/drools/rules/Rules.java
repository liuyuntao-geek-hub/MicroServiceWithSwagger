package com.anthem.hca.smartpcp.drools.rules;

import org.apache.commons.lang3.StringUtils;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.RulesRegex;

public class Rules {

	private String market;
	private String lob;
	private String product;
	private String assignmentType;
	private String assignmentMethod;
	private boolean fallbackRequired = true;

	public String getMarket() {
		return market;
	}

	public void setMarket(String param) {
		market = param.trim();
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String param) {
		lob = param.trim();
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String param) {
		product = param.trim();
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String param) {
		assignmentType = param.trim();
		assignmentType = assignmentType.replaceFirst("^n|N$", "New").replaceFirst("^r|R$", "Re-Enrolled").replaceFirst("^e|E$", "Existing");
	}

	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	public void setAssignmentMethod(String param) {
		assignmentMethod = param.trim();
		assignmentMethod = assignmentMethod.replaceFirst("^o|O$", "Online").replaceFirst("^b|B$", "Batch");
	}

	public boolean isFallbackRequired() {
        return fallbackRequired;
	}

	public void setToActualOrFallback(String param) throws DroolsParseException {
		System.out.println("Actual and fall back.........");
		if (StringUtils.isNotEmpty(param) && StringUtils.isNotBlank(param)) {
			if (param.matches(RulesRegex.ACTUAL_FALLBACK)) {
				fallbackRequired = !param.trim().equalsIgnoreCase("ACTUAL");
			}
			else {
				throw new DroolsParseException("Actual or Fallback must be one of these - 'ACTUAL/FALLBACK' in Actual-Or-Fallback-Configuration-Rules.xls");
			}
		}
		else {
			throw new DroolsParseException("Actual or Fallback cannot be empty in Actual-Or-Fallback-Configuration-Rules.xls");
		}
	}
	
	public void setFallbackRequired(boolean fallback) {
		fallbackRequired = fallback;
	}

}
