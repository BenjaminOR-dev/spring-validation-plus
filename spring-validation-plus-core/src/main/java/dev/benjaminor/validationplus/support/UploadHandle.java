package dev.benjaminor.validationplus.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstracción mínima de un archivo subido, independiente de Spring o Servlet.
 */
public interface UploadHandle {

    boolean isEmpty();

    long getSize();

    String getContentType();

    String getOriginalFilename();

    InputStream openStream() throws IOException;
}
