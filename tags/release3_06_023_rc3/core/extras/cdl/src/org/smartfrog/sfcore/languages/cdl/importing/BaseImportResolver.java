package org.smartfrog.sfcore.languages.cdl.importing;

import org.smartfrog.sfcore.languages.cdl.ParseContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 */
public class BaseImportResolver implements ImportResolver {

    private ParseContext context;
    public static final String ERROR_NO_RESOLUTION = "Unable to resolve :";

    /**
     * Bind to a context
     *
     * @param newcontext new context
     */
    public void bind(ParseContext newcontext) {
        context = newcontext;
    }

    /**
     * Get our parse context
     *
     * @return
     */
    public ParseContext getContext() {
        return context;
    }

    /**
     * map the path to a URI. For in-classpath resolution, URLs of the type
     * returned by
     *
     * @param path
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    public URL resolveToURL(String path) throws IOException {
        throw createResolutionFailure(path);
    }

    /**
     * creat an exception for throwing when there is a resolution failure
     *
     * @param path
     * @return
     */
    public IOException createResolutionFailure(String path) {
        return new FileNotFoundException(ERROR_NO_RESOLUTION + path);
    }


}
