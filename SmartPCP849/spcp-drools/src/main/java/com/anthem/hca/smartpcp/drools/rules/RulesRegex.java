package com.anthem.hca.smartpcp.drools.rules;

/**
 * The RulesRegex class is used to store all Regular Expressions
 * that are checked while parsing data from the Excel Business Rules.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class RulesRegex {

	private RulesRegex() {
	    throw new IllegalStateException("Cannot instantiate RulesRegex");
	}

	public static final String ACTUAL_FALLBACK = "(?i)[\\s]*ACTUAL[\\s]*|[\\s]*FALLBACK[\\s]*";
	public static final String INVOCATION_ORDER = "(?i)[\\s]*A[\\s]*|[\\s]*M[\\s]*|[\\s]*AM[\\s]*|[\\s]*MA[\\s]*";
	public static final String PRIMARY_SPECIALTIES = "[\\s]*,[\\s]*";
	public static final String GENDER = "(?i)[\\s]*M[\\s]*|[\\s]*F[\\s]*|[\\s]*MALE[\\s]*|[\\s]*FEMALE[\\s]*";
	public static final String PROVIDER_TIERS = "[\\s]*,[\\s]*";
	public static final String YES_NO = "(?i)[\\s]*Y[\\s]*|[\\s]*N[\\s]*|[\\s]*YES[\\s]*|[\\s]*NO[\\s]*";
	public static final String PROXIMITY = "[\\s]*[\\d]+[\\s]*-[\\s]*[\\d]+[\\s]*|[\\s]*[\\d]+[\\s]*\\+[\\s]*";
	public static final String DOUBLE_QUOTES = "^\"|\"$";

}
