package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.support.CrossFieldConstraintSupport;
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

    private String referenceField;
    private String confirmationField;

    @Override
    public void initialize(Confirmed constraintAnnotation) {
        this.referenceField = CrossFieldConstraintSupport.firstNonBlank(
                constraintAnnotation.field(),
                constraintAnnotation.value());
        this.confirmationField = constraintAnnotation.confirmation();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object target = CrossFieldConstraintSupport.resolveRoot(value, context);
        if (target == null || referenceField.isBlank()) {
            return true;
        }

        String currentField = CrossFieldConstraintSupport.resolveCurrentField(
                context,
                CrossFieldValidationUtils.resolveConfirmationField(referenceField, confirmationField));
        if (currentField.isBlank()) {
            return true;
        }

        String effectiveReferenceField = referenceField;
        if (effectiveReferenceField.isBlank()
                && !CrossFieldConstraintSupport.isClassLevelValidation(value, context)
                && currentField.endsWith("Confirmation")) {
            effectiveReferenceField = currentField.substring(0, currentField.length() - "Confirmation".length());
        }
        if (effectiveReferenceField.isBlank()) {
            return true;
        }

        Object referenceValue = ReflectionUtils.getFieldValue(target, effectiveReferenceField);
        Object confirmationValue = ReflectionUtils.getFieldValue(target, currentField);

        if (EmptyUtils.isEmpty(confirmationValue) || EmptyUtils.isEmpty(referenceValue)) {
            return true;
        }

        if (Objects.equals(referenceValue, confirmationValue)) {
            return true;
        }

        return CrossFieldConstraintSupport.reportViolation(context, value, currentField);
    }
}
