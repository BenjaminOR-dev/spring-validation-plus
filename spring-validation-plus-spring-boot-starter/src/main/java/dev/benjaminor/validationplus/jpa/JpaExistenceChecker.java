package dev.benjaminor.validationplus.jpa;

import dev.benjaminor.validationplus.spi.ExistenceCheckRequest;
import dev.benjaminor.validationplus.spi.ExistenceChecker;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA de {@link ExistenceChecker}.
 */
@Transactional(readOnly = true)
public class JpaExistenceChecker implements ExistenceChecker {

    private final EntityManager entityManager;

    public JpaExistenceChecker(EntityManager entityManager) {
        this.entityManager = entityManager;
        ValidationPlusCheckers.registerExistenceChecker(this);
    }

    @Override
    public boolean exists(ExistenceCheckRequest request) {
        String entityName = JpaEntityUtils.resolveEntityName(request.entity());
        String whereClause = JpaEntityUtils.buildEqualityClause(
                request.column(), "value", request.ignoreCase(), request.value());

        String jpql = "SELECT COUNT(e) FROM %s e WHERE %s".formatted(entityName, whereClause);

        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("value", JpaEntityUtils.normalizeValue(request.value()))
                .getSingleResult();

        return count != null && count > 0;
    }
}
