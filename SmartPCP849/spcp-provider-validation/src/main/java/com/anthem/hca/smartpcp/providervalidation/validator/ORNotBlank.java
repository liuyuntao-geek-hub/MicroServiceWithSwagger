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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import com.anthem.hca.smartpcp.providervalidation.constants.ErrorMessages;

/**
 * @author AF71111
 *
 *	Custom Validator interface to check if either of 2 properties are present
 *	
 */
@Documented
@Constraint(validatedBy = ORNotBlankValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ORNotBlank {
	
	String message() default ErrorMessages.MISSING_CONTRACTCODE_OR_NETWRKID;
	
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] orNotBlankProperties();

}


