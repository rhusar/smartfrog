package org.smartfrog.sfcore.common;

/**
 * An exception that respresents the attempt to resolve a LAZY reference at static resolution (compile) time.
 * This never occurs at runtime since teh concept of LAZY does not exist.
 */
public class SmartFrogLazyResolutionException extends SmartFrogResolutionException {
    /**
     * Constructs a SmartFrogResolutionException with message.
     *
     * @param message exception message
     */
    public SmartFrogLazyResolutionException(String message) {
        super(message);
    }
}
