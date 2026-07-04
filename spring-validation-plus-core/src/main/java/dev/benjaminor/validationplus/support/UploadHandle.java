package dev.benjaminor.validationplus.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * Minimal abstraction of an uploaded file, independent of Spring or Servlet.
 */
public interface UploadHandle {

    boolean isEmpty();

    long getSize();

    String getContentType();

    String getOriginalFilename();

    InputStream openStream() throws IOException;
}
