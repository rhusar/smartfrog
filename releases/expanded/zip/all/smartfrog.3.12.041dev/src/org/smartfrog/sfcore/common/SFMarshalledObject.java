/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.MarshalledObject;

/**
 * A wrapper class to avoid stub classes in intermediate nodes.
 * We delay the packing of the inner object until we serialize the
 * wrapper to avoid replacing exported objects by stubs in a single VM.
 *
 *
 */
final public class SFMarshalledObject implements Serializable{

    /** The original object or a packed version of it. */
    private Object value = null;

    /** Whether we have already "packed" an inner object.*/
    private boolean alreadySet = false;

    /** Whether the original object was of type MarshalledOnject, so
        we do not need to unwrap it. */
    private boolean wasMarshalled = false;

    /**
     * Creates a new <code>SFMarshalledObject</code> instance.
     * @param value an <code>Object</code> value
     */
    public SFMarshalledObject(Object value) {
        this.value = value;
    }

    /**
     * Gets an unwrapped version of the original object, or exactly
     * the original object if we have not been serialized.
     *
     * @return An unwrapped version of the original object, or exactly
     * the original object if we have not been serialized.
     * @exception IOException if an <code>IOException</code> occurs while
     * deserializing the object from its internal representation.
     * @exception ClassNotFoundException if a
     * <code>ClassNotFoundException</code> occurs while deserializing the
     * object from its interna
     */
    public synchronized Object get() throws IOException, ClassNotFoundException {
        if ((!alreadySet) || (value == null)) {
            return value;
        }
        if (value instanceof MarshalledObject && !wasMarshalled) {
            return ((MarshalledObject)value).get();
        } else {
            return value;
        }
    }

    /**
     * Customize the serialization of this object so it "packs" the
     * inner object the first time we serialize the "wrapper" object.
     *
     * @param out a <code>java.io.ObjectOutputStream</code> value
     * @exception IOException if an error occurs
     */
    private void writeObject(ObjectOutputStream out)
        throws IOException {
        pack();
        out.defaultWriteObject();
    }

    /**
     * Packs the inner object in a MarshalledObject to avoid
     * stub classes being needed in intermediate nodes. We avoid
     * "packing" multiple times in case we serialize multiple times.
     * @exception IOException if an error occurs
     */
    private synchronized void pack() throws IOException {
        if (alreadySet) {
            return;
        }
        try {
            if (value instanceof MarshalledObject) {
                wasMarshalled = true;
            } else {
               value = new MarshalledObject(value);                
            }
        } finally {
            alreadySet = true;
        }
    }
}
