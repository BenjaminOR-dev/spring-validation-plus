package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.constraints.Different;
import dev.benjaminor.validationplus.constraints.ProhibitedIf;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.constraints.RequiredIfAccepted;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.Same;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FieldLevelCrossFieldValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void requiredWithOnFieldShouldRequireAnnotatedPropertyWhenCompanionIsPresent() {
        FieldLevelRequiredWithDto dto = new FieldLevelRequiredWithDto();
        dto.setNewPassword("secret");

        Set<ConstraintViolation<FieldLevelRequiredWithDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<FieldLevelRequiredWithDto> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("oldPassword");
        assertThat(violation.getMessage()).contains("newPassword");
        assertThat(violation.getMessage()).doesNotContain("{other}");
    }

    @Test
    void requiredIfOnFieldShouldRequireAnnotatedPropertyWhenConditionMatches() {
        FieldLevelRequiredIfDto dto = new FieldLevelRequiredIfDto();
        dto.role = "ADMIN";

        Set<ConstraintViolation<FieldLevelRequiredIfDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("adminCode");
        assertThat(violations.iterator().next().getMessage()).contains("role");
        assertThat(violations.iterator().next().getMessage()).doesNotContain("{other}");
    }

    @Test
    void sameOnFieldShouldValidateAgainstReferenceField() {
        FieldLevelSameDto dto = new FieldLevelSameDto();
        dto.password = "secret";
        dto.passwordConfirmation = "other";

        Set<ConstraintViolation<FieldLevelSameDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("passwordConfirmation");
        assertThat(violations.iterator().next().getMessage()).contains("password");
        assertThat(violations.iterator().next().getMessage()).doesNotContain("{other}");
    }

    @Test
    void differentOnFieldShouldValidateAgainstReferenceField() {
        FieldLevelDifferentDto dto = new FieldLevelDifferentDto();
        dto.email = "user@example.com";
        dto.backupEmail = "user@example.com";

        Set<ConstraintViolation<FieldLevelDifferentDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("backupEmail");
    }

    @Test
    void confirmedOnFieldShouldValidateAgainstReferenceField() {
        FieldLevelConfirmedDto dto = new FieldLevelConfirmedDto();
        dto.password = "secret";
        dto.passwordConfirmation = "other";

        Set<ConstraintViolation<FieldLevelConfirmedDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("passwordConfirmation");
        assertThat(violations.iterator().next().getMessage()).contains("confirmación");
    }

    @Test
    void confirmedOnFieldShouldIgnoreMissingConfirmation() {
        FieldLevelConfirmedDto dto = new FieldLevelConfirmedDto();
        dto.password = "secret";

        Set<ConstraintViolation<FieldLevelConfirmedDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void requiredIfAcceptedOnFieldShouldUseValueShorthand() {
        FieldLevelRequiredIfAcceptedDto dto = new FieldLevelRequiredIfAcceptedDto();
        dto.termsAccepted = "yes";

        Set<ConstraintViolation<FieldLevelRequiredIfAcceptedDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("fullName");
        assertThat(violations.iterator().next().getMessage()).contains("termsAccepted");
        assertThat(violations.iterator().next().getMessage()).doesNotContain("{other}");
    }

    @Test
    void prohibitedIfOnFieldShouldValidateAnnotatedProperty() {
        FieldLevelProhibitedIfDto dto = new FieldLevelProhibitedIfDto();
        dto.role = "ADMIN";
        dto.nickname = "admin-user";

        Set<ConstraintViolation<FieldLevelProhibitedIfDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nickname");
        assertThat(violations.iterator().next().getMessage()).contains("role");
        assertThat(violations.iterator().next().getMessage()).doesNotContain("{other}");
    }

    static class FieldLevelRequiredWithDto {
        private String newPassword;

        @RequiredWith("newPassword")
        private String oldPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
    }

    static class FieldLevelRequiredIfDto {
        private String role;

        @RequiredIf(field = "role", value = "ADMIN")
        private String adminCode;
    }

    static class FieldLevelSameDto {
        private String password;

        @Same("password")
        private String passwordConfirmation;
    }

    static class FieldLevelDifferentDto {
        private String email;

        @Different("email")
        private String backupEmail;
    }

    static class FieldLevelConfirmedDto {
        private String password;

        @Confirmed("password")
        private String passwordConfirmation;
    }

    static class FieldLevelRequiredIfAcceptedDto {
        private String termsAccepted;

        @RequiredIfAccepted("termsAccepted")
        private String fullName;
    }

    static class FieldLevelProhibitedIfDto {
        private String role;

        @ProhibitedIf(field = "role", value = "ADMIN")
        private String nickname;
    }
}
