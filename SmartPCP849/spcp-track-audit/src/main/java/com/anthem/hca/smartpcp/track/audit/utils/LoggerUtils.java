package com.anthem.hca.smartpcp.track.audit.utils;

/**
 * LoggerUtils is a helper class that is used to clean Log messages before publishing them
 * to Splunk. It contains a single static method which does all the cleaning activities.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class LoggerUtils {

    // NewLine and Carriage Return Replacement values
    protected static final char NEWLINE = '\n';
    protected static final char CARRIAGE_RETURN = '\r';
    protected static final char LINE_WRAP_REPLACE = '_';

    // Value appended to messages if they were changed by the cleaning process
    protected static final String SUFFIX = " (Encoded)";

	/**
	 * This method removes Newline (CRLF) Characters from the provided String and Encodes it for HTML before
	 * returning the 'clean' version to the caller. Note that HTML Encoding requires ESAPI Property
	 * {@value DefaultSecurityConfiguration#LOG_ENCODING_REQUIRED} be set to {@code true}. Since we are not
	 * using a ESAPI Property file, we will always treat this as {@code true} in our method logic.
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
	 * Constructor for LoggerUtils. No visibility modifier so that it can be tested but not called from a different package.
	 * 
	 * @param None
	 */
	LoggerUtils() {
	    throw new IllegalStateException("Cannot instantiate LoggerUtils");
	}

}
