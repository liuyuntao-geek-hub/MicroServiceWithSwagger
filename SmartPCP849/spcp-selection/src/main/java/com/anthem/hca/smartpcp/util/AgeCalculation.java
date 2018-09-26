package com.anthem.hca.smartpcp.util;

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
}
