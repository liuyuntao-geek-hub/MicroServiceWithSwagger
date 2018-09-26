package com.anthem.hca.smartpcp.affinity.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.anthem.hca.smartpcp.affinity.constants.ErrorMessages;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ORNotBlank is used to create Custom Validator interface to check if either of 2 properties are present
 * 
 * @author AF65409 
 */
@Documented
@Constraint(validatedBy = OrNotBlankValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrNotBlank {

	String message() default ErrorMessages.MISSING_CONTRACTCODE_OR_NETWRKID;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] orNotBlankProperties();

}
