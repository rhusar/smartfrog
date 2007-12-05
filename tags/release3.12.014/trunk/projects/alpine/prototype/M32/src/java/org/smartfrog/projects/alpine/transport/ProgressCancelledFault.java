package org.smartfrog.projects.alpine.transport;

import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

/**
 * Fault to throw if progress is cancelled
 */
public class ProgressCancelledFault extends AlpineRuntimeException {

    public ProgressCancelledFault(String message) {
        super(message);
    }

}
