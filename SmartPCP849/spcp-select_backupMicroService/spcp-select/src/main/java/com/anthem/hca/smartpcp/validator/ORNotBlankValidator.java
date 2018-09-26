package com.anthem.hca.smartpcp.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Custom Validator class to check if either of 2 properties are present.
 * 
 * 
 * @author AF71111
 */
public class ORNotBlankValidator implements ConstraintValidator<ORNotBlank, Object> {

	private String[] orNotBlankProperties;
	private String message;

	@Override
	public void initialize(ORNotBlank constraintAnnotation) {
		this.orNotBlankProperties = constraintAnnotation.orNotBlankProperties();
		this.message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Object validatingObject, ConstraintValidatorContext constraintContext) {

		String[] emptyPropertyArray = Arrays.stream(orNotBlankProperties).filter(property -> {
			List<String> propertyValue = ((List<String>) new BeanWrapperImpl(validatingObject)
					.getPropertyValue(property));
			return (null == propertyValue || propertyValue.isEmpty()) ? true : false;
		}).toArray(String[]::new);

		boolean isValid = (emptyPropertyArray.length < 2) ? true : false;
		if (!isValid) {
			constraintContext.disableDefaultConstraintViolation();
			constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		}

		return isValid;
	}

	String message() {
		return message;

	}
}
