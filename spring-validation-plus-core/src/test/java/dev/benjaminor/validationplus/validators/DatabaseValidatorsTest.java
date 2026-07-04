package dev.benjaminor.validationplus.validators;

import dev.benjaminor.validationplus.constraints.Exists;
import dev.benjaminor.validationplus.constraints.Unique;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import dev.benjaminor.validationplus.support.ValidatorTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseValidatorsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidationPlusCheckers.reset();
        validator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
    }

    @AfterEach
    void tearDown() {
        ValidationPlusCheckers.reset();
    }

    @Test
    void uniqueShouldFailWhenCheckerFindsDuplicate() {
        registerUniqueChecker(false);

        UniqueDto dto = new UniqueDto();
        dto.email = "taken@example.com";

        Set<ConstraintViolation<UniqueDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void uniqueShouldPassWhenCheckerFindsNoDuplicate() {
        registerUniqueChecker(true);

        UniqueDto dto = new UniqueDto();
        dto.email = "free@example.com";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void uniqueShouldIgnoreCurrentIdOnUpdate() {
        registerUniqueChecker(true);

        UniqueUpdateDto dto = new UniqueUpdateDto();
        dto.id = 1L;
        dto.email = "user@example.com";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void uniqueShouldIgnoreCurrentIdFromContextParameter() {
        ValidationPlusCheckers.registerContextValueProvider(key -> {
            if ("id".equals(key)) {
                return java.util.Optional.of(1L);
            }
            return java.util.Optional.empty();
        });
        registerUniqueChecker(true);

        UniqueContextDto dto = new UniqueContextDto();
        dto.email = "user@example.com";

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void existsShouldResolveValueFromContextParameter() {
        ValidationPlusCheckers.registerContextValueProvider(key -> {
            if ("roleId".equals(key)) {
                return java.util.Optional.of(1L);
            }
            return java.util.Optional.empty();
        });
        registerExistenceChecker(true);

        ExistsContextDto dto = new ExistsContextDto();

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void uniqueShouldFailWhenCheckerIsMissing() {
        UniqueDto dto = new UniqueDto();
        dto.email = "user@example.com";

        Set<ConstraintViolation<UniqueDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("verificador de unicidad");
    }

    @Test
    void existsShouldFailWhenCheckerIsMissing() {
        ExistsDto dto = new ExistsDto();
        dto.roleId = 1L;

        Set<ConstraintViolation<ExistsDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("verificador de existencia");
    }

    @Test
    void existsShouldFailWhenCheckerDoesNotFindValue() {
        registerExistenceChecker(false);

        ExistsDto dto = new ExistsDto();
        dto.roleId = 999L;

        Set<ConstraintViolation<ExistsDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("roleId");
    }

    @Test
    void existsShouldPassWhenCheckerFindsValue() {
        registerExistenceChecker(true);

        ExistsDto dto = new ExistsDto();
        dto.roleId = 1L;

        assertThat(validator.validate(dto)).isEmpty();
    }

    private void registerUniqueChecker(boolean unique) {
        ValidationPlusCheckers.registerUniquenessChecker(request -> {
            if ("taken@example.com".equalsIgnoreCase(String.valueOf(request.value()))) {
                return false;
            }
            if (request.excludeId() != null && "user@example.com".equalsIgnoreCase(String.valueOf(request.value()))) {
                return true;
            }
            return unique;
        });
    }

    private void registerExistenceChecker(boolean exists) {
        ValidationPlusCheckers.registerExistenceChecker(request ->
                exists || Long.valueOf(1L).equals(request.value()));
    }

    @Unique(entity = StubEntity.class, field = "email", column = "email", excludeParameter = "id")
    static class UniqueContextDto {
        private String email;
    }

    @Exists(entity = StubEntity.class, field = "roleId", column = "id", parameter = "roleId")
    static class ExistsContextDto {
    }

    @Unique(entity = StubEntity.class, field = "email", column = "email")
    static class UniqueDto {
        private String email;
    }

    @Unique(entity = StubEntity.class, field = "email", column = "email", excludeField = "id")
    static class UniqueUpdateDto {
        private Long id;
        private String email;
    }

    @Exists(entity = StubEntity.class, field = "roleId", column = "id")
    static class ExistsDto {
        private Long roleId;
    }

    static class StubEntity {
        private Long id;
        private String email;
    }
}
