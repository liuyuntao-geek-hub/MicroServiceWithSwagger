package com.anthem.hca.smartpcp.util;

import java.time.LocalDate;
import java.time.Period;

import org.apache.commons.lang3.StringUtils;


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
	public static boolean checkFuture(String memberDob) {

		LocalDate now = LocalDate.now();
		LocalDate dob = LocalDate.parse(memberDob);
		return dob.isAfter(now);
	}
	
	/**
	 * Method to Calculate Age
	 * 
	 * @param birthDate
	 * @return age in years
	 */
	public static double calculateAge(String birthDate) {

		LocalDate localDate = null;
		if(!StringUtils.isBlank(birthDate))
		{
		localDate = LocalDate.parse(birthDate);
		localDate = LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
		}
		LocalDate currentDate = LocalDate.now();
		if ((localDate != null) && (currentDate != null)) {
			Period p = Period.between(LocalDate.parse(birthDate), currentDate);
			return (p.getYears()+p.getMonths()/12.0+p.getDays()/365.0);
		} else {
			return 0.0;
		}
	}
	
	public static boolean isAgeUnder18(String dob) {

		boolean flag = true;
		LocalDate currentDate = LocalDate.now();
		Period p = Period.between(LocalDate.parse(dob), currentDate);
		Double age = (p.getYears()+p.getMonths()/12.0+p.getDays()/365.0);//calculating age in years including months and days 
		if (age > 18.0) {
			flag = false;
		}
		return flag;
	}

}
