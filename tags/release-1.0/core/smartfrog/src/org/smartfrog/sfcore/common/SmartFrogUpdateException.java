package org.smartfrog.sfcore.common;

public class SmartFrogUpdateException extends SmartFrogException {

    public SmartFrogUpdateException( String message, Exception cause) {
        super(message, cause);
    }

        public SmartFrogUpdateException( String message) {
        super(message);
    }

}
