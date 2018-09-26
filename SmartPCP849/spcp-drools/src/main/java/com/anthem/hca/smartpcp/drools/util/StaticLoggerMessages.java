package com.anthem.hca.smartpcp.drools.util;

/**
 * The LoggerMessages class is used to store all Logger Messages
 * as well as the Success Response in the Output Payload.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class StaticLoggerMessages {

	public static final String RULES_EXIT_MSG_SMARTPCP = "Rules extracted for SmartPCP Invocation Order";
	public static final String RULES_EXIT_MSG_PROVAL_AFFINITY = "Rules extracted for Affinity Provider Validation";
	public static final String RULES_EXIT_MSG_PROVAL_MDO = "Rules extracted for MDO Provider Validation";
	public static final String RULES_EXIT_MSG_MDOPOOL = "Rules extracted for MDO Build Pool";
	public static final String RULES_EXIT_MSG_MDOSCORE = "Rules extracted for MDO Scoring";
	public static final String KIESESSION_DISPOSE = "KieSession exists. Disposing the old KieSession";
	public static final String FIRE_RULES = "Firing Rules for ";
	public static final String CONTENT_ERROR = "Error getting contents of ";
	public static final String FILE_DOWNLOADED = "Downloaded ";

	/**
	 * Private Constructor for LoggerMessages.
	 * 
	 * @param None
	 */
	private StaticLoggerMessages() {
		throw new IllegalStateException("Cannot instantiate StaticLoggerMessages");
	}

}
