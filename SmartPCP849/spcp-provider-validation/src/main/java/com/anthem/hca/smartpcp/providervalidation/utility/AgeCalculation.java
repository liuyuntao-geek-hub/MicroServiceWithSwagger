/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - This java class is used for provider validation
 * 
 * 
 * @author AF69961
 */
package com.anthem.hca.smartpcp.providervalidation.utility;

import java.time.LocalDate;
import java.time.Period;

import org.apache.commons.lang3.StringUtils;

public class AgeCalculation {

	private AgeCalculation() {

	}

	/**
	 * Method to Calculate Age
	 * 
	 * @param birthDate
	 * @return age in years
	 */
	public static int calculateAge(String birthDate) {

		LocalDate localDate = null;
		int returnValue = 0;
		
		if(!StringUtils.isBlank(birthDate))
		{
		localDate = LocalDate.parse(birthDate);
		localDate = LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
		}
		LocalDate currentDate = LocalDate.now();
		if ((localDate != null) && (currentDate != null)) {
			returnValue = Period.between(localDate, currentDate).getYears();
		} else {
			returnValue = 0;
		}
		
		return returnValue;
	}

}
