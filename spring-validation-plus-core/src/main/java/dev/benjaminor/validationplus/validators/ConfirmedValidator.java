package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * Validator for {@link Confirmed}.
 */
public class ConfirmedValidator implements ConstraintValidator<Confirmed, Object> {

    private String field;
    private String confirmationField;

    @Override
    public void initialize(Confirmed constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.confirmationField = CrossFieldValidationUtils.resolveConfirmationField(
                constraintAnnotation.field(),
                constraintAnnotation.confirmation());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(value, field);
        Object confirmationValue = ReflectionUtils.getFieldValue(value, confirmationField);

        if (EmptyUtils.isEmpty(fieldValue) && EmptyUtils.isEmpty(confirmationValue)) {
            return true;
        }

        if (Objects.equals(fieldValue, confirmationValue)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, confirmationField);
    }
}
