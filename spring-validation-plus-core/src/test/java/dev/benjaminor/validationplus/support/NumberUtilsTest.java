package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class NumberUtilsTest {

    @Test
    void shouldDetectIntegerTypes() {
        assertThat(NumberUtils.isIntegerType(10)).isTrue();
        assertThat(NumberUtils.isIntegerType(10L)).isTrue();
        assertThat(NumberUtils.isIntegerType(3.14d)).isFalse();
        assertThat(NumberUtils.isIntegerType("10")).isFalse();
    }

    @Test
    void shouldDetectDecimalTypes() {
        assertThat(NumberUtils.isDecimalType(3.14f)).isTrue();
        assertThat(NumberUtils.isDecimalType(3.14d)).isTrue();
        assertThat(NumberUtils.isDecimalType(new BigDecimal("10.5"))).isTrue();
        assertThat(NumberUtils.isDecimalType(10)).isFalse();
        assertThat(NumberUtils.isDecimalType(null)).isTrue();
    }

    @Test
    void shouldCompareMinAndMaxValues() {
        assertThat(NumberUtils.isMinValue(5, 1)).isTrue();
        assertThat(NumberUtils.isMinValue(0, 1)).isFalse();
        assertThat(NumberUtils.isMinValue(null, 1)).isTrue();

        assertThat(NumberUtils.isMaxValue(100, 100)).isTrue();
        assertThat(NumberUtils.isMaxValue(101, 100)).isFalse();
        assertThat(NumberUtils.isMaxValue(null, 100)).isTrue();
    }
}
