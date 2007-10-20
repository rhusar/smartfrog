package org.smartfrog.sfcore.languages.cdl.importing;

import java.io.IOException;
import java.net.URL;

/**
 * Import resolver that pulls stuff off the classpath
 */
public class ClasspathResolver extends BaseImportResolver {

    private ClassLoader classLoader;

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
     * @return the URL to the resource
     * @throws java.io.IOException on failure to locate or other problems
     */
    public URL createReferenceURL(String path) throws IOException {
        URL resource = resolve(path);
        if (resource == null) {
            throw createResolutionFailure(path);
        }
        return resource;
    }

    /**
     * resolve; throw no exceptions but instead return null on failure
     * @param path
     * @return a url or a path
     */
    protected URL resolve(String path) {
        URL resource = getClassLoader().getResource(path);
        return resource;
    }


}
