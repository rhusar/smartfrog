package org.smartfrog.sfcore.languages.cdl.importing;

import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.importing.classpath.UrlFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * CDL document importing is a bit, well, more relaxed than the secure mechanism
 * of SmartFrog
 * This is the base import resolver.
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
     * Turn the reference URL into the source URL which can then be opened.
     * If any form of caching/retrieval is done, this should be where the
     * reference URL is turned into a local URL to a file: copy.
     *
     * @param referenceURL the URL returned by {@link #createReferenceURL(String)}
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    public URL convertToSourceURL(URL referenceURL) throws IOException {
        return referenceURL;
    }

    /**
     * Take a path and turn it in to an absolute URL in the schema of choice.
     * This is the URL that will be used for caching the import list, and
     * for relative references, not for loading the files
     *
     * @param path
     * @return a URL
     * @throws java.io.IOException
     */
    public URL createReferenceURL(String path) throws IOException {
        UrlFactory urlFactory = context.getUrlFactory();
        return urlFactory.createUrl(path);
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
