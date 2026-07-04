package dev.benjaminor.validationplus.spi;

import java.util.Optional;

/**
 * Registro global de implementaciones SPI usadas por {@code @Unique} y {@code @Exists}.
 */
public final class ValidationPlusCheckers {

    private static volatile UniquenessChecker uniquenessChecker;
    private static volatile ExistenceChecker existenceChecker;
    private static volatile ContextValueProvider contextValueProvider;

    private ValidationPlusCheckers() {
    }

    public static void registerUniquenessChecker(UniquenessChecker checker) {
        uniquenessChecker = checker;
    }

    public static void registerExistenceChecker(ExistenceChecker checker) {
        existenceChecker = checker;
    }

    public static void registerContextValueProvider(ContextValueProvider provider) {
        contextValueProvider = provider;
    }

    public static Optional<UniquenessChecker> uniquenessChecker() {
        return Optional.ofNullable(uniquenessChecker);
    }

    public static Optional<ExistenceChecker> existenceChecker() {
        return Optional.ofNullable(existenceChecker);
    }

    public static Optional<ContextValueProvider> contextValueProvider() {
        return Optional.ofNullable(contextValueProvider);
    }

    /**
     * Limpia los checkers registrados. Útil en tests.
     */
    public static void reset() {
        uniquenessChecker = null;
        existenceChecker = null;
        contextValueProvider = null;
    }
}
