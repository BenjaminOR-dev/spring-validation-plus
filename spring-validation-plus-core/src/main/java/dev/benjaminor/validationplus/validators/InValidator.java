package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.In;
import dev.benjaminor.validationplus.support.InValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link In}.
 */
public class InValidator implements ConstraintValidator<In, Object> {

    private String[] allowedValues;

    @Override
    public void initialize(In constraintAnnotation) {
        this.allowedValues = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return InValidationUtils.isIn(value, allowedValues);
    }
}
