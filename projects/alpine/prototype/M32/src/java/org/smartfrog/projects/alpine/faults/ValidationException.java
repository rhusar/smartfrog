/**
 * Apache2.0 licensed code from Java Development with Ant, 2nd edition
 */
package org.smartfrog.projects.alpine.faults;

/**
 * Raised when validation fails
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

}
