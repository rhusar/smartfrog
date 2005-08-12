package org.smartfrog.sfcore.languages.cdl.importing;

import org.smartfrog.sfcore.languages.cdl.ParseContext;

import java.io.IOException;
import java.net.URL;

/**
 * This is our import resolving
 */
public interface ImportResolver {

    /**
     * Bind to a context
     *
     * @param context new context
     */
    void bind(ParseContext context);

    /**
     * map the path to a URI. For in-classpath resolution, URLs of the type
     * returned by
     *
     * @param path
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    URL resolveToURL(String path) throws IOException;
}
