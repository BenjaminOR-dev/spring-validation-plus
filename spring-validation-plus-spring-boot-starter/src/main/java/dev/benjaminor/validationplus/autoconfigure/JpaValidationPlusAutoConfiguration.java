package dev.benjaminor.validationplus.autoconfigure;

import dev.benjaminor.validationplus.jpa.JpaExistenceChecker;
import dev.benjaminor.validationplus.jpa.JpaUniquenessChecker;
import dev.benjaminor.validationplus.spi.ExistenceChecker;
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * JPA auto-configuration for {@code @Unique} and {@code @Exists}.
 *
 * <p>Uses {@code afterName} for Spring Boot 3.x and 4.x Hibernate JPA auto-config class names
 * (package moved in Boot 4).
 */
@AutoConfiguration(afterName = {
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
@ConditionalOnClass({EntityManager.class, EntityManagerFactory.class})
@ConditionalOnBean(EntityManagerFactory.class)
@ConditionalOnProperty(prefix = "spring.validation-plus", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JpaValidationPlusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UniquenessChecker.class)
    public JpaUniquenessChecker jpaUniquenessChecker(EntityManager entityManager) {
        return new JpaUniquenessChecker(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean(ExistenceChecker.class)
    public JpaExistenceChecker jpaExistenceChecker(EntityManager entityManager) {
        return new JpaExistenceChecker(entityManager);
    }

    @Bean
    SmartInitializingSingleton validationPlusDatabaseCheckerRegistration(
            ObjectProvider<UniquenessChecker> uniquenessChecker,
            ObjectProvider<ExistenceChecker> existenceChecker) {
        return () -> {
            uniquenessChecker.ifAvailable(ValidationPlusCheckers::registerUniquenessChecker);
            existenceChecker.ifAvailable(ValidationPlusCheckers::registerExistenceChecker);
        };
    }
}
