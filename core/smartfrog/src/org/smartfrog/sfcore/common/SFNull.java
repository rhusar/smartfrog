/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.sfcore.common;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Class implementing the notion of a NULL value in a component description.
 * <p/>
 * It is indicated by using the attribute definition with no value, such as in
 * foo;
 * <p/>
 * There is exactly one instance of the NULL value, to ensure that object
 * equality works appropriately. This is obtained using the static get()
 * method.
 */

public final class SFNull implements Serializable {

    /**
     * holder of the sole instance of the class
     */
    private static SFNull soleInstance = new SFNull();

    /**
     * private constructor to ensure that no new instances can be built
     */
    private SFNull() {
    }

    /**
     * obtain the sole instance of the SFNull class
     *
     * @return the instance
     */
    public static SFNull get() {
        return soleInstance;
    }

    /**
     * Method to ensure that unmarshalling an instance during e.g. an RMI call
     * results in the sole instance being obtained
     *
     * @return the instance
     *
     * @throws ObjectStreamException if failed
     */
    Object readResolve() throws ObjectStreamException {
        return soleInstance;
    }

    /**
     * Print String for display
     *
     * @return the string "", which is the form required for the unparse
     */
    public String toString() {
        return "NULL";
    }
}
