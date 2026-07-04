package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Password;
import dev.benjaminor.validationplus.support.PasswordValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, Object> {
    private int min;
    private boolean letters;
    private boolean mixedCase;
    private boolean numbers;
    private boolean symbols;
    @Override
    public void initialize(Password a) {
        this.min = a.min(); this.letters = a.letters(); this.mixedCase = a.mixedCase();
        this.numbers = a.numbers(); this.symbols = a.symbols();
    }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return PasswordValidationUtils.isValid(value, min, letters, mixedCase, numbers, symbols);
    }
}
