package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Accepted;
import dev.benjaminor.validationplus.constraints.After;
import dev.benjaminor.validationplus.constraints.Before;
import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.Date;
import dev.benjaminor.validationplus.constraints.Declined;
import dev.benjaminor.validationplus.constraints.Digits;
import dev.benjaminor.validationplus.constraints.EndsWith;
import dev.benjaminor.validationplus.constraints.In;
import dev.benjaminor.validationplus.constraints.Json;
import dev.benjaminor.validationplus.constraints.MinDigits;
import dev.benjaminor.validationplus.constraints.NotIn;
import dev.benjaminor.validationplus.constraints.NotRegex;
import dev.benjaminor.validationplus.constraints.Regex;
import dev.benjaminor.validationplus.constraints.Size;
import dev.benjaminor.validationplus.constraints.StartsWith;
import dev.benjaminor.validationplus.constraints.Url;
import dev.benjaminor.validationplus.constraints.Uuid;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void acceptedShouldValidateTruthyValues() {
        SimpleDto dto = new SimpleDto();
        dto.acceptedField = "yes";
        dto.declinedField = "no";

        assertThat(validator.validateProperty(dto, "acceptedField")).isEmpty();
        assertThat(validator.validateProperty(dto, "declinedField")).isEmpty();
    }

    @Test
    void acceptedAndDeclinedShouldRejectInvalidValues() {
        SimpleDto dto = new SimpleDto();
        dto.acceptedField = "no";
        dto.declinedField = "yes";

        assertThat(validator.validateProperty(dto, "acceptedField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "declinedField")).isNotEmpty();
    }

    @Test
    void acceptedAndDeclinedShouldRejectNullValues() {
        SimpleDto dto = new SimpleDto();

        assertThat(validator.validateProperty(dto, "acceptedField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "declinedField")).isNotEmpty();
    }

    @Test
    void inAndNotInShouldValidateAllowedValues() {
        SimpleDto dto = new SimpleDto();
        dto.inField = "admin";
        dto.notInField = "guest";

        assertThat(validator.validateProperty(dto, "inField")).isEmpty();
        assertThat(validator.validateProperty(dto, "notInField")).isEmpty();

        dto.inField = "guest";
        dto.notInField = "admin";

        assertThat(validator.validateProperty(dto, "inField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "notInField")).isNotEmpty();
    }

    @Test
    void regexAndNotRegexShouldValidatePatterns() {
        SimpleDto dto = new SimpleDto();
        dto.regexField = "ABC123";
        dto.notRegexField = "valid";

        assertThat(validator.validateProperty(dto, "regexField")).isEmpty();
        assertThat(validator.validateProperty(dto, "notRegexField")).isEmpty();

        dto.regexField = "abc";
        dto.notRegexField = "123";

        assertThat(validator.validateProperty(dto, "regexField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "notRegexField")).isNotEmpty();
    }

    @Test
    void urlAndUuidShouldValidateFormats() {
        SimpleDto dto = new SimpleDto();
        dto.urlField = "https://example.com";
        dto.uuidField = "550e8400-e29b-41d4-a716-446655440000";

        assertThat(validator.validateProperty(dto, "urlField")).isEmpty();
        assertThat(validator.validateProperty(dto, "uuidField")).isEmpty();

        dto.urlField = "not-a-url";
        dto.uuidField = "invalid-uuid";

        assertThat(validator.validateProperty(dto, "urlField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "uuidField")).isNotEmpty();
    }

    @Test
    void dateBeforeAndAfterShouldValidateDates() {
        SimpleDto dto = new SimpleDto();
        dto.acceptedField = "yes";
        dto.declinedField = "no";
        dto.dateField = LocalDate.of(2024, 6, 1);
        dto.beforeField = LocalDate.of(2024, 1, 1);
        dto.afterField = LocalDate.of(2025, 1, 1);
        dto.referenceDate = LocalDate.of(2024, 6, 15);

        assertThat(validator.validate(dto)).isEmpty();

        dto.beforeField = LocalDate.of(2025, 1, 1);
        assertThat(validator.validateProperty(dto, "beforeField")).isNotEmpty();
    }

    @Test
    void betweenSizeAndDigitsShouldValidateRanges() {
        SimpleDto dto = new SimpleDto();
        dto.betweenField = 5;
        dto.sizeField = "12345";
        dto.digitsField = new BigDecimal("123.45");

        assertThat(validator.validateProperty(dto, "betweenField")).isEmpty();
        assertThat(validator.validateProperty(dto, "sizeField")).isEmpty();
        assertThat(validator.validateProperty(dto, "digitsField")).isEmpty();

        dto.betweenField = 100;
        dto.sizeField = "123";
        dto.digitsField = new BigDecimal("1234.567");

        assertThat(validator.validateProperty(dto, "betweenField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "sizeField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "digitsField")).isNotEmpty();
    }

    @Test
    void digitsAndMinDigitsShouldAgreeOnTrailingZeros() {
        SimpleDto dto = new SimpleDto();
        dto.digitsField = new BigDecimal("10.00");

        assertThat(validator.validateProperty(dto, "digitsField")).isEmpty();

        DigitsAlignmentDto alignmentDto = new DigitsAlignmentDto();
        alignmentDto.value = new BigDecimal("10.00");

        assertThat(validator.validate(alignmentDto)).isEmpty();
    }

    static class DigitsAlignmentDto {
        @MinDigits(2)
        private java.math.BigDecimal value;
    }

    @Test
    void startsWithEndsWithAndJsonShouldValidate() {
        SimpleDto dto = new SimpleDto();
        dto.startsWithField = "hello world";
        dto.endsWithField = "my-file.txt";
        dto.jsonField = "{\"name\":\"Benjamin\"}";

        assertThat(validator.validateProperty(dto, "startsWithField")).isEmpty();
        assertThat(validator.validateProperty(dto, "endsWithField")).isEmpty();
        assertThat(validator.validateProperty(dto, "jsonField")).isEmpty();

        dto.startsWithField = "world";
        dto.endsWithField = "my-file";
        dto.jsonField = "{invalid}";

        assertThat(validator.validateProperty(dto, "startsWithField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "endsWithField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "jsonField")).isNotEmpty();
    }

    @Test
    void messagesShouldBeLocalizedInSpanish() {
        SimpleDto dto = new SimpleDto();
        dto.urlField = "bad";

        Set<ConstraintViolation<SimpleDto>> violations = validator.validateProperty(dto, "urlField");

        assertThat(violations.iterator().next().getMessage()).contains("URL");
    }

    static class SimpleDto {

        @Accepted
        Object acceptedField;

        @Declined
        Object declinedField;

        @In({"admin", "editor"})
        String inField;

        @NotIn({"admin", "banned"})
        String notInField;

        @Regex(pattern = "^[A-Z0-9]+$")
        String regexField;

        @NotRegex(pattern = "^\\d+$")
        String notRegexField;

        @Url
        String urlField;

        @Uuid
        String uuidField;

        @Date
        Object dateField;

        @Before(value = "2024-12-31")
        LocalDate beforeField;

        @After(value = "2024-01-01")
        LocalDate afterField;

        LocalDate referenceDate;

        @Between(min = 1, max = 10)
        Integer betweenField;

        @Size(5)
        String sizeField;

        @Digits(integer = 3, fraction = 2)
        BigDecimal digitsField;

        @StartsWith("hello")
        String startsWithField;

        @EndsWith(".txt")
        String endsWithField;

        @Json
        String jsonField;
    }
}
