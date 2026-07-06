package dev.benjaminor.validationplus.exception;

import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.StringType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationErrorOrderingTest {

    @Test
    void shouldOrderFieldsByDeclarationOrder() {
        List<ValidationErrorOrdering.ValidationErrorEntry> entries = List.of(
                new ValidationErrorOrdering.ValidationErrorEntry("email", "email error", "EmailAddress"),
                new ValidationErrorOrdering.ValidationErrorEntry("name", "name error", "Required"),
                new ValidationErrorOrdering.ValidationErrorEntry("password", "password error", "MinLength"));

        Map<String, List<String>> ordered = ValidationErrorOrdering.order(SampleRequest.class, entries);

        assertThat(ordered.keySet()).containsExactly("name", "email", "password");
    }

    @Test
    void shouldOrderConstraintsByAnnotationDeclarationOrderOnSameField() {
        List<ValidationErrorOrdering.ValidationErrorEntry> entries = List.of(
                new ValidationErrorOrdering.ValidationErrorEntry(
                        "passwordConfirmation",
                        "La confirmación del campo password no coincide.",
                        "Confirmed"),
                new ValidationErrorOrdering.ValidationErrorEntry(
                        "passwordConfirmation",
                        "El campo passwordConfirmation es obligatorio cuando password está presente.",
                        "RequiredWith"),
                new ValidationErrorOrdering.ValidationErrorEntry(
                        "passwordConfirmation",
                        "El campo passwordConfirmation debe tener al menos 5 caracteres.",
                        "MinLength"));

        Map<String, List<String>> ordered = ValidationErrorOrdering.order(SampleRequest.class, entries);

        assertThat(ordered.get("passwordConfirmation")).containsExactly(
                "El campo passwordConfirmation es obligatorio cuando password está presente.",
                "La confirmación del campo password no coincide.",
                "El campo passwordConfirmation debe tener al menos 5 caracteres.");
    }

    @Test
    void shouldPreserveInsertionOrderWhenTargetTypeIsUnknown() {
        List<ValidationErrorOrdering.ValidationErrorEntry> entries = List.of(
                new ValidationErrorOrdering.ValidationErrorEntry("b", "second", "B"),
                new ValidationErrorOrdering.ValidationErrorEntry("a", "first", "A"));

        Map<String, List<String>> ordered = ValidationErrorOrdering.order(null, entries);

        assertThat(ordered.keySet()).containsExactly("b", "a");
    }

    static class SampleRequest {
        private String name;
        private String email;
        private String password;

        @RequiredWith("password")
        @Confirmed("password")
        @StringType
        @MinLength(5)
        private String passwordConfirmation;
    }
}
