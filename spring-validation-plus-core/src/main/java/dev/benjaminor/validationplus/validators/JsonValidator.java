package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Json;
import dev.benjaminor.validationplus.support.JsonValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Json}.
 */
public class JsonValidator implements ConstraintValidator<Json, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return JsonValidationUtils.isValidJson(value);
    }
}
