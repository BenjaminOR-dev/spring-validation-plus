package dev.benjaminor.validationplus.support;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utilities for validaciones de red.
 */
public final class NetworkValidationUtils {

    private static final Pattern MAC_ADDRESS = Pattern.compile(
            "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    private static final Pattern IPV6_LITERAL = Pattern.compile("^[0-9a-fA-F:.]+$");

    private NetworkValidationUtils() {
    }

    public static boolean isIp(Object value) {
        return isIpv4(value) || isIpv6(value);
    }

    public static boolean isIpv4(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        return isIpv4Literal(charSequence.toString().trim());
    }

    public static boolean isIpv6(Object value) {
        if (EmptyUtils.isBlank(value)) {
            return true;
        }
        if (!(value instanceof CharSequence charSequence)) {
            return false;
        }
        String candidate = charSequence.toString().trim();
        if (!candidate.contains(":") || !IPV6_LITERAL.matcher(candidate).matches()) {
            return false;
        }
        try {
            return InetAddress.getByName(candidate) instanceof Inet6Address;
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

    private static boolean isIpv4Literal(String candidate) {
        String[] octets = candidate.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        for (String octet : octets) {
            if (octet.isEmpty() || octet.length() > 3) {
                return false;
            }
            if (octet.length() > 1 && octet.startsWith("0")) {
                return false;
            }
            try {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException exception) {
                return false;
            }
        }
        return true;
    }
}
