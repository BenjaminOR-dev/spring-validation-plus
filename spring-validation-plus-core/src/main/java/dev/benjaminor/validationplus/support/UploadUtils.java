package dev.benjaminor.validationplus.support;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Resuelve objetos de upload comunes ({@code MultipartFile}, {@code Part}, {@link UploadHandle}).
 */
public final class UploadUtils {

    private UploadUtils() {
    }

    public static Optional<UploadHandle> resolve(Object value) {
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof UploadHandle handle) {
            return Optional.of(handle);
        }
        Optional<UploadHandle> multipart = fromReflective(value, "org.springframework.web.multipart.MultipartFile",
                "isEmpty", "getSize", "getContentType", "getOriginalFilename", "getInputStream");
        if (multipart.isPresent()) {
            return multipart;
        }
        return fromReflective(value, "jakarta.servlet.http.Part",
                null, "getSize", "getContentType", "getSubmittedFileName", "getInputStream");
    }

    private static Optional<UploadHandle> fromReflective(
            Object value,
            String typeName,
            String emptyMethod,
            String sizeMethod,
            String contentTypeMethod,
            String filenameMethod,
            String streamMethod) {
        if (!isInstanceOf(value, typeName)) {
            return Optional.empty();
        }
        return Optional.of(new ReflectiveUploadHandle(value, emptyMethod, sizeMethod, contentTypeMethod, filenameMethod, streamMethod));
    }

    private static boolean isInstanceOf(Object value, String typeName) {
        try {
            Class<?> type = Class.forName(typeName);
            return type.isInstance(value);
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    private static final class ReflectiveUploadHandle implements UploadHandle {

        private final Object target;
        private final Method emptyMethod;
        private final Method sizeMethod;
        private final Method contentTypeMethod;
        private final Method filenameMethod;
        private final Method streamMethod;

        ReflectiveUploadHandle(
                Object target,
                String emptyMethodName,
                String sizeMethodName,
                String contentTypeMethodName,
                String filenameMethodName,
                String streamMethodName) {
            this.target = target;
            this.emptyMethod = emptyMethodName != null ? findMethod(target.getClass(), emptyMethodName) : null;
            this.sizeMethod = findMethod(target.getClass(), sizeMethodName);
            this.contentTypeMethod = findMethod(target.getClass(), contentTypeMethodName);
            this.filenameMethod = findMethod(target.getClass(), filenameMethodName);
            this.streamMethod = findMethod(target.getClass(), streamMethodName);
        }

        @Override
        public boolean isEmpty() {
            if (emptyMethod != null) {
                Object result = invoke(emptyMethod);
                return Boolean.TRUE.equals(result);
            }
            long size = getSize();
            String filename = getOriginalFilename();
            return size == 0 && EmptyUtils.isBlank(filename);
        }

        @Override
        public long getSize() {
            Object result = invoke(sizeMethod);
            if (result instanceof Number number) {
                return number.longValue();
            }
            return 0L;
        }

        @Override
        public String getContentType() {
            Object result = invoke(contentTypeMethod);
            return result != null ? result.toString() : null;
        }

        @Override
        public String getOriginalFilename() {
            Object result = invoke(filenameMethod);
            return result != null ? result.toString() : null;
        }

        @Override
        public InputStream openStream() throws IOException {
            Object result = invoke(streamMethod);
            if (result instanceof InputStream inputStream) {
                return inputStream;
            }
            throw new IOException("Unable to open upload stream");
        }

        private Object invoke(Method method) {
            if (method == null) {
                return null;
            }
            try {
                return method.invoke(target);
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to invoke upload method '" + method.getName() + "'", exception);
            }
        }

        private static Method findMethod(Class<?> type, String methodName) {
            try {
                Method method = type.getMethod(methodName);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException exception) {
                throw new IllegalStateException("Upload type missing method '" + methodName + "'", exception);
            }
        }
    }
}
