/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * This is a class for wrapping up non-remotable state to the SmartFrog context. This can be added, remote
 * users get an error message, while local callers get the real reference.
 */

public class WrappedInstance<T> implements Serializable {

    /**
     * The instance data does not serialize and is marked as transient.
     */
    private transient T instance;

    /**
     * The toString value that is used in the {@link #toString()} operation.
     */
    private String stringValue;

    public WrappedInstance(T instance) {
        setInstance(instance);
    }

    public WrappedInstance() {
    }

    public T getInstance() {
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;

        stringValue = instance == null ? null : instance.toString();
    }

    @Override
    public String toString() {
        return "Wrapping of " + stringValue
                + (instance == null ? " instance not serialized" : "");
    }

    /**
     * Resolve an instance
     * @param source source prim
     * @param attribute reference string
     * @param mandatory is it mandatory
     * @return the reference, null if exists but ias
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException network problems
     */
    public T resolve(Prim source, String attribute, boolean mandatory)
            throws SmartFrogResolutionException, RemoteException {
        Reference r = new Reference(attribute);
        Object wrapper = source.sfResolve(r, mandatory);
        if (!(wrapper instanceof WrappedInstance)) {
            throw new SmartFrogResolutionException(r,
                    source.sfCompleteName(),
                    "Not a WrappedInstance: " + wrapper);
        }
        WrappedInstance<T> that = (WrappedInstance<T>) wrapper;
        T inst = that.getInstance();
        if (mandatory && inst == null) {
            throw new SmartFrogResolutionException(r,
                    source.sfCompleteName(),
                    "WrappedInstance value is null -this object is not remotely accessible: " + wrapper);
        }
        return inst;
    }
}
