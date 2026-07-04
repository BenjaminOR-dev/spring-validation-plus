package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.NotIn;
import dev.benjaminor.validationplus.support.InValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link NotIn}.
 */
public class NotInValidator implements ConstraintValidator<NotIn, Object> {

    private String[] forbiddenValues;

    @Override
    public void initialize(NotIn constraintAnnotation) {
        this.forbiddenValues = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return InValidationUtils.isNotIn(value, forbiddenValues);
    }
}
