package dev.benjaminor.validationplus.support;

import dev.benjaminor.validationplus.constraints.ConditionalOperator;

import java.util.Locale;
import java.util.Map;

/**
 * Locale-aware condition phrases for conditional constraint messages ({@code {condition}}).
 */
public final class ConditionalMessageSupport {

    private static final Map<ConditionalOperator, String> WHEN_EN = Map.of(
            ConditionalOperator.EQUALS, "is",
            ConditionalOperator.NOT_EQUALS, "is not",
            ConditionalOperator.IN, "is one of");

    private static final Map<ConditionalOperator, String> UNLESS_EN = WHEN_EN;

    private static final Map<ConditionalOperator, String> WHEN_ES = Map.of(
            ConditionalOperator.EQUALS, "es",
            ConditionalOperator.NOT_EQUALS, "no es",
            ConditionalOperator.IN, "es uno de");

    private static final Map<ConditionalOperator, String> UNLESS_ES = Map.of(
            ConditionalOperator.EQUALS, "sea",
            ConditionalOperator.NOT_EQUALS, "no sea",
            ConditionalOperator.IN, "sea uno de");

    private static final Map<ConditionalOperator, String> WHEN_PT = Map.of(
            ConditionalOperator.EQUALS, "é",
            ConditionalOperator.NOT_EQUALS, "não é",
            ConditionalOperator.IN, "é um de");

    private static final Map<ConditionalOperator, String> UNLESS_PT = Map.of(
            ConditionalOperator.EQUALS, "seja",
            ConditionalOperator.NOT_EQUALS, "não seja",
            ConditionalOperator.IN, "seja um de");

    private ConditionalMessageSupport() {
    }

    public static String conditionPhrase(ConditionalOperator operator, Locale locale, boolean unless) {
        ConditionalOperator effectiveOperator = operator != null ? operator : ConditionalOperator.EQUALS;
        String language = locale != null ? locale.getLanguage() : "en";

        return switch (language) {
            case "es" -> phrase(unless ? UNLESS_ES : WHEN_ES, effectiveOperator);
            case "pt" -> phrase(unless ? UNLESS_PT : WHEN_PT, effectiveOperator);
            default -> phrase(unless ? UNLESS_EN : WHEN_EN, effectiveOperator);
        };
    }

    private static String phrase(Map<ConditionalOperator, String> phrases, ConditionalOperator operator) {
        return phrases.getOrDefault(operator, phrases.get(ConditionalOperator.EQUALS));
    }
}
