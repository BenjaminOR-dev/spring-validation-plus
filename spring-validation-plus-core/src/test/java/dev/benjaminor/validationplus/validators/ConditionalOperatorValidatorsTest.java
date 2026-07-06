package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import dev.benjaminor.validationplus.constraints.ProhibitedIf;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionalOperatorValidatorsTest {

    private jakarta.validation.Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator();
    }

    @Test
    void requiredIfShouldUseEqualsByDefault() {
        RequiredIfEqualsDto dto = new RequiredIfEqualsDto();
        dto.role = "ADMIN";

        Set<ConstraintViolation<RequiredIfEqualsDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void requiredIfShouldRequireWhenNotEquals() {
        RequiredIfNotEqualsDto valid = new RequiredIfNotEqualsDto();
        valid.role = "GUEST";
        valid.adminCode = "code";

        RequiredIfNotEqualsDto invalid = new RequiredIfNotEqualsDto();
        invalid.role = "ADMIN";

        assertTrue(validator.validate(valid).isEmpty());
        assertFalse(validator.validate(invalid).isEmpty());
    }

    @Test
    void requiredIfShouldRequireWhenIn() {
        RequiredIfInDto valid = new RequiredIfInDto();
        valid.role = "USER";
        valid.adminCode = "code";

        RequiredIfInDto invalid = new RequiredIfInDto();
        invalid.role = "ADMIN";

        assertTrue(validator.validate(valid).isEmpty());
        assertFalse(validator.validate(invalid).isEmpty());
    }

    @Test
    void prohibitedIfShouldProhibitWhenNotEquals() {
        ProhibitedIfNotEqualsDto validWithoutNickname = new ProhibitedIfNotEqualsDto();
        validWithoutNickname.role = "ADMIN";

        ProhibitedIfNotEqualsDto validWithGuestRole = new ProhibitedIfNotEqualsDto();
        validWithGuestRole.role = "GUEST";
        validWithGuestRole.nickname = "nick";

        ProhibitedIfNotEqualsDto invalid = new ProhibitedIfNotEqualsDto();
        invalid.role = "ADMIN";
        invalid.nickname = "nick";

        assertTrue(validator.validate(validWithoutNickname).isEmpty());
        assertTrue(validator.validate(validWithGuestRole).isEmpty());
        assertFalse(validator.validate(invalid).isEmpty());
    }

    @RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
    static class RequiredIfEqualsDto {
        private String role;
        private String adminCode;
    }

    @RequiredIf(field = "role", value = "GUEST", operator = ConditionalOperator.NOT_EQUALS, required = "adminCode")
    static class RequiredIfNotEqualsDto {
        private String role;
        private String adminCode;
    }

    @RequiredIf(field = "role", value = "ADMIN,MODERATOR", operator = ConditionalOperator.IN, required = "adminCode")
    static class RequiredIfInDto {
        private String role;
        private String adminCode;
    }

    @ProhibitedIf(field = "role", value = "GUEST", operator = ConditionalOperator.NOT_EQUALS, prohibited = "nickname")
    static class ProhibitedIfNotEqualsDto {
        private String role;
        private String nickname;
    }
}
