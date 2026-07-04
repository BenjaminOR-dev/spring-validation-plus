package dev.benjaminor.validationplus.autoconfigure;

import dev.benjaminor.validationplus.support.ValidationPlusMessageInterpolator;
import jakarta.validation.MessageInterpolator;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Message interpolator that uses the HTTP request locale ({@link LocaleContextHolder}) when available.
 */
public class SpringLocaleValidationPlusMessageInterpolator extends ValidationPlusMessageInterpolator {

    @Override
    public String interpolate(String messageTemplate, MessageInterpolator.Context context, Locale locale) {
        return super.interpolate(messageTemplate, context, resolveLocale(locale));
    }

    private Locale resolveLocale(Locale locale) {
        if (locale != null) {
            return locale;
        }
        Locale requestLocale = LocaleContextHolder.getLocale();
        return requestLocale != null ? requestLocale : Locale.getDefault();
    }
}
