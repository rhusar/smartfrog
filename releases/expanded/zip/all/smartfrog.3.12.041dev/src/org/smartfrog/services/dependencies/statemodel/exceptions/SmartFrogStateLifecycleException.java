package org.smartfrog.services.dependencies.statemodel.exceptions;

import org.smartfrog.sfcore.prim.Prim;

/**

 */
public class SmartFrogStateLifecycleException extends SmartFrogStateException {
    public SmartFrogStateLifecycleException(String message, Prim sfObject) {
        super(message, sfObject);
    }

        public SmartFrogStateLifecycleException(String message, Exception cause, Prim sfObject) {
        super(message, cause, sfObject);
    }
}
