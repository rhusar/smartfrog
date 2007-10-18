package org.smartfrog.sfcore.common;

import java.io.Serializable;
import java.io.ObjectStreamException;

/**
 * Class implementing the notion of a temporary value in a component description.
 *
 * It is created by an ASSERTION that is staticly checked only in response to
 * a correct validation.
 *
 * All attributes whose values are a temporary value are removed during the method
 * sfAsComponentDescription from the Phases interface.
 *
 * There is exactly one instance of the temporary value, to ensure that object equality
 * works appropriately. This is obtained using the static get() method.
 *
 */

public final class SFTempValue implements Serializable {

    /** holder of the sole instance of the class */
    private static SFTempValue soleInstance = new SFTempValue();

    /** private constructor to ensure that no new instances can be built */
    private SFTempValue() {
    }

    /** obtain the sole instance of the SFNull class
     *
     * @return the instance
     */
    public static SFTempValue get() {
	return soleInstance;
    }

    /** Method to ensure that unmarshalling an instance during
     * e.g. an RMI call results in the sole instance being obtained
     *
     * @return the instance
     * @throws java.io.ObjectStreamException if failed
     */
    Object readResolve() throws ObjectStreamException {
	return soleInstance;
    }

    /** Print String for display
     *
     * @return the string "", which is the form required for the unparse
     */
    public String toString() {
	return "SFTempValue";
    }
}
