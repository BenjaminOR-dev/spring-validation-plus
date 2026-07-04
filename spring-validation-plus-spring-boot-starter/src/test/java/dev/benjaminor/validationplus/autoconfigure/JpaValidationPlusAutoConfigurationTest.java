package dev.benjaminor.validationplus.autoconfigure;

import dev.benjaminor.validationplus.jpa.JpaExistenceChecker;
import dev.benjaminor.validationplus.jpa.JpaUniquenessChecker;
import dev.benjaminor.validationplus.spi.ExistenceChecker;
import dev.benjaminor.validationplus.spi.UniquenessChecker;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JpaValidationPlusAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JpaValidationPlusAutoConfiguration.class))
            .withBean(EntityManagerFactory.class, () -> mock(EntityManagerFactory.class))
            .withBean(EntityManager.class, () -> mock(EntityManager.class));

    @AfterEach
    void tearDown() {
        ValidationPlusCheckers.reset();
    }

    @Test
    void shouldRegisterJpaCheckersWhenNoCustomBeansExist() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JpaUniquenessChecker.class);
            assertThat(context).hasSingleBean(JpaExistenceChecker.class);
            assertThat(ValidationPlusCheckers.uniquenessChecker()).isPresent();
            assertThat(ValidationPlusCheckers.existenceChecker()).isPresent();
        });
    }

    @Test
    void shouldSkipJpaUniquenessCheckerWhenCustomUniquenessCheckerExists() {
        contextRunner.withBean(UniquenessChecker.class, () -> request -> true)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(JpaUniquenessChecker.class);
                    assertThat(context).hasSingleBean(UniquenessChecker.class);
                    assertThat(context).hasSingleBean(JpaExistenceChecker.class);
                    assertThat(ValidationPlusCheckers.uniquenessChecker()).isPresent();
                });
    }

    @Test
    void shouldSkipJpaExistenceCheckerWhenCustomExistenceCheckerExists() {
        contextRunner.withBean(ExistenceChecker.class, () -> request -> true)
                .run(context -> {
                    assertThat(context).hasSingleBean(JpaUniquenessChecker.class);
                    assertThat(context).doesNotHaveBean(JpaExistenceChecker.class);
                    assertThat(context).hasSingleBean(ExistenceChecker.class);
                    assertThat(ValidationPlusCheckers.existenceChecker()).isPresent();
                });
    }

    @Test
    void shouldRegisterCustomUniquenessCheckerInSpi() {
        UniquenessChecker customChecker = request -> true;

        contextRunner.withBean(UniquenessChecker.class, () -> customChecker)
                .run(context -> {
                    assertThat(ValidationPlusCheckers.uniquenessChecker()).contains(customChecker);
                });
    }
}
