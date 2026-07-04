package dev.benjaminor.validationplus.jpa.integration;

import dev.benjaminor.validationplus.constraints.Exists;
import dev.benjaminor.validationplus.constraints.Unique;
import dev.benjaminor.validationplus.jpa.JpaExistenceChecker;
import dev.benjaminor.validationplus.jpa.JpaUniquenessChecker;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JpaCheckerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("jpa-checker")
@Transactional
class JpaCheckerIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Validator validator;

    private Long existingRoleId;

    @BeforeEach
    void setUp() {
        assertThat(ValidationPlusCheckers.uniquenessChecker()).isPresent();
        assertThat(ValidationPlusCheckers.existenceChecker()).isPresent();
        assertThat(ValidationPlusCheckers.uniquenessChecker().orElseThrow())
                .isInstanceOf(JpaUniquenessChecker.class);
        assertThat(ValidationPlusCheckers.existenceChecker().orElseThrow())
                .isInstanceOf(JpaExistenceChecker.class);

        TestRole role = new TestRole("admin");
        entityManager.persist(role);
        entityManager.persist(new TestUser("taken@example.com"));
        entityManager.flush();
        existingRoleId = role.getId();
    }

    @Test
    void uniqueShouldFailWhenEmailAlreadyExistsInDatabase() {
        CreateUserRequest request = new CreateUserRequest();
        request.email = "taken@example.com";

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void uniqueShouldPassWhenEmailIsAvailable() {
        CreateUserRequest request = new CreateUserRequest();
        request.email = "free@example.com";

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void uniqueShouldIgnoreCurrentRecordOnUpdate() {
        TestUser existing = entityManager.createQuery(
                        "SELECT u FROM TestUser u WHERE u.email = :email", TestUser.class)
                .setParameter("email", "taken@example.com")
                .getSingleResult();

        UpdateUserRequest request = new UpdateUserRequest();
        request.id = existing.getId();
        request.email = "taken@example.com";

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void existsShouldPassWhenReferencedEntityIsPresent() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.roleId = existingRoleId;

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void existsShouldFailWhenReferencedEntityIsMissing() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.roleId = 999_999L;

        Set<ConstraintViolation<AssignRoleRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("roleId");
    }

    @Test
    void uniqueShouldBeCaseInsensitiveWhenConfigured() {
        CreateUserRequest request = new CreateUserRequest();
        request.email = "TAKEN@example.com";

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Unique(entity = TestUser.class, field = "email", column = "email", ignoreCase = true)
    static class CreateUserRequest {
        private String email;
    }

    @Unique(entity = TestUser.class, field = "email", column = "email", excludeField = "id")
    static class UpdateUserRequest {
        private Long id;
        private String email;
    }

    @Exists(entity = TestRole.class, field = "roleId", column = "id")
    static class AssignRoleRequest {
        private Long roleId;
    }
}
