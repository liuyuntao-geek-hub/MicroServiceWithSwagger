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
package com.anthem.hca.smartpcp.providervalidation.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;


/**
 * @author AF71111
 *
 *	Custom Validator class to check if either of 2 properties are present
 *	
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

		String [] emptyPropertyArray = Arrays.stream(orNotBlankProperties).filter(property -> {
			List<String> propertyValue = ((List<String>) new BeanWrapperImpl(validatingObject).getPropertyValue(property));
			return (null == propertyValue || propertyValue.isEmpty()) ? true : false;
		}).toArray(String[]::new);

		boolean isValid = (emptyPropertyArray.length < 2) ? true : false;
		if (!isValid) {
			message = message.replace("[]", Arrays.toString(emptyPropertyArray));
			constraintContext.disableDefaultConstraintViolation();
			constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		}

		return isValid;
	}

	String message() {
		return message;

	}
}
