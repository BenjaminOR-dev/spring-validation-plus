package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessagesI18nTest {

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
}
