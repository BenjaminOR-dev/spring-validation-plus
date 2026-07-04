package dev.benjaminor.validationplus.support;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilidades para validaciones de red.
 */
public final class NetworkValidationUtils {

    private static final Pattern MAC_ADDRESS = Pattern.compile(
            "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");

    private NetworkValidationUtils() {
    }

    public static boolean isIp(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        try {
            InetAddress.getByName(charSequence.toString().trim());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isIpv4(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        try {
            return InetAddress.getByName(charSequence.toString().trim()) instanceof Inet4Address;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isIpv6(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        try {
            return InetAddress.getByName(charSequence.toString().trim()) instanceof Inet6Address;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isMacAddress(Object value) {
        return PatternValidationUtils.matchesRegex(value, MAC_ADDRESS);
    }

    public static boolean isActiveUrl(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        String candidate = charSequence.toString().trim();
        try {
            URI uri = new URI(candidate);
            String scheme = uri.getScheme();
            if (scheme == null) {
                return false;
            }
            String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
            if (!"http".equals(normalizedScheme) && !"https".equals(normalizedScheme)) {
                return false;
            }
            return uri.getHost() != null && !uri.getHost().isBlank();
        } catch (URISyntaxException exception) {
            return false;
        }
    }
}
