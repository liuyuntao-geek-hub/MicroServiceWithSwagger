package com.anthem.hca.smartpcp.affinity.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ORNotBlankValidator is used to create Custom Validator interface to check if either of 2 properties are present
 * 
 * @author AF65409 
 */
public class OrNotBlankValidator implements ConstraintValidator<OrNotBlank, Object> {

	private String[] orNotBlankProperties;
	private String message;

	@Override
	public void initialize(OrNotBlank constraintAnnotation) {
		this.orNotBlankProperties = constraintAnnotation.orNotBlankProperties();
		this.message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Object validatingObject, ConstraintValidatorContext constraintContext) {

		String[] emptyPropertyArray = Arrays.stream(orNotBlankProperties).filter(property -> {
			List<?> propertyValue = ((List<?>) new BeanWrapperImpl(validatingObject).getPropertyValue(property));
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