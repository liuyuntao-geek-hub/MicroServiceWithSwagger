/**
 * @author AF71111
 *
 *	Custom Validator interface to check if either of 2 properties are present
 *	
 */
package com.anthem.hca.smartpcp.mdoprocessing.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.anthem.hca.smartpcp.mdoprocessing.utils.ErrorMessages;



@Documented
@Constraint(validatedBy = ORNotBlankValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ORNotBlank {
	
	String message() default ErrorMessages.INVALID_CONTRACTCODE_OR_NETWRKID;
	
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] orNotBlankProperties();

}
