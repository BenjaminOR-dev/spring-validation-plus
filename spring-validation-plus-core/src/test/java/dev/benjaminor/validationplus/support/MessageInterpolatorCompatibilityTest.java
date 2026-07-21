package dev.benjaminor.validationplus.support;

import dev.benjaminor.validationplus.constraints.Between;
import dev.benjaminor.validationplus.constraints.Confirmed;
import dev.benjaminor.validationplus.constraints.ConditionalOperator;
import dev.benjaminor.validationplus.constraints.Digits;
import dev.benjaminor.validationplus.constraints.MaxLength;
import dev.benjaminor.validationplus.constraints.MinLength;
import dev.benjaminor.validationplus.constraints.Required;
import dev.benjaminor.validationplus.constraints.RequiredIf;
import dev.benjaminor.validationplus.constraints.RequiredWith;
import dev.benjaminor.validationplus.constraints.Size;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end interpolation checks for {@link ValidationPlusMessageInterpolator}.
 * Must pass on Hibernate Validator 8 (Spring Boot 3.x) and 9 (Spring Boot 4.x).
 */
class MessageInterpolatorCompatibilityTest {

    private Validator esValidator;
    private Validator enValidator;
    private Validator ptValidator;

    @BeforeEach
    void setUp() {
        esValidator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("es"));
        enValidator = ValidatorTestSupport.createValidator(Locale.ENGLISH);
        ptValidator = ValidatorTestSupport.createValidator(Locale.forLanguageTag("pt"));
    }

    @Test
    void requiredInterpolatesFieldNameInAllLocales() {
        RequiredBean bean = new RequiredBean();

        assertThat(message(esValidator, bean)).isEqualTo("El campo email es obligatorio.");
        assertThat(message(enValidator, bean)).isEqualTo("The email field is required.");
        assertThat(message(ptValidator, bean)).isEqualTo("O campo email é obrigatório.");
    }

    @Test
    void minLengthInterpolatesFieldAndMin() {
        LengthBean bean = new LengthBean();
        bean.name = "ab";

        assertThat(message(esValidator, bean, "name"))
                .isEqualTo("El campo name debe tener al menos 3 caracteres.");
    }

    @Test
    void maxLengthInterpolatesFieldAndMax() {
        LengthBean bean = new LengthBean();
        bean.name = "abcdefghijk";

        assertThat(message(esValidator, bean, "name"))
                .isEqualTo("El campo name no debe exceder 10 caracteres.");
    }

    @Test
    void betweenInterpolatesFieldMinAndMax() {
        BetweenBean bean = new BetweenBean();
        bean.age = 1;

        assertThat(message(esValidator, bean, "age"))
                .isEqualTo("El campo age debe estar entre 18 y 65.");
    }

    @Test
    void sizeInterpolatesFieldAndValueWithoutAssumingItems() {
        SizeBean bean = new SizeBean();
        bean.code = "ab";

        assertThat(message(esValidator, bean, "code"))
                .isEqualTo("El campo code debe tener tamaño 4.");
    }

    @Test
    void digitsInterpolatesIntegerAndFraction() {
        DigitsBean bean = new DigitsBean();
        bean.amount = new BigDecimal("1234.567");

        assertThat(message(esValidator, bean, "amount"))
                .isEqualTo("El campo amount debe tener 3 dígitos enteros y 2 decimales.");
    }

    @Test
    void requiredWithInterpolatesFieldAndOther() {
        RequiredWithBean bean = new RequiredWithBean();
        bean.password = "secret";

        assertThat(message(esValidator, bean))
                .isEqualTo(
                        "El campo passwordConfirmation es obligatorio cuando está presente alguno de estos campos: password.");
    }

    @Test
    void requiredIfInterpolatesConditionPhrase() {
        RequiredIfBean bean = new RequiredIfBean();
        bean.role = "ADMIN";

        assertThat(message(esValidator, bean))
                .isEqualTo("El campo adminCode es obligatorio cuando role es ADMIN.");
    }

    @Test
    void requiredIfInFormatsCommaSeparatedValues() {
        RequiredIfInBean bean = new RequiredIfInBean();
        bean.role = "ADMIN";

        assertThat(message(esValidator, bean))
                .isEqualTo("El campo adminCode es obligatorio cuando role es uno de ADMIN, MODERATOR.");
    }

    @Test
    void confirmedInterpolatesFieldName() {
        ConfirmedBean bean = new ConfirmedBean();
        bean.password = "secret";
        bean.passwordConfirmation = "other";

        assertThat(message(esValidator, bean))
                .isEqualTo("La confirmación del campo password no coincide.");
    }

    @Test
    void fieldNameIsNeverBlankWhenPropertyConstraintFails() {
        RequiredBean bean = new RequiredBean();

        String es = message(esValidator, bean);
        String en = message(enValidator, bean);
        String pt = message(ptValidator, bean);

        assertThat(es).doesNotContain("campo  ");
        assertThat(en).doesNotContain("The  field");
        assertThat(pt).doesNotContain("campo  ");
        assertThat(es).contains("email");
        assertThat(en).contains("email");
        assertThat(pt).contains("email");
    }

    private static String message(Validator validator, Object bean) {
        Set<ConstraintViolation<Object>> violations = validator.validate(bean);
        assertThat(violations).isNotEmpty();
        return violations.iterator().next().getMessage();
    }

    private static String message(Validator validator, Object bean, String property) {
        Set<ConstraintViolation<Object>> violations = validator.validateProperty(bean, property);
        assertThat(violations).isNotEmpty();
        return violations.iterator().next().getMessage();
    }

    static class RequiredBean {
        @Required
        String email;
    }

    static class LengthBean {
        @MinLength(3)
        @MaxLength(10)
        String name = "valid";
    }

    static class BetweenBean {
        @Between(min = 18, max = 65)
        Integer age;
    }

    static class SizeBean {
        @Size(4)
        String code;
    }

    static class DigitsBean {
        @Digits(integer = 3, fraction = 2)
        BigDecimal amount;
    }

    static class RequiredWithBean {
        String password;

        @RequiredWith("password")
        String passwordConfirmation;
    }

    @RequiredIf(field = "role", value = "ADMIN", required = "adminCode")
    static class RequiredIfBean {
        String role;
        String adminCode;
    }

    @RequiredIf(field = "role", value = "ADMIN,MODERATOR", operator = ConditionalOperator.IN, required = "adminCode")
    static class RequiredIfInBean {
        String role;
        String adminCode;
    }

    static class ConfirmedBean {
        String password;

        @Confirmed("password")
        String passwordConfirmation;
    }
}
