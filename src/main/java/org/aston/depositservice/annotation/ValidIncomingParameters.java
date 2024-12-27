package org.aston.depositservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.aston.depositservice.validation.IncomingParametersValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_PARAMETER;
import static org.aston.depositservice.configuration.ApplicationConstant.UNSUPPORTED_DATA_TYPE;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IncomingParametersValidator.class)
public @interface ValidIncomingParameters {

    String message() default UNSUPPORTED_DATA_TYPE;

    String messageForMissingParameter() default MISSING_PARAMETER;

    String regexp() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

