package org.aston.depositservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.aston.depositservice.annotation.ValidIncomingParameters;

public class IncomingParametersValidator implements ConstraintValidator<ValidIncomingParameters, String> {

    private String regexp;

    private String messageForMissingParameter;

    private String invalidRequestMessage;


    @Override
    public void initialize(ValidIncomingParameters constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
        this.messageForMissingParameter = constraintAnnotation.messageForMissingParameter();
        this.invalidRequestMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageForMissingParameter)
                    .addConstraintViolation();
            return false;
        }

        if (!value.matches(regexp)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(invalidRequestMessage)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

