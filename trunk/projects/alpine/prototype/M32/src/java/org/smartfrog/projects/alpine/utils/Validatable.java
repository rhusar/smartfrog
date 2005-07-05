/**
 * Apache2.0 licensed code from Java Development with Ant, 2nd edition
 */
package org.smartfrog.projects.alpine.utils;


/**
 * An interface to aid in validating classes.
 */
public interface Validatable {


    /**
     * validate an instance.
     * Return if the object is valid, thrown an exception if not.
     * It is imperative that this call has <i>No side effects</i>.
     *
     * @return true unless an exception is thrown
     * @throws ValidationException with text if not valid
     */
    public boolean validate() throws ValidationException;
}
