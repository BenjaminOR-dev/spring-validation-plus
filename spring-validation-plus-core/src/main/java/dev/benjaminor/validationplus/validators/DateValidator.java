package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Date;
import dev.benjaminor.validationplus.support.DateValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Date}.
 */
public class DateValidator implements ConstraintValidator<Date, Object> {

    private String format;

    @Override
    public void initialize(Date constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return DateValidationUtils.isDate(value, format);
    }
}
