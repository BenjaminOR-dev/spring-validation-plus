package dev.benjaminor.validationplus.jpa;

import dev.benjaminor.validationplus.spi.UniqueCheckRequest;
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of {@link UniquenessChecker}.
 */
@Transactional(readOnly = true)
public class JpaUniquenessChecker implements UniquenessChecker {

    private final EntityManager entityManager;

    public JpaUniquenessChecker(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean isUnique(UniqueCheckRequest request) {
        String entityName = JpaEntityUtils.resolveEntityName(request.entity());
        String whereClause = JpaEntityUtils.buildEqualityClause(
                request.column(), "value", request.ignoreCase(), request.value());

        String jpql = "SELECT COUNT(e) FROM %s e WHERE %s".formatted(entityName, whereClause);

        if (request.excludeId() != null) {
            jpql += " AND e.%s != :excludeId".formatted(request.excludeColumn());
        }

        var query = entityManager.createQuery(jpql, Long.class)
                .setParameter("value", JpaEntityUtils.normalizeValue(request.value()));

        if (request.excludeId() != null) {
            query.setParameter("excludeId", JpaEntityUtils.convertId(
                    request.entity(), request.excludeColumn(), request.excludeId()));
        }

        Long count = query.getSingleResult();
        return count == null || count == 0;
    }
}
