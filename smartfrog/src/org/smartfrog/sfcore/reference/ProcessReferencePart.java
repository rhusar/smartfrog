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

package org.smartfrog.sfcore.reference;


import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * Implements the process reference part.
 *
 */
public class ProcessReferencePart extends ReferencePart {
    /** String representation of this part. */
    public static final String PROCESS = "PROCESS";

    /**
     * Constructs a process reference part.
     */
    public ProcessReferencePart() {
    }

    /**
     * Returns this object since this is already a process reference part.
     *
     * @return this part
     */
    public ReferencePart asProcessReferencePart() {
        return this;
    }

    /**
     * Returns a string representation of the reference part.
     * Implements abstract method ReferencePart.toString.
     * @return stringified reference part
     */
    public String toString() {
        return PROCESS;
    }

    /**
     * Compares this reference part with another one. Equality means that the
     * other refPart is a ProcessReferencePart.
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object refPart) {
        boolean isEqual = false;

        try {
            isEqual = refPart instanceof ProcessReferencePart;
        } catch (Exception e) {
        }

        return isEqual;
    }

    /**
     * Returns hashcode of this part.
     * Implements abstract method ReferencePart.hashCode.
     *
     * @return hash code for part, the hashcode of the hostname
     */
    public int hashCode() {
        return PROCESS.hashCode();
    }

    /**
     * Resolves this reference part using the given reference resolver. This
     * forwards the reference to the parent if it exists or to the actual
     * component if the root is found (parent== null). The originating
     * reference and index are needed to forward the request to the parent or
     * component
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    public Object resolve(ReferenceResolver rr, Reference r, int index)
        throws SmartFrogResolutionException {
        ProcessCompound pc = SFProcess.getProcessCompound();

        if (pc != null) {
            return forwardReference(pc, r, index + 1);
        } else {
            throw SmartFrogResolutionException.notComponent(r, null);
        }
    }

    /**
     * Resolves this reference part using the given remote reference resolver.
     * This forwards the reference to the parent if it exists or to the actual
     * component if the root is found (parent== null). The originating
     * reference and index are needed to forward the request to the parent or
     * component
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    public Object resolve(RemoteReferenceResolver rr, Reference r, int index)
        throws SmartFrogResolutionException {
        ProcessCompound pc = SFProcess.getProcessCompound();

        if (pc != null) {
            return forwardReference(pc, r, index + 1);
        } else {
            throw SmartFrogResolutionException.notComponent(r, null);
        }
    }
}
