/**
 * @author AF71274
 *
 */
package com.anthem.hca.smartpcp.mdoprocessing.validator;

import java.time.LocalDate;


public class DateValidator {
	
	/**
	 * @param memberDob
	 * @return
	 */
	public boolean checkFuture(String memberDob)
	{
		LocalDate current=LocalDate.now();
		LocalDate dob=LocalDate.parse(memberDob);
		return dob.isAfter(current);
		
	}

}
