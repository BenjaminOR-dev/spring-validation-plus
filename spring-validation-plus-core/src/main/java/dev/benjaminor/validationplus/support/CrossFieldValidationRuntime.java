package dev.benjaminor.validationplus.support;

/**
 * Holds the bean currently being validated so field-level cross-field constraints
 * can read sibling properties.
 */
public final class CrossFieldValidationRuntime {

    private static final ThreadLocal<Object> ROOT_BEAN = new ThreadLocal<>();

    private CrossFieldValidationRuntime() {
    }

    public static void setRootBean(Object rootBean) {
        if (rootBean == null) {
            ROOT_BEAN.remove();
            return;
        }
        ROOT_BEAN.set(rootBean);
    }

    public static Object getRootBean() {
        return ROOT_BEAN.get();
    }

    public static void clearRootBean() {
        ROOT_BEAN.remove();
    }
}
