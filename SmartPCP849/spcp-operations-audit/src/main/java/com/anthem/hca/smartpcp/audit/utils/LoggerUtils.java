package com.anthem.hca.smartpcp.audit.utils;

import org.owasp.esapi.codecs.HTMLEntityCodec;

/**
 * LoggerUtils is a helper class that is used to clean Log messages before publishing them
 * to Splunk. It contains a single static method which does all the cleaning activities.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class LoggerUtils {

    // NewLine and Carriage Return Replacement values
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final char LINE_WRAP_REPLACE = '_';

    // HTML Encoding Configuration
    private static final char BACKSLASH = '\\';
    private static final char OPEN_SLF_FORMAT = '{';
    private static final char CLOSE_SLF_FORMAT = '}';
    private static final char[] IMMUNE_SLF4J_HTML = { ',', '.', '-', '_', ' ', ':', BACKSLASH, OPEN_SLF_FORMAT, CLOSE_SLF_FORMAT };
    private static final HTMLEntityCodec SLF4J_HTML_CODEC = new HTMLEntityCodec();

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

		//Use a Customized HTML Encoder to exclude SLF4J syntax markers for data replacement
		clean = SLF4J_HTML_CODEC.encode(IMMUNE_SLF4J_HTML, clean);

		if (!message.equals(clean)) {
			clean += " (Encoded)";
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
