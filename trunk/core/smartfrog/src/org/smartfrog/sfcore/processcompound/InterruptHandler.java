package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.logging.LogSF;

/**
 * Interface used to decouple a Sun-JVM-only class from the rest of the system.
 */
public interface InterruptHandler {


    /**
     * bind to a signal. On HP-UX+cruise control this fails with an error,
     * one we don't see on the command line.
     * This handler catches the exception and logs it, so that smartfrog
     * keeps running even if graceful shutdown is broken.
     * @param name name of interrupt to bind to.
     * @param log
     */
    void bind(String name, LogSF log);
}
