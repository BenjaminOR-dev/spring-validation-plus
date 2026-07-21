package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Exists;
import dev.benjaminor.validationplus.spi.ExistenceCheckRequest;
import dev.benjaminor.validationplus.spi.ExistenceChecker;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import dev.benjaminor.validationplus.support.CrossFieldValidationUtils;
import dev.benjaminor.validationplus.support.DatabaseValidationUtils;
import dev.benjaminor.validationplus.support.EmptyUtils;
import dev.benjaminor.validationplus.support.ReflectionUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Exists}.
 */
public class ExistsValidator implements ConstraintValidator<Exists, Object> {

    private Class<?> entity;
    private String field;
    private String column;
    private String parameter;
    private boolean ignoreCase;
    private String persistenceUnit;

    @Override
    public void initialize(Exists constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
        this.field = constraintAnnotation.field();
        this.column = constraintAnnotation.column();
        this.parameter = constraintAnnotation.parameter();
        this.ignoreCase = constraintAnnotation.ignoreCase();
        this.persistenceUnit = constraintAnnotation.persistenceUnit();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        Object fieldValue = ReflectionUtils.getFieldValue(dto, field);
        if (EmptyUtils.isEmpty(fieldValue)) {
            fieldValue = DatabaseValidationUtils.resolveContextValue(parameter).orElse(null);
        }
        if (EmptyUtils.isEmpty(fieldValue)) {
            return true;
        }

        ExistenceChecker checker = ValidationPlusCheckers.existenceChecker().orElse(null);
        if (checker == null) {
            return DatabaseValidationUtils.failCheckerMissing(
                    context, field, "dev.benjaminor.validationplus.constraints.Exists.checkerMissing");
        }

        ExistenceCheckRequest request = new ExistenceCheckRequest(
                entity, column, fieldValue, ignoreCase, persistenceUnit);

        if (checker.exists(request)) {
            return true;
        }

        return CrossFieldValidationUtils.failOnField(context, field);
    }
}
