package com.anthem.hca.smartpcp.mdo.pool.validator;

import java.time.LocalDate;

/**
 * @author AF71274
 *
 */
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
