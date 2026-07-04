package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.constraints.Different;
import dev.benjaminor.validationplus.constraints.ProhibitedIf;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.constraints.RequiredIfAccepted;
import dev.benjaminor.validationplus.constraints.RequiredIfDeclined;
import dev.benjaminor.validationplus.constraints.RequiredUnless;
import dev.benjaminor.validationplus.constraints.Missing;
import dev.benjaminor.validationplus.constraints.MissingUnless;
import dev.benjaminor.validationplus.constraints.MissingWith;
import dev.benjaminor.validationplus.constraints.MissingWithAll;
import dev.benjaminor.validationplus.constraints.Prohibited;
import dev.benjaminor.validationplus.constraints.ProhibitedUnless;
import dev.benjaminor.validationplus.constraints.RequiredWithoutAll;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.RequiredWithout;
import dev.benjaminor.validationplus.constraints.Same;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClassLevelValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void requiredUnlessShouldSkipWhenUnlessConditionMatches() {
        RequiredUnlessDto dto = new RequiredUnlessDto();
        dto.role = "guest";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void requiredUnlessShouldRequireFieldWhenConditionDoesNotMatch() {
        RequiredUnlessDto dto = new RequiredUnlessDto();
        dto.role = "admin";

        Set<ConstraintViolation<RequiredUnlessDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void requiredWithShouldRequireFieldWhenCompanionIsPresent() {
        RequiredWithDto dto = new RequiredWithDto();
        dto.password = "secret";

        Set<ConstraintViolation<RequiredWithDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("passwordConfirmation");
    }

    @Test
    void requiredWithoutShouldRequireFieldWhenCompanionIsAbsent() {
        RequiredWithoutDto dto = new RequiredWithoutDto();

        Set<ConstraintViolation<RequiredWithoutDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void sameShouldFailWhenValuesDoNotMatch() {
        SameDto dto = new SameDto();
        dto.password = "secret";
        dto.passwordConfirmation = "other";

        Set<ConstraintViolation<SameDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("passwordConfirmation");
        assertThat(violations.iterator().next().getMessage()).contains("passwordConfirmation");
    }

    @Test
    void differentShouldFailWhenValuesMatch() {
        DifferentDto dto = new DifferentDto();
        dto.email = "user@example.com";
        dto.backupEmail = "user@example.com";

        Set<ConstraintViolation<DifferentDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("backupEmail");
    }

    @Test
    void confirmedShouldFailWhenConfirmationDoesNotMatch() {
        ConfirmedDto dto = new ConfirmedDto();
        dto.password = "secret";
        dto.passwordConfirmation = "other";

        Set<ConstraintViolation<ConfirmedDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("passwordConfirmation");
        assertThat(violations.iterator().next().getMessage()).contains("confirmación");
    }

    @Test
    void requiredIfShouldStillWorkAlongsideNewConstraints() {
        RequiredIfDto dto = new RequiredIfDto();
        dto.role = "ADMIN";

        Set<ConstraintViolation<RequiredIfDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("adminCode");
    }

    @Test
    void requiredIfAcceptedShouldSkipWhenTriggerFieldIsNull() {
        RequiredIfAcceptedDto dto = new RequiredIfAcceptedDto();

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void requiredIfAcceptedShouldRequireFieldWhenTriggerIsAccepted() {
        RequiredIfAcceptedDto dto = new RequiredIfAcceptedDto();
        dto.termsAccepted = "yes";

        Set<ConstraintViolation<RequiredIfAcceptedDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("fullName");
    }

    @Test
    void requiredIfAcceptedShouldPassWhenRequiredFieldIsPresent() {
        RequiredIfAcceptedDto dto = new RequiredIfAcceptedDto();
        dto.termsAccepted = "yes";
        dto.fullName = "Benjamin";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void requiredIfDeclinedShouldSkipWhenTriggerFieldIsNull() {
        RequiredIfDeclinedDto dto = new RequiredIfDeclinedDto();

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void requiredIfDeclinedShouldRequireFieldWhenTriggerIsDeclined() {
        RequiredIfDeclinedDto dto = new RequiredIfDeclinedDto();
        dto.newsletter = "no";

        Set<ConstraintViolation<RequiredIfDeclinedDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("reason");
    }

    @Test
    void requiredIfDeclinedShouldPassWhenRequiredFieldIsPresent() {
        RequiredIfDeclinedDto dto = new RequiredIfDeclinedDto();
        dto.newsletter = "no";
        dto.reason = "Not interested";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void prohibitedIfShouldFailWhenProhibitedFieldIsPresent() {
        ProhibitedIfDto dto = new ProhibitedIfDto();
        dto.role = "ADMIN";
        dto.nickname = "admin-nick";

        Set<ConstraintViolation<ProhibitedIfDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nickname");
    }

    @Test
    void prohibitedIfShouldPassWhenConditionDoesNotMatch() {
        ProhibitedIfDto dto = new ProhibitedIfDto();
        dto.role = "USER";
        dto.nickname = "nick";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void missingShouldFailWhenFieldIsPresent() {
        MissingDto dto = new MissingDto();
        dto.phone = "555-0100";

        Set<ConstraintViolation<MissingDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("phone");
    }

    @Test
    void missingShouldPassWhenFieldIsAbsent() {
        MissingDto dto = new MissingDto();

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void missingShouldFailWhenFieldIsBlankButProvided() {
        MissingDto dto = new MissingDto();
        dto.phone = "   ";

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void prohibitedShouldFailWhenFieldIsBlankButProvided() {
        ProhibitedDto dto = new ProhibitedDto();
        dto.nickname = "   ";

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void missingUnlessShouldRequireFieldAbsentWhenConditionDoesNotMatch() {
        MissingUnlessDto dto = new MissingUnlessDto();
        dto.role = "admin";
        dto.nickname = "nick";

        assertThat(validator.validate(dto)).isNotEmpty();

        dto.nickname = null;
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void missingUnlessShouldAllowFieldWhenUnlessConditionMatches() {
        MissingUnlessDto dto = new MissingUnlessDto();
        dto.role = "guest";
        dto.nickname = "nick";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void prohibitedUnlessShouldFailWhenFieldPresentAndConditionDoesNotMatch() {
        ProhibitedUnlessDto dto = new ProhibitedUnlessDto();
        dto.role = "admin";
        dto.nickname = "nick";

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void prohibitedUnlessShouldAllowFieldWhenUnlessConditionMatches() {
        ProhibitedUnlessDto dto = new ProhibitedUnlessDto();
        dto.role = "guest";
        dto.nickname = "nick";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void missingWithShouldRequireFieldAbsentWhenCompanionIsPresent() {
        MissingWithDto dto = new MissingWithDto();
        dto.email = "user@example.com";
        dto.phone = "555-0100";

        assertThat(validator.validate(dto)).isNotEmpty();

        dto.phone = null;
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void missingWithAllShouldRequireFieldAbsentWhenAllCompanionsArePresent() {
        MissingWithAllDto dto = new MissingWithAllDto();
        dto.email = "user@example.com";
        dto.phone = "555-0100";
        dto.nickname = "nick";

        assertThat(validator.validate(dto)).isNotEmpty();

        dto.nickname = null;
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void requiredWithoutAllShouldRequireFieldWhenAllCompanionsAreAbsent() {
        RequiredWithoutAllDto dto = new RequiredWithoutAllDto();

        Set<ConstraintViolation<RequiredWithoutAllDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    void requiredWithoutAllShouldSkipWhenAnyCompanionIsPresent() {
        RequiredWithoutAllDto dto = new RequiredWithoutAllDto();
        dto.email = "user@example.com";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @RequiredUnless(field = "role", value = "guest", required = "email")
    static class RequiredUnlessDto {
        private String role;
        private String email;
    }

    @RequiredWith(fields = {"password"}, required = "passwordConfirmation")
    static class RequiredWithDto {
        private String password;
        private String passwordConfirmation;
    }

    @RequiredWithout(fields = {"phone"}, required = "email")
    static class RequiredWithoutDto {
        private String phone;
        private String email;
    }

    @Same(field = "password", other = "passwordConfirmation")
    static class SameDto {
        private String password;
        private String passwordConfirmation;
    }

    @Different(field = "email", other = "backupEmail")
    static class DifferentDto {
        private String email;
        private String backupEmail;
    }

    @Confirmed(field = "password")
    static class ConfirmedDto {
        private String password;
        private String passwordConfirmation;
    }

    @RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
    static class RequiredIfDto {
        private String role;
        private String adminCode;
    }

    @RequiredIfAccepted(field = "termsAccepted", required = "fullName")
    static class RequiredIfAcceptedDto {
        private String termsAccepted;
        private String fullName;
    }

    @RequiredIfDeclined(field = "newsletter", required = "reason")
    static class RequiredIfDeclinedDto {
        private String newsletter;
        private String reason;
    }

    @ProhibitedIf(field = "role", value = "ADMIN", prohibited = "nickname")
    static class ProhibitedIfDto {
        private String role;
        private String nickname;
    }

    @Missing(field = "phone")
    static class MissingDto {
        private String phone;
    }

    @Prohibited(field = "nickname")
    static class ProhibitedDto {
        private String nickname;
    }

    @MissingUnless(field = "role", value = "guest", missing = "nickname")
    static class MissingUnlessDto {
        private String role;
        private String nickname;
    }

    @ProhibitedUnless(field = "role", value = "guest", prohibited = "nickname")
    static class ProhibitedUnlessDto {
        private String role;
        private String nickname;
    }

    @MissingWith(fields = {"email"}, missing = "phone")
    static class MissingWithDto {
        private String email;
        private String phone;
    }

    @MissingWithAll(fields = {"email", "phone"}, missing = "nickname")
    static class MissingWithAllDto {
        private String email;
        private String phone;
        private String nickname;
    }

    @RequiredWithoutAll(fields = {"email", "phone"}, required = "name")
    static class RequiredWithoutAllDto {
        private String email;
        private String phone;
        private String name;
    }
}
