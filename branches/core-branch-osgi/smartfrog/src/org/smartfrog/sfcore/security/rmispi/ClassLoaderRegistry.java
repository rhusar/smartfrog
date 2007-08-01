package org.smartfrog.sfcore.security.rmispi;

import java.util.Map;
import java.util.HashMap;

public class ClassLoaderRegistry {
    // ClassLoader -> String
    private static Map annotations = new HashMap();
    // String -> ClassLoader
    private static Map loaders = new HashMap();
    private static ClassLoader defaultCL;

    public static void init(ClassLoader defaultClassLoader) {
        System.out.println("Registering default class loader: " + defaultClassLoader);

        defaultCL = defaultClassLoader;
    }
    
    public static void cleanShutdown() {
        annotations.clear();
        loaders.clear();
    }

    // This would be easier to use if it took a Reference directly, but we can't have dependencies outside this package
    public static void registerClassLoader(ClassLoader loader, String annotation) {
        if (loader == null) throw new IllegalArgumentException("The class loader must not be null");
        // The null annotation is reserved for the default class loader.
        if (annotation == null) throw new IllegalArgumentException("The annotation must not be null");
        annotations.put(loader, annotation);
        loaders.put(annotation, loader);
    }

    public static ClassLoader getClassLoaderForAnnotation(String annotation) {
        ClassLoader loader = (ClassLoader) loaders.get(annotation);
        if (loader == null) {
//            System.out.println("Giving default class loader for annotation: " + annotation);
            loader = defaultCL;
        }
        if (annotation != null) System.out.println("Giving class loader: "+loader+" for annotation: " + annotation);
        return loader;
    }

    public static String getAnnotationForClass(Class clazz) {
        String annotation = (String) annotations.get(clazz.getClassLoader());
        if (annotation != null) System.out.println("Giving annotation: " + annotation + " for class: " + clazz + " loaded by: " + clazz.getClassLoader());
        return annotation;
    }
}
