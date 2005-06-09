package org.smartfrog.sfcore.languages.cdl.importing;

import java.net.URL;
import java.io.IOException;

/**
 */
public class ClasspathResolver extends BaseImportResolver {

    ClassLoader classLoader;

    public ClasspathResolver(ClassLoader classLoader) {
        setClassLoader(classLoader);
    }

    /**
     * use our own classloader
     */
    public ClasspathResolver() {
        setClassLoader(this.getClass().getClassLoader());
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * map the path to a URI. For in-classpath resolution, URLs of the type
     * returned by
     *
     * @param path
     *
     * @return the URL to the resource
     *
     * @throws java.io.IOException on failure to locate or other problems
     */
    public URL resolveToURL(String path) throws IOException {
        URL resource = getClassLoader().getResource(path);
        if(resource==null) {
            throw createResolutionFailure(path);
        }
        return resource;
    }


}
