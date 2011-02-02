package org.smartfrog.services.cloudfarmer.client.web.exceptions;

import java.io.IOException;

/**
 * the cluster controller is busy
 */
public class ClusterControllerBusyException extends IOException {

    public ClusterControllerBusyException() {
    }

    public ClusterControllerBusyException(String message) {
        super(message);
    }

    public ClusterControllerBusyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClusterControllerBusyException(Throwable cause) {
        super(cause);
    }
}
