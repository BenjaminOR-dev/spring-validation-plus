package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessageUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldFormatWholeNumbersWithoutDecimalSuffix() {
        assertThat(ValidationMessageUtils.formatParameterValue(1.0)).isEqualTo("1");
        assertThat(ValidationMessageUtils.formatParameterValue(50.0)).isEqualTo("50");
        assertThat(ValidationMessageUtils.formatParameterValue(1.5)).isEqualTo("1.5");
        assertThat(ValidationMessageUtils.formatParameterValue(new BigDecimal("1234.00"))).isEqualTo("1234");
        assertThat(ValidationMessageUtils.formatParameterValue(new String[]{"ADMIN", "MODERATOR"}))
                .isEqualTo("ADMIN, MODERATOR");
        assertThat(ValidationMessageUtils.formatCommaSeparatedList("ADMIN,MODERATOR"))
                .isEqualTo("ADMIN, MODERATOR");
        assertThat(ValidationMessageUtils.formatCommaSeparatedList("ADMIN, MODERATOR"))
                .isEqualTo("ADMIN, MODERATOR");
    }

    @Test
    void shouldInterpolateBetweenMessageWithIntegerLimits() {
        String message = ValidationMessageUtils.interpolate(
                "The {field} field must be between {min} and {max}.",
                Map.of("field", "items", "min", 1.0, "max", 50.0));

        assertThat(message).isEqualTo("The items field must be between 1 and 50.");
    }

    @Test
    void partialAppOverrideDoesNotHideLibraryDefaults() throws IOException {
        Files.writeString(
                tempDir.resolve("ValidationMessages_es.properties"),
                """
                dev.benjaminor.validationplus.constraints.Unique.message=OVERRIDE unique {field}.
                """);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader overrideLoader = new URLClassLoader(
                new URL[]{tempDir.toUri().toURL()},
                ValidationMessageUtils.class.getClassLoader())) {
            Thread.currentThread().setContextClassLoader(overrideLoader);

            assertThat(ValidationMessageUtils.resolve(
                            "dev.benjaminor.validationplus.constraints.Required.message",
                            Locale.forLanguageTag("es"),
                            Map.of("field", "email")))
                    .isEqualTo("El campo email es obligatorio.");

            assertThat(ValidationMessageUtils.resolve(
                            "dev.benjaminor.validationplus.constraints.Unique.message",
                            Locale.forLanguageTag("es"),
                            Map.of("field", "email")))
                    .isEqualTo("OVERRIDE unique email.");
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    @Test
    void resolveTemplateFallsBackWhenKeyMissingFromPartialOverride() throws IOException {
        Files.writeString(
                tempDir.resolve("ValidationMessages_es.properties"),
                """
                # intentional partial override (Exists only)
                dev.benjaminor.validationplus.constraints.Exists.message=OVERRIDE exists {field}.
                """);
        Files.writeString(
                tempDir.resolve("ValidationMessages.properties"),
                """
                dev.benjaminor.validationplus.constraints.Exists.message=OVERRIDE exists en {field}.
                """);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader overrideLoader = new URLClassLoader(
                new URL[]{tempDir.toUri().toURL()},
                ValidationMessageUtils.class.getClassLoader())) {
            Thread.currentThread().setContextClassLoader(overrideLoader);

            String required = ValidationMessageUtils.resolveTemplate(
                    "{dev.benjaminor.validationplus.constraints.Required.message}",
                    Locale.forLanguageTag("es"),
                    Map.of("field", "session"));

            assertThat(required).isEqualTo("El campo session es obligatorio.");
            assertThat(required).doesNotContain("{dev.benjaminor.validationplus");
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }
}
