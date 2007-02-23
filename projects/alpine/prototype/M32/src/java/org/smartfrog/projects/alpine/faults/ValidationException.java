/**
 * Apache2.0 licensed code from Java Development with Ant, 2nd edition
 * i.e. Steve wrote it. 
 */
package org.smartfrog.projects.alpine.faults;

/**
 * Raised when validation fails
 */
public class ValidationException extends AlpineRuntimeException {


    /**
     * {@inheritDoc}
     * @param message message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * @param message message
     * @param cause underlying cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     * @param cause underlying cause
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

}
