package dev.benjaminor.validationplus.autoconfigure;

import dev.benjaminor.validationplus.exception.ValidationExceptionHandler;
import dev.benjaminor.validationplus.properties.SpringValidationPlusProperties;
import dev.benjaminor.validationplus.spi.ContextValueProvider;
import dev.benjaminor.validationplus.spi.ValidationPlusCheckers;
import dev.benjaminor.validationplus.web.RequestContextValueProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Main Spring Validation Plus auto-configuration.
 */
@AutoConfiguration(before = ValidationAutoConfiguration.class)
@ConditionalOnClass({LocalValidatorFactoryBean.class, DispatcherServlet.class})
@ConditionalOnProperty(prefix = "spring.validation-plus", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SpringValidationPlusProperties.class)
public class SpringValidationPlusAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(ContextValueProvider.class)
    public ContextValueProvider validationPlusContextValueProvider() {
        return new RequestContextValueProvider();
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    SmartInitializingSingleton validationPlusContextValueProviderRegistration(ContextValueProvider provider) {
        return () -> ValidationPlusCheckers.registerContextValueProvider(provider);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalValidatorFactoryBean validationPlusValidatorFactoryBean() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setMessageInterpolator(new SpringLocaleValidationPlusMessageInterpolator());
        return factoryBean;
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "spring.validation-plus.exception-handler", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(ValidationExceptionHandler.class)
    public ValidationExceptionHandler validationExceptionHandler() {
        return new ValidationExceptionHandler();
    }
}
