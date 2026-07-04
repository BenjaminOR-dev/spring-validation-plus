package dev.benjaminor.validationplus.exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FieldErrorMessageResolverTest {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es");

    @Test
    void shouldResolveIntegerTypeMismatchFromCodes() {
        FieldError error = new FieldError("user", "size", "abc", false, new String[]{
                "typeMismatch.user.size",
                "typeMismatch.size",
                "typeMismatch.java.lang.Integer",
                "typeMismatch"
        }, null, "Failed to convert property value of type 'java.lang.String' to required type 'java.lang.Integer'");

        String message = FieldErrorMessageResolver.resolve(error, LOCALE_ES);

        assertThat(message).isEqualTo("El campo size debe ser un entero.");
    }

    @Test
    void shouldResolveBooleanTypeMismatchFromCodes() {
        FieldError error = new FieldError("user", "active", "maybe", false, new String[]{
                "typeMismatch.user.active",
                "typeMismatch.java.lang.Boolean"
        }, null, "Failed to convert property value");

        String message = FieldErrorMessageResolver.resolve(error, LOCALE_ES);

        assertThat(message).isEqualTo("El campo active debe ser verdadero o falso.");
    }

    @Test
    void shouldResolveDecimalTypeMismatchFromCodes() {
        FieldError error = new FieldError("user", "score", "abc", false, new String[]{
                "typeMismatch.java.lang.Double"
        }, null, "Failed to convert property value");

        String message = FieldErrorMessageResolver.resolve(error, LOCALE_ES);

        assertThat(message).isEqualTo("El campo score debe ser un número decimal.");
    }

    @Test
    void shouldResolveBigIntegerAsIntegerTypeMismatch() {
        FieldError error = new FieldError("user", "amount", "abc", false, new String[]{
                "typeMismatch.java.math.BigInteger"
        }, null, "Failed to convert property value");

        String message = FieldErrorMessageResolver.resolve(error, LOCALE_ES);

        assertThat(message).isEqualTo("El campo amount debe ser un entero.");
    }

    @Test
    void shouldReturnDefaultMessageForNonTypeMismatchErrors() {
        FieldError error = new FieldError("user", "name", null, false, new String[]{"NotNull.user.name"}, null,
                "El campo name es obligatorio.");

        String message = FieldErrorMessageResolver.resolve(error, LOCALE_ES);

        assertThat(message).isEqualTo("El campo name es obligatorio.");
    }
}
