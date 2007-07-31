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

    public static void registerClassLoader(ClassLoader loader, String annotation) {
        annotations.put(loader, annotation);
        loaders.put(annotation, loader);
    }

    static ClassLoader getClassLoaderForAnnotation(String annotation) {        
        ClassLoader loader = (ClassLoader) loaders.get(annotation);
        if (loader == null) {
//            System.out.println("Giving default class loader for annotation: " + annotation);
            loader = defaultCL;
        }
        if (annotation != null) System.out.println("Giving class loader: "+loader+" for annotation: " + annotation);
        return loader;
    }

    static String getAnnotationForClass(Class clazz) {
        String annotation = (String) annotations.get(clazz.getClassLoader());
        if (annotation != null) System.out.println("Giving annotation: " + annotation + " for class: " + clazz + " loaded by: " + clazz.getClassLoader());
        return annotation;
    }
}
