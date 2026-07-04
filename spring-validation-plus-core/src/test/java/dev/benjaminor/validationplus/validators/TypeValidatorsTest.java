package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.ArrayType;
import dev.benjaminor.validationplus.constraints.BooleanType;
import dev.benjaminor.validationplus.constraints.DecimalType;
import dev.benjaminor.validationplus.constraints.MaxValue;
import dev.benjaminor.validationplus.constraints.MinValue;
import dev.benjaminor.validationplus.constraints.Nullable;
import dev.benjaminor.validationplus.constraints.StringType;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TypeValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @Test
    void nullableShouldAlwaysPass() {
        TypeDto dto = new TypeDto();
        dto.nullableField = null;
        dto.stringField = 123;

        assertThat(validator.validateProperty(dto, "nullableField")).isEmpty();
        assertThat(validator.validateProperty(dto, "stringField")).isNotEmpty();
    }

    @Test
    void stringTypeShouldAcceptStringsAndRejectOtherTypes() {
        TypeDto dto = new TypeDto();
        dto.stringField = "hello";

        assertThat(validator.validateProperty(dto, "stringField")).isEmpty();

        dto.stringField = null;
        assertThat(validator.validateProperty(dto, "stringField")).isEmpty();

        dto.stringField = 123;
        assertThat(validator.validateProperty(dto, "stringField")).isNotEmpty();
    }

    @Test
    void decimalTypeShouldAcceptDecimalTypesOnly() {
        TypeDto dto = new TypeDto();
        dto.decimalField = 3.14d;

        assertThat(validator.validateProperty(dto, "decimalField")).isEmpty();

        dto.decimalField = new BigDecimal("10.5");
        assertThat(validator.validateProperty(dto, "decimalField")).isEmpty();

        dto.decimalField = 10;
        assertThat(validator.validateProperty(dto, "decimalField")).isNotEmpty();
    }

    @Test
    void booleanTypeShouldAcceptBooleanOnly() {
        TypeDto dto = new TypeDto();
        dto.booleanField = true;

        assertThat(validator.validateProperty(dto, "booleanField")).isEmpty();

        dto.booleanField = null;
        assertThat(validator.validateProperty(dto, "booleanField")).isEmpty();

        dto.booleanField = "true";
        assertThat(validator.validateProperty(dto, "booleanField")).isNotEmpty();
    }

    @Test
    void arrayTypeShouldAcceptArraysAndCollections() {
        TypeDto dto = new TypeDto();
        dto.arrayField = List.of("a", "b");

        assertThat(validator.validateProperty(dto, "arrayField")).isEmpty();

        dto.arrayField = new String[]{"a", "b"};
        assertThat(validator.validateProperty(dto, "arrayField")).isEmpty();

        dto.arrayField = null;
        assertThat(validator.validateProperty(dto, "arrayField")).isEmpty();

        dto.arrayField = "not-an-array";
        assertThat(validator.validateProperty(dto, "arrayField")).isNotEmpty();
    }

    @Test
    void minValueShouldValidateLowerBound() {
        TypeDto dto = new TypeDto();
        dto.numericField = 5;

        assertThat(validator.validateProperty(dto, "numericField")).isEmpty();

        dto.numericField = 0;
        Set<ConstraintViolation<TypeDto>> violations = validator.validateProperty(dto, "numericField");

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("El campo numericField debe ser al menos 1.");
    }

    @Test
    void maxValueShouldValidateUpperBound() {
        TypeDto dto = new TypeDto();
        dto.limitedField = 100;

        assertThat(validator.validateProperty(dto, "limitedField")).isEmpty();

        dto.limitedField = 101;
        Set<ConstraintViolation<TypeDto>> violations = validator.validateProperty(dto, "limitedField");

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("El campo limitedField no debe exceder 100.");
    }

    static class TypeDto {

        @Nullable
        Object nullableField;

        @StringType
        Object stringField;

        @DecimalType
        Object decimalField;

        @BooleanType
        Object booleanField;

        @ArrayType
        Object arrayField;

        @MinValue(1)
        Integer numericField;

        @MaxValue(100)
        Integer limitedField;
    }
}
