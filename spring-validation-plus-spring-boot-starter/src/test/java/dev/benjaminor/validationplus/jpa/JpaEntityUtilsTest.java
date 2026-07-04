package dev.benjaminor.validationplus.jpa;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JpaEntityUtilsTest {

    @Test
    void convertIdShouldResolveInheritedIdField() {
        Object converted = JpaEntityUtils.convertId(ChildEntity.class, "id", "42");

        assertThat(converted).isEqualTo(42L);
    }

    static class BaseEntity {
        private Long id;
    }

    static class ChildEntity extends BaseEntity {
        private String name;
    }
}
