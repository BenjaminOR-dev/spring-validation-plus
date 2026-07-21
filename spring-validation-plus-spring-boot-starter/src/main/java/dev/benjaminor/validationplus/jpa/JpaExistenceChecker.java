package dev.benjaminor.validationplus.jpa;

import dev.benjaminor.validationplus.spi.ExistenceCheckRequest;
import dev.benjaminor.validationplus.spi.ExistenceChecker;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of {@link ExistenceChecker}.
 *
 * <p>Supports multi-{@code EntityManagerFactory} apps via
 * {@link PersistenceUnitEntityManagerResolver} and
 * {@link ExistenceCheckRequest#persistenceUnit()}.
 */
@Transactional(readOnly = true)
public class JpaExistenceChecker implements ExistenceChecker {

    private final PersistenceUnitEntityManagerResolver entityManagerResolver;

    public JpaExistenceChecker(PersistenceUnitEntityManagerResolver entityManagerResolver) {
        this.entityManagerResolver = entityManagerResolver;
    }

    /**
     * Single-EM constructor (backward compatible).
     */
    public JpaExistenceChecker(EntityManager entityManager) {
        this(PersistenceUnitEntityManagerResolver.single(entityManager));
    }

    @Override
    public boolean exists(ExistenceCheckRequest request) {
        EntityManager entityManager = entityManagerResolver.resolve(request.persistenceUnit());

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
