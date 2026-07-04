package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.*;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ExtendedValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void textAndNetworkValidatorsShouldValidateValues() {
        ExtendedDto dto = new ExtendedDto();
        dto.alpha = "abc";
        dto.ipv4 = "192.168.0.1";
        dto.hexColor = "#ff00aa";
        dto.ulid = "01ARZ3NDEKTSV4RRFFQ69G5FAV";

        assertThat(validator.validateProperty(dto, "alpha")).isEmpty();
        assertThat(validator.validateProperty(dto, "ipv4")).isEmpty();
        assertThat(validator.validateProperty(dto, "hexColor")).isEmpty();
        assertThat(validator.validateProperty(dto, "ulid")).isEmpty();

        dto.alpha = "abc123";
        dto.ipv4 = "999.999.999.999";
        assertThat(validator.validateProperty(dto, "alpha")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "ipv4")).isNotEmpty();
    }

    @Test
    void numericComparisonsShouldValidateValues() {
        ExtendedDto dto = new ExtendedDto();
        dto.gtField = 10;
        dto.lteField = 5;
        dto.multipleField = 6;

        assertThat(validator.validateProperty(dto, "gtField")).isEmpty();
        assertThat(validator.validateProperty(dto, "lteField")).isEmpty();
        assertThat(validator.validateProperty(dto, "multipleField")).isEmpty();

        dto.gtField = 3;
        dto.multipleField = 7;
        assertThat(validator.validateProperty(dto, "gtField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "multipleField")).isNotEmpty();
    }

    @Test
    void enumPasswordAndDistinctShouldValidateValues() {
        ExtendedDto dto = new ExtendedDto();
        dto.role = "ADMIN";
        dto.password = "Secret1!";
        dto.tags = List.of("a", "b");

        assertThat(validator.validateProperty(dto, "role")).isEmpty();
        assertThat(validator.validateProperty(dto, "password")).isEmpty();
        assertThat(validator.validateProperty(dto, "tags")).isEmpty();

        dto.role = "INVALID";
        dto.password = "short";
        dto.tags = List.of("a", "a");
        assertThat(validator.validateProperty(dto, "role")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "password")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "tags")).isNotEmpty();
    }

    @Test
    void classLevelExtendedValidatorsShouldValidateRules() {
        RequiredWithAllDto withAll = new RequiredWithAllDto();
        withAll.email = "a@b.com";
        withAll.phone = "123";
        assertThat(validator.validate(withAll)).isNotEmpty();

        withAll.name = "Benjamin";
        assertThat(validator.validate(withAll)).isEmpty();

        ProhibitedDto prohibited = new ProhibitedDto();
        prohibited.nickname = "nick";
        assertThat(validator.validate(prohibited)).isNotEmpty();
    }

    @Test
    void beforeOrEqualAndAfterOrEqualShouldValidateInclusiveDates() {
        InclusiveDateDto dto = new InclusiveDateDto();
        dto.eventDate = java.time.LocalDate.of(2024, 6, 15);
        dto.beforeLimit = java.time.LocalDate.of(2024, 6, 15);
        dto.afterLimit = java.time.LocalDate.of(2024, 6, 15);

        assertThat(validator.validate(dto)).isEmpty();

        dto.beforeLimit = java.time.LocalDate.of(2024, 6, 16);
        assertThat(validator.validateProperty(dto, "beforeLimit")).isNotEmpty();

        dto.beforeLimit = java.time.LocalDate.of(2024, 6, 15);
        dto.afterLimit = java.time.LocalDate.of(2024, 6, 14);
        assertThat(validator.validateProperty(dto, "afterLimit")).isNotEmpty();
    }

    @Test
    void inArrayShouldValidateValueAgainstCompanionArray() {
        InArrayDto dto = new InArrayDto();
        dto.allowed = List.of("admin", "editor");
        dto.role = "admin";

        assertThat(validator.validate(dto)).isEmpty();

        dto.role = "guest";
        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void missingIfShouldRequireFieldToBeAbsentWhenConditionMatches() {
        MissingIfDto dto = new MissingIfDto();
        dto.type = "guest";
        dto.email = "user@example.com";

        assertThat(validator.validate(dto)).isNotEmpty();

        dto.email = null;
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void minMaxAndDigitsBetweenShouldValidateDigitCounts() {
        DigitsDto dto = new DigitsDto();
        dto.minDigitsField = new java.math.BigDecimal("12345");
        dto.maxDigitsField = new java.math.BigDecimal("999");
        dto.digitsBetweenField = new java.math.BigDecimal("1234");

        assertThat(validator.validate(dto)).isEmpty();

        dto.minDigitsField = new java.math.BigDecimal("123");
        dto.maxDigitsField = new java.math.BigDecimal("10000");
        dto.digitsBetweenField = new java.math.BigDecimal("12");
        assertThat(validator.validateProperty(dto, "minDigitsField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "maxDigitsField")).isNotEmpty();
        assertThat(validator.validateProperty(dto, "digitsBetweenField")).isNotEmpty();
    }

    @Test
    void minMaxAndDigitsBetweenShouldHandleScientificNotation() {
        DigitsDto dto = new DigitsDto();
        dto.minDigitsField = new java.math.BigDecimal("1E+3");
        dto.maxDigitsField = new java.math.BigDecimal("1E+3");
        dto.digitsBetweenField = new java.math.BigDecimal("1E+3");

        assertThat(validator.validate(dto)).isEmpty();
    }

    enum Role {
        ADMIN, USER
    }

    static class ExtendedDto {
        @Alpha
        private String alpha;

        @Ipv4
        private String ipv4;

        @HexColor
        private String hexColor;

        @Ulid
        private String ulid;

        @Gt(5)
        private Integer gtField;

        @Lte(5)
        private Integer lteField;

        @MultipleOf(3)
        private Integer multipleField;

        @EnumValue(enumClass = Role.class)
        private String role;

        @Password(min = 8, letters = true, mixedCase = true, numbers = true, symbols = true)
        private String password;

        @Distinct
        private List<String> tags;
    }

    @RequiredWithAll(fields = {"email", "phone"}, required = "name")
    static class RequiredWithAllDto {
        private String email;
        private String phone;
        private String name;
    }

    @Prohibited(field = "nickname")
    static class ProhibitedDto {
        private String nickname;
    }

    static class InclusiveDateDto {
        private java.time.LocalDate eventDate = java.time.LocalDate.of(2024, 6, 15);

        @BeforeOrEqual(value = "2024-06-15")
        private java.time.LocalDate beforeLimit;

        @AfterOrEqual(value = "2024-06-15")
        private java.time.LocalDate afterLimit;
    }

    @InArray(field = "role", arrayField = "allowed")
    static class InArrayDto {
        private List<String> allowed;
        private String role;
    }

    @MissingIf(field = "type", value = "guest", missing = "email")
    static class MissingIfDto {
        private String type;
        private String email;
    }

    static class DigitsDto {
        @MinDigits(4)
        private java.math.BigDecimal minDigitsField;

        @MaxDigits(4)
        private java.math.BigDecimal maxDigitsField;

        @DigitsBetween(min = 3, max = 5)
        private java.math.BigDecimal digitsBetweenField;
    }
}
