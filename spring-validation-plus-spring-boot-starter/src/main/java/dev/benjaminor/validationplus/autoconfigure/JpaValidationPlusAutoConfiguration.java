package dev.benjaminor.validationplus.autoconfigure;

import dev.benjaminor.validationplus.jpa.JpaExistenceChecker;
import dev.benjaminor.validationplus.jpa.JpaUniquenessChecker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuración JPA para {@code @Unique} y {@code @Exists}.
 */
@AutoConfiguration(after = HibernateJpaAutoConfiguration.class)
@ConditionalOnClass({EntityManager.class, EntityManagerFactory.class})
@ConditionalOnBean(EntityManagerFactory.class)
public class JpaValidationPlusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JpaUniquenessChecker jpaUniquenessChecker(EntityManager entityManager) {
        return new JpaUniquenessChecker(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaExistenceChecker jpaExistenceChecker(EntityManager entityManager) {
        return new JpaExistenceChecker(entityManager);
    }
}
