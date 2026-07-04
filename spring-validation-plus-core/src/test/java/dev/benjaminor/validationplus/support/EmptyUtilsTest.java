package dev.benjaminor.validationplus.support;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmptyUtilsTest {

    @Test
    void shouldDetectEmptyValues() {
        assertThat(EmptyUtils.isEmpty(null)).isTrue();
        assertThat(EmptyUtils.isEmpty("")).isTrue();
        assertThat(EmptyUtils.isEmpty("   ")).isTrue();
        assertThat(EmptyUtils.isEmpty(List.of())).isTrue();
        assertThat(EmptyUtils.isEmpty(new String[0])).isTrue();
        assertThat(EmptyUtils.isEmpty("value")).isFalse();
    }
}
