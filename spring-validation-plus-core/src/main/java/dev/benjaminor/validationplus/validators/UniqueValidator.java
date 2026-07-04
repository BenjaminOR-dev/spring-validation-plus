package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Unique;
import dev.benjaminor.validationplus.spi.UniqueCheckRequest;
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.DatabaseValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para {@link Unique}.
 */
public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    private Class<?> entity;
    private String field;
    private String column;
    private String excludeField;
    private String excludeParameter;
    private String excludeColumn;
    private boolean ignoreCase;

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
        this.field = constraintAnnotation.field();
        this.column = constraintAnnotation.column();
        this.excludeField = constraintAnnotation.excludeField();
        this.excludeParameter = constraintAnnotation.excludeParameter();
        this.excludeColumn = constraintAnnotation.excludeColumn();
        this.ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(dto, field);
        if (EmptyUtils.isEmpty(fieldValue)) {
            return true;
        }

        UniquenessChecker checker = ValidationPlusCheckers.uniquenessChecker().orElse(null);
        if (checker == null) {
            return DatabaseValidationUtils.failCheckerMissing(
                    context, field, "dev.benjaminor.validationplus.constraints.Unique.checkerMissing");
        }

        Object excludeId = DatabaseValidationUtils.resolveExcludeId(dto, excludeField, excludeParameter);
        UniqueCheckRequest request = new UniqueCheckRequest(
                entity, column, fieldValue, excludeId, excludeColumn, ignoreCase);

        if (checker.isUnique(request)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, field);
    }
}
