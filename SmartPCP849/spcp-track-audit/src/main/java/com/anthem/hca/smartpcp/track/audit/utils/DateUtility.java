package com.anthem.hca.smartpcp.track.audit.utils;

import java.time.LocalDate;

import org.springframework.context.annotation.Configuration;


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
@Configuration
public class DateUtility {

	/**
	 * @param memberDob
	 * @return boolean
	 */
	public boolean checkFuture(String dateString) {

		LocalDate now = LocalDate.now();
		LocalDate dob = LocalDate.parse(dateString);
		return dob.isAfter(now);
	}
	
	public boolean checkAfterOrSame(String beforeDateString, String afterDateString) {
		LocalDate beforeDate = LocalDate.parse(beforeDateString);
		LocalDate afterDate = LocalDate.parse(afterDateString);
		
		return afterDate.isAfter(beforeDate) || afterDate.isEqual(beforeDate);
	}
}
