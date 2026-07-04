package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.EnumValue;
import dev.benjaminor.validationplus.support.EnumValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private Class<? extends java.lang.Enum<?>> enumClass;
    private boolean ignoreCase;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return EnumValidationUtils.isEnumConstant(value, enumClass, ignoreCase);
    }
}
