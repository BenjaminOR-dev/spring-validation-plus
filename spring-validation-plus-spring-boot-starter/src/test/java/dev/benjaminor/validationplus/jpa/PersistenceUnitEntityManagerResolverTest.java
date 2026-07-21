package dev.benjaminor.validationplus.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PersistenceUnitEntityManagerResolverTest {

    @Test
    void single_ignoresPersistenceUnitName() {
        EntityManager em = mock(EntityManager.class);
        PersistenceUnitEntityManagerResolver resolver = PersistenceUnitEntityManagerResolver.single(em);

        assertThat(resolver.resolve("")).isSameAs(em);
        assertThat(resolver.resolve("anything")).isSameAs(em);
    }

    @Test
    void from_blankUsesPrimary() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MultiEmfConfig.class)) {
            Map<String, EntityManagerFactory> factories = context.getBeansOfType(EntityManagerFactory.class);
            PersistenceUnitEntityManagerResolver resolver =
                    PersistenceUnitEntityManagerResolver.from(context, factories);

            EntityManager resolved = resolver.resolve("");
            assertThat(resolved).isNotNull();

            EntityManager named = resolver.resolve("nomina");
            assertThat(named).isNotNull();
        }
    }

    @Test
    void from_unknownUnitThrows() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MultiEmfConfig.class)) {
            Map<String, EntityManagerFactory> factories = context.getBeansOfType(EntityManagerFactory.class);
            PersistenceUnitEntityManagerResolver resolver =
                    PersistenceUnitEntityManagerResolver.from(context, factories);

            assertThatThrownBy(() -> resolver.resolve("missing-unit"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("missing-unit");
        }
    }

    @Configuration
    static class MultiEmfConfig {

        @Bean
        @Primary
        DataSource capaDataSource() {
            return embeddedDataSource("capa");
        }

        @Bean
        DataSource nominaDataSource() {
            return embeddedDataSource("nomina");
        }

        @Bean
        @Primary
        LocalContainerEntityManagerFactoryBean entityManagerFactoryCapa(DataSource capaDataSource) {
            return emf(capaDataSource, "capa");
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactoryNomina(
                @org.springframework.beans.factory.annotation.Qualifier("nominaDataSource") DataSource nominaDataSource) {
            return emf(nominaDataSource, "nomina");
        }

        private static DataSource embeddedDataSource(String name) {
            org.springframework.jdbc.datasource.DriverManagerDataSource ds =
                    new org.springframework.jdbc.datasource.DriverManagerDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1");
            ds.setUsername("sa");
            ds.setPassword("");
            return ds;
        }

        private static LocalContainerEntityManagerFactoryBean emf(DataSource dataSource, String unit) {
            LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
            bean.setDataSource(dataSource);
            bean.setPackagesToScan(PersistenceUnitEntityManagerResolverTest.class.getPackageName());
            bean.setPersistenceUnitName(unit);
            bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            bean.setJpaPropertyMap(Map.of(
                    "hibernate.hbm2ddl.auto", "none",
                    "hibernate.dialect", "org.hibernate.dialect.H2Dialect"));
            return bean;
        }
    }
}
