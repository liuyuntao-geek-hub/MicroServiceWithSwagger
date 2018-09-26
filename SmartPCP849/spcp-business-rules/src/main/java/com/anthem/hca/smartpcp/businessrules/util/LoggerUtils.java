package com.anthem.hca.smartpcp.businessrules.util;

/**
 * LoggerUtils is a helper class that is used to clean Log messages before publishing them
 * to Splunk. It contains a single static method which does all the cleaning activities.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.2
 */

public class LoggerUtils {

    // NewLine and Carriage Return Replacement values
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final char LINE_WRAP_REPLACE = '_';
    private static final String SUFFIX = " (Encoded)";

	/**
	 * This method removes Newline (CRLF) Characters from the provided String returning
	 * the 'clean' version to the caller.
	 * 
	 * @param  message Original String message to clean
	 * @return         Cleaned String message
	 */
    public static String cleanMessage(String message) {
        // ensure no CRLF injection into logs for forging records
        String clean = message.replace(NEWLINE, LINE_WRAP_REPLACE).replace(CARRIAGE_RETURN, LINE_WRAP_REPLACE);

		if (!message.equals(clean)) {
			clean += SUFFIX;
        }

        return clean;
    }

	/**
	 * Private Constructor for LoggerUtils.
	 * 
	 * @param None
	 */
	private LoggerUtils() {
	    throw new IllegalStateException("Cannot instantiate LoggerUtils");
	}

}
