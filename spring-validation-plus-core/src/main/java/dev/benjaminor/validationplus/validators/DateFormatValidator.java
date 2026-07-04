package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.DateFormat;
import dev.benjaminor.validationplus.support.DateValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateFormatValidator implements ConstraintValidator<DateFormat, Object> {
    private String format;
    @Override
    public void initialize(DateFormat a) { this.format = a.format(); }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return DateValidationUtils.isDateFormat(value, format);
    }
}
