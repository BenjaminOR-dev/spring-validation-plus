package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessagesI18nTest {

    @Test
    void shouldResolveEnglishMessagesFromDefaultBundle() {
        String message = ValidationMessageUtils.resolve(
                "dev.benjaminor.validationplus.constraints.Required.message",
                Locale.forLanguageTag("en"),
                Map.of("field", "email"));

        assertThat(message).isEqualTo("The email field is required.");
    }

    @Test
    void shouldResolvePortugueseMessages() {
        String message = ValidationMessageUtils.resolve(
                "dev.benjaminor.validationplus.constraints.Required.message",
                Locale.forLanguageTag("pt"),
                Map.of("field", "email"));

        assertThat(message).isEqualTo("O campo email é obrigatório.");
    }

    @Test
    void shouldResolvePortugueseBrazilLocaleViaFallback() {
        String message = ValidationMessageUtils.resolve(
                "dev.benjaminor.validationplus.constraints.Required.message",
                Locale.forLanguageTag("pt-BR"),
                Map.of("field", "nome"));

        assertThat(message).isEqualTo("O campo nome é obrigatório.");
    }

    @Test
    void shouldResolveSpanishSizeAndInvalidValueMessages() {
        assertThat(ValidationMessageUtils.resolve(
                        "dev.benjaminor.validationplus.constraints.Size.message",
                        Locale.forLanguageTag("es"),
                        Map.of("field", "code", "value", 4)))
                .isEqualTo("El campo code debe tener tamaño 4.");

        assertThat(ValidationMessageUtils.resolve(
                        "dev.benjaminor.validationplus.constraints.In.message",
                        Locale.forLanguageTag("es"),
                        Map.of("field", "role")))
                .isEqualTo("El valor del campo role no es válido.");

        assertThat(ValidationMessageUtils.resolve(
                        "dev.benjaminor.validationplus.constraints.RequiredWith.message",
                        Locale.forLanguageTag("es"),
                        Map.of("field", "email", "other", "name, phone")))
                .isEqualTo(
                        "El campo email es obligatorio cuando está presente alguno de estos campos: name, phone.");
    }

    @Test
    void shouldResolveExistsAndUniqueSpanishDefaults() {
        assertThat(ValidationMessageUtils.resolve(
                        "dev.benjaminor.validationplus.constraints.Exists.message",
                        Locale.forLanguageTag("es"),
                        Map.of("field", "idRegistro")))
                .isEqualTo("No existe un valor registrado para el campo idRegistro.");

        assertThat(ValidationMessageUtils.resolve(
                        "dev.benjaminor.validationplus.constraints.Unique.message",
                        Locale.forLanguageTag("es"),
                        Map.of("field", "email")))
                .isEqualTo("El valor del campo email ya está en uso.");
    }
}
