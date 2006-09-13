package org.smartfrog.sfcore.common;

import java.io.Serializable;
import java.io.ObjectStreamException;

/**
 * Class implementing the notion of a NULL value in a component description.
 *
 * It is indicated by using the attribute definition with no value, such as in
 *     foo;
 *
 * There is exactly one instance of the NULL value, to ensure that object equality
 * works appropriately. This is obtained using the static get() method.
 *
 */

public final class SFNull implements Serializable {

    /** holder of the sole instance of the class */
    private static SFNull soleInstance = new SFNull();

    /** private constructor to ensure that no new instances can be built */
    private SFNull() {
    }

    /** obtain the sole instance of the SFNull class
     *
     * @return the instance
     */
    public static SFNull get() {
	return soleInstance;
    }

    /** Method to ensure that unmarshalling an instance during
     * e.g. an RMI call results in the sole instance being obtained
     *
     * @return the instance
     * @throws ObjectStreamException if failed
     */
    Object readResolve() throws ObjectStreamException {
	return soleInstance;
    }

    /** Print String for display
     *
     * @return the string "", which is the form required for the unparse
     */
    public String toString() {
	return "NULL";
    }
}
