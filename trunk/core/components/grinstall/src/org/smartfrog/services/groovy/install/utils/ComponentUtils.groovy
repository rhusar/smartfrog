package org.smartfrog.services.groovy.install.utils

/**
 * Utils to aid working with Groovy components
 */
public class ComponentUtils {

    public String extractClassHierarchy(instance) {
        StringBuilder builder = new StringBuilder()
        builder << "Instance $instance : ${instance.class} ${instance.class.classLoader}\n"
        listParents(builder, instance.class)
        return builder.toString()
    }

    def listParents(StringBuilder builder, Class clazz) {
        builder << clazz << " -- " << clazz.classLoader << "\n"
        def parent = clazz.getSuperclass()
        if (parent != null) {
            listParents(builder, parent)
        } else {
            builder << " -- \n "
        }
    }

}
