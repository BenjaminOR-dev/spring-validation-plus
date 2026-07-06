package dev.benjaminor.validationplus.support;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionalComparisonUtilsTest {

    @Test
    void equalsShouldMatchSameStringValue() {
        assertTrue(ConditionalComparisonUtils.matches("ADMIN", "ADMIN", ConditionalOperator.EQUALS));
        assertFalse(ConditionalComparisonUtils.matches("USER", "ADMIN", ConditionalOperator.EQUALS));
    }

    @Test
    void notEqualsShouldMatchDifferentValues() {
        assertTrue(ConditionalComparisonUtils.matches("ADMIN", "GUEST", ConditionalOperator.NOT_EQUALS));
        assertFalse(ConditionalComparisonUtils.matches("GUEST", "GUEST", ConditionalOperator.NOT_EQUALS));
    }

    @Test
    void inShouldMatchAnyCommaSeparatedValue() {
        assertTrue(ConditionalComparisonUtils.matches("ADMIN", "ADMIN,MODERATOR", ConditionalOperator.IN));
        assertTrue(ConditionalComparisonUtils.matches("MODERATOR", "ADMIN, MODERATOR", ConditionalOperator.IN));
        assertFalse(ConditionalComparisonUtils.matches("USER", "ADMIN,MODERATOR", ConditionalOperator.IN));
    }
}
