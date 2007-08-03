package org.smartfrog.sfcore.security.rmispi;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class ClassLoaderRegistry {
    // String -> ClassLoader
    private static final Map loaders = new HashMap();
    private static ClassLoader defaultCL;

    public static void init(ClassLoader defaultClassLoader) {
        System.out.println("Registering default class loader: " + defaultClassLoader);

        defaultCL = defaultClassLoader;
    }
    
    public static void cleanShutdown() {
        loaders.clear();
        defaultCL = null;        
    }

    /**
     * Registers a new class loader for use with RMI.
     * The annotation used can be any String, but it has to be the same for the equivalent class loader on other
     * processes that will communicate with this one through RMI. Thus it is expected that users will use SmartFrog References
     * made local to a process.
     * This would be easier to use if it took a Reference directly, but we can't have outwards dependencies from this package.
     */
    public static void registerClassLoader(ClassLoader loader, String annotation) {
        if (loader == null) throw new IllegalArgumentException("The class loader must not be null");
        // The null annotation is reserved for the default class loader.
        if (annotation == null) throw new IllegalArgumentException("The annotation must not be null");
        System.out.println("Registering class loader:" + loader + " with annotation: \"" + annotation + '\"');
        loaders.put(annotation, loader);
    }

    public static ClassLoader getClassLoaderForAnnotation(String annotation) {
        ClassLoader loader;
        if (annotation == null) loader = defaultCL;
        else loader = (ClassLoader) loaders.get(annotation);
        
        if (loader == null)
            throw new IllegalStateException("Trying to get class loader for unknown annotation: " + annotation);

        if (annotation != null) System.out.println("Giving class loader: "+loader+" for annotation: " + annotation);
        return loader;
    }

    public static String getAnnotationForClass(Class clazz) {
        // Do NOT use a reverse map, as it is expected that hashCode/equals on some ClassLoaders can have changed since they were put in the loaders map.
        // This surfaced when using class loader proxies for OSGi.
        ClassLoader loader = clazz.getClassLoader();
        Iterator it = loaders.entrySet().iterator();
        String annotation = null;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(loader)) {
                annotation = (String) entry.getKey();
                break;
            }
        }
        if (annotation != null) System.out.println("Giving annotation: " + annotation + " for class: " + clazz + " loaded by: " + clazz.getClassLoader());
        return annotation;
    }

    static void printState() {
        System.out.println("Current state of the class loader registry:");
        System.out.println(loaders);
    }
}
