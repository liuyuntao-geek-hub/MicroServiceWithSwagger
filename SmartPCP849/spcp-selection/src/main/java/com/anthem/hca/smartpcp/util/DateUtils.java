package com.anthem.hca.smartpcp.util;

import java.time.LocalDate;


/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Utility class to verify whether the date of birth is of future date.
 * 
 * 
 * @author AF71111
 */
public class DateUtils {

	/**
	 * @param memberDob
	 * @return boolean
	 */
	public boolean checkFuture(String memberDob) {

		LocalDate now = LocalDate.now();
		LocalDate dob = LocalDate.parse(memberDob);
		return dob.isAfter(now);
	}
	

}
