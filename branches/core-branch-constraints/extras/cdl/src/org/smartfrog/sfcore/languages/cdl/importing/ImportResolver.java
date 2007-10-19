package org.smartfrog.sfcore.languages.cdl.importing;

import org.smartfrog.sfcore.languages.cdl.ParseContext;

import java.io.IOException;
import java.net.URL;

/**
 * This what things that resolve imports implement
 */
public interface ImportResolver {

    /**
     * Bind to a context
     *
     * @param context new context
     */
    void bind(ParseContext context);

    /**
     * Take a path and turn it in to an absolute URL in the schema of choice.
     * This is the URL that will be used for caching the import list, and
     * for relative references, not for loading the files
     * @param path
     * @return a URL
     * @throws IOException
     */
    URL createReferenceURL(String path) throws IOException;

    /**
     * Turn the reference URL into the source URL which can then be opened.
     * If any form of caching/retrieval is done, this should be where the
     * reference URL is turned into a local URL to a file: copy.
     *
     * @param referenceURL the URL returned by {@link #createReferenceURL(String)}
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    URL convertToSourceURL(URL referenceURL) throws IOException;
}
