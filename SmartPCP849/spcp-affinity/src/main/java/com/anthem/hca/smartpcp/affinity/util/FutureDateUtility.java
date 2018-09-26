package com.anthem.hca.smartpcp.affinity.util;

import java.time.LocalDate;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			FutureDateUtility  class to verify whether the date of birth is of future date.
 * 
 * @author AF65409 
 */
public class FutureDateUtility {

	/**
	 * @param memberDob
	 * @return boolean
	 * 
	 * 			isFutureDate is used to check whether the date of birth is of future date.
	 */
	public boolean isFutureDate(String memberDob) {

		LocalDate now = LocalDate.now();
		LocalDate dob = LocalDate.parse(memberDob);
		return dob.isAfter(now);
	}
}
