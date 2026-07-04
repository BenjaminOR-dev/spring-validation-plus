package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessageUtilsTest {

    @Test
    void shouldFormatWholeNumbersWithoutDecimalSuffix() {
        assertThat(ValidationMessageUtils.formatParameterValue(1.0)).isEqualTo("1");
        assertThat(ValidationMessageUtils.formatParameterValue(50.0)).isEqualTo("50");
        assertThat(ValidationMessageUtils.formatParameterValue(1.5)).isEqualTo("1.5");
        assertThat(ValidationMessageUtils.formatParameterValue(new BigDecimal("1234.00"))).isEqualTo("1234");
    }

    @Test
    void shouldInterpolateBetweenMessageWithIntegerLimits() {
        String message = ValidationMessageUtils.interpolate(
                "The {field} field must be between {min} and {max}.",
                Map.of("field", "items", "min", 1.0, "max", 50.0));

        assertThat(message).isEqualTo("The items field must be between 1 and 50.");
    }
}
