package org.smartfrog.services.dependencies.statemodel.exceptions;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 */
public class SmartFrogStateException extends SmartFrogException {
    public SmartFrogStateException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    public SmartFrogStateException(String message, Exception cause, Prim sfObject) {
        super(message, cause, sfObject);
    }
}
