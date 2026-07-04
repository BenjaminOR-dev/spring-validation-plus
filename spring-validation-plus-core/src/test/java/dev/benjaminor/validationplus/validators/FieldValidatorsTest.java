package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.EmailAddress;
import dev.benjaminor.validationplus.constraints.IntegerType;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Numeric;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FieldValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void requiredShouldFailForEmptyValues() {
        SampleDto dto = new SampleDto();
        dto.name = "   ";
        dto.email = "";
        dto.tags = List.of();
        dto.size = 1;

        Set<ConstraintViolation<SampleDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void integerTypeShouldAcceptIntegerValues() {
        SampleDto dto = validDto();
        dto.size = 10;

        assertThat(validator.validateProperty(dto, "size")).isEmpty();
    }

    @Test
    void integerTypeShouldRejectDecimalValues() {
        SampleDto dto = validDto();
        dto.decimalHolder = 3.14d;

        assertThat(validator.validateProperty(dto, "decimalHolder")).isNotEmpty();
    }

    @Test
    void numericShouldAcceptNumbers() {
        SampleDto dto = validDto();
        dto.amount = new BigDecimal("10.5");

        assertThat(validator.validateProperty(dto, "amount")).isEmpty();
    }

    @Test
    void emailAddressShouldValidateFormat() {
        SampleDto dto = validDto();
        dto.email = "invalid-email";

        Set<ConstraintViolation<SampleDto>> violations = validator.validateProperty(dto, "email");

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("correo");
    }

    @Test
    void minAndMaxLengthShouldValidateStringLength() {
        SampleDto dto = validDto();
        dto.name = "A";

        assertThat(validator.validateProperty(dto, "name")).isNotEmpty();

        dto.name = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertThat(validator.validateProperty(dto, "name")).isNotEmpty();
    }

    @Test
    void requiredIfShouldRequireFieldWhenConditionMatches() {
        ConditionalDto dto = new ConditionalDto();
        dto.role = "ADMIN";

        Set<ConstraintViolation<ConditionalDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("adminCode");
    }

    @Test
    void messagesShouldBeLocalizedInSpanish() {
        SampleDto dto = validDto();
        dto.email = null;

        Set<ConstraintViolation<SampleDto>> violations = validator.validateProperty(dto, "email");

        assertThat(violations.iterator().next().getMessage()).isEqualTo("El campo email es obligatorio.");
    }

    private SampleDto validDto() {
        SampleDto dto = new SampleDto();
        dto.name = "Benjamin";
        dto.email = "user@example.com";
        dto.size = 1;
        dto.tags = List.of("a");
        return dto;
    }

    static class SampleDto {

        @Required
        @MinLength(2)
        @MaxLength(50)
        String name;

        @Required
        @EmailAddress
        String email;

        @Required
        @IntegerType
        Integer size;

        @Numeric
        BigDecimal amount;

        @IntegerType
        Double decimalHolder;

        @Required
        List<String> tags;
    }

    @RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
    static class ConditionalDto {

        private String role;

        private String adminCode;
    }
}
