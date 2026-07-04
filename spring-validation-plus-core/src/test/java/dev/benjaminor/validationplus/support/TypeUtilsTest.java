package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TypeUtilsTest {

    @Test
    void shouldDetectStringTypes() {
        assertThat(TypeUtils.isStringType("hello")).isTrue();
        assertThat(TypeUtils.isStringType(null)).isTrue();
        assertThat(TypeUtils.isStringType(123)).isFalse();
    }

    @Test
    void shouldDetectBooleanTypes() {
        assertThat(TypeUtils.isBooleanType(true)).isTrue();
        assertThat(TypeUtils.isBooleanType(null)).isTrue();
        assertThat(TypeUtils.isBooleanType("true")).isFalse();
        assertThat(TypeUtils.isBooleanType(1)).isFalse();
    }

    @Test
    void shouldDetectArrayTypes() {
        assertThat(TypeUtils.isArrayType(new String[]{"a"})).isTrue();
        assertThat(TypeUtils.isArrayType(java.util.List.of("a"))).isTrue();
        assertThat(TypeUtils.isArrayType(null)).isTrue();
        assertThat(TypeUtils.isArrayType("array")).isFalse();
    }
}
