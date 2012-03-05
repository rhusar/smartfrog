package org.smartfrog.services.hadoop.bluemine.jobs

class MainArgException extends Exception {

    int exitCode = -1;

    MainArgException(final String message) {
        super(message)
    }

    MainArgException(final int exitCode, final String message) {
        super(message)
        this.exitCode = exitCode
    }


}
