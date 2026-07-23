package dev.benjaminor.validationplus.exception;

import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.Same;
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
                        "El campo passwordConfirmation debe coincidir con password.",
                        "Same"),
                new ValidationErrorOrdering.ValidationErrorEntry(
                        "passwordConfirmation",
                        "El campo passwordConfirmation es obligatorio cuando está presente alguno de estos campos: password.",
                        "RequiredWith"),
                new ValidationErrorOrdering.ValidationErrorEntry(
                        "passwordConfirmation",
                        "El campo passwordConfirmation debe tener al menos 5 caracteres.",
                        "MinLength"));

        Map<String, List<String>> ordered = ValidationErrorOrdering.order(SampleRequest.class, entries);

        assertThat(ordered.get("passwordConfirmation")).containsExactly(
                "El campo passwordConfirmation es obligatorio cuando está presente alguno de estos campos: password.",
                "El campo passwordConfirmation debe coincidir con password.",
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
        @Same("password")
        @StringType
        @MinLength(5)
        private String passwordConfirmation;
    }
}
