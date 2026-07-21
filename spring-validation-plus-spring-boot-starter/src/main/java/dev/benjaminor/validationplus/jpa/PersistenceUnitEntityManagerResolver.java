package dev.benjaminor.validationplus.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Resolves an {@link EntityManager} by JPA persistence unit name for multi-EMF apps.
 *
 * <p>Blank / null unit → primary EMF. Named unit → lookup by persistence unit name
 * or Spring bean name.
 */
public final class PersistenceUnitEntityManagerResolver {

    private final EntityManagerFactory primary;
    private final Map<String, EntityManagerFactory> byName;
    private final EntityManager fixedEntityManager;

    private PersistenceUnitEntityManagerResolver(
            EntityManagerFactory primary,
            Map<String, EntityManagerFactory> byName,
            EntityManager fixedEntityManager) {
        this.primary = primary;
        this.byName = Map.copyOf(byName);
        this.fixedEntityManager = fixedEntityManager;
    }

    /**
     * Single-EM mode (tests / custom wiring) — ignores the persistence unit name.
     */
    public static PersistenceUnitEntityManagerResolver single(EntityManager entityManager) {
        Objects.requireNonNull(entityManager, "entityManager");
        return new PersistenceUnitEntityManagerResolver(null, Map.of(), entityManager);
    }

    /**
     * Builds a resolver from all {@link EntityManagerFactory} beans in the context.
     */
    public static PersistenceUnitEntityManagerResolver from(
            ApplicationContext context,
            Map<String, EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories");
        if (entityManagerFactories.isEmpty()) {
            throw new IllegalStateException("No EntityManagerFactory beans available for Validation Plus");
        }

        EntityManagerFactory primary = resolvePrimary(context, entityManagerFactories);
        Map<String, EntityManagerFactory> byName = new LinkedHashMap<>();
        entityManagerFactories.forEach((beanName, emf) -> {
            byName.put(beanName, emf);
            String unitName = persistenceUnitName(emf);
            if (StringUtils.hasText(unitName)) {
                byName.put(unitName, emf);
            }
        });

        return new PersistenceUnitEntityManagerResolver(primary, byName, null);
    }

    public EntityManager resolve(String persistenceUnit) {
        if (fixedEntityManager != null) {
            return fixedEntityManager;
        }

        EntityManagerFactory emf;
        if (!StringUtils.hasText(persistenceUnit)) {
            emf = primary;
        } else {
            emf = byName.get(persistenceUnit);
            if (emf == null) {
                throw new IllegalStateException(
                        "Unknown persistence unit '%s' for Validation Plus. Available: %s"
                                .formatted(persistenceUnit, byName.keySet()));
            }
        }

        EntityManager transactional = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        if (transactional != null) {
            return transactional;
        }
        return SharedEntityManagerCreator.createSharedEntityManager(emf);
    }

    public static EntityManagerFactory resolvePrimary(
            ApplicationContext context,
            Map<String, EntityManagerFactory> entityManagerFactories) {
        if (context instanceof ConfigurableApplicationContext configurable) {
            ConfigurableListableBeanFactory beanFactory = configurable.getBeanFactory();
            for (String beanName : entityManagerFactories.keySet()) {
                if (beanFactory.containsBeanDefinition(beanName)
                        && beanFactory.getMergedBeanDefinition(beanName).isPrimary()) {
                    return entityManagerFactories.get(beanName);
                }
            }
        }
        for (String beanName : entityManagerFactories.keySet()) {
            if (context.findAnnotationOnBean(beanName, Primary.class) != null) {
                return entityManagerFactories.get(beanName);
            }
        }
        if (entityManagerFactories.containsKey("entityManagerFactory")) {
            return entityManagerFactories.get("entityManagerFactory");
        }
        return entityManagerFactories.values().iterator().next();
    }

    static String persistenceUnitName(EntityManagerFactory emf) {
        if (emf instanceof EntityManagerFactoryInfo info) {
            return info.getPersistenceUnitName();
        }
        return null;
    }
}
