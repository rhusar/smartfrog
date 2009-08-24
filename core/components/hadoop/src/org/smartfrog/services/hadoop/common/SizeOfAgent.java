package org.smartfrog.services.hadoop.common;

import java.lang.instrument.Instrumentation;

/**
 * Uses the Instrumentation API to determine the size of an entity
 */
public class SizeOfAgent {

    static Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }

    public static boolean hasSizeof() {
        return instrumentation != null;
    }

    /**
     * Determine the size of an instance. Only works if we are running with instrumentation
     * @param instance object instance to look at
     * @return the size, or -1 if we don't know
     */
    public static long sizeOf(Object instance) {
        if(!hasSizeof()) {
            return -1;
        }
        return instrumentation.getObjectSize(instance);
    }

}
