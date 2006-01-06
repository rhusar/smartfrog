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

import java.net.InetAddress;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * Implements the host reference part.
 *
 */
public class HostReferencePart extends ReferencePart {
    /** String representation of this part. */
    public static final String HOST = "HOST";

    /** Hostname for the HOST reference part. */
    public String hostname;

    /**
     * Constructs HostReferencePart for some host.
     *
     * @param hostname hostname for the process compound referenced
     */
    public HostReferencePart(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Returns this object since this is already a host reference part.
     *
     * @return this part
     */
    public ReferencePart asHostReferencePart() {
        return this;
    }
    /**
     * Returns InetAddress of the host.
     *
     * @return InetAddress of the host
     *
     * @throws Exception failed to find the inetAddress
     */
    protected InetAddress getAddress() throws Exception {
        return InetAddress.getByName(hostname);
    }

    /**
     * Returns a string representation of the reference part.
     * Implements abstract method ReferencePart.toString.
     * @return stringified reference part 
     */
    public String toString() {
        if (hostname.charAt(0)>='0'&&(hostname.charAt(0)<='9')){
          return HOST + " " + '"'+hostname+ '"';
        } else {
          return HOST + " " + hostname;
        }
    }

    /**
     * Compares this reference part with another one. Equality means that the
     * other refPart is a HostReferencePart and the hostnames are string
     * equal.
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object refPart) {
        boolean isEqual = false;

        try {
            isEqual = hostname.equals(((HostReferencePart) refPart).hostname);
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
        return hostname.hashCode();
    }

    /**
     * Resolves this reference part using the given reference resolver. This
     * forwards the reference to the parent if it exists or to the actual
     * component if the root is found (parent== null). The originating
     * reference and index are needed to forward the request to the parent or
     * component.
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
        ProcessCompound pc = null;

        try {
            pc = SFProcess.getRootLocator()
                                  .getRootProcessCompound(getAddress());
        } catch (Exception e) {
            //TODO: Check
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(e);
        }

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
     * component.
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
        ProcessCompound pc = null;

        try {
            pc = SFProcess.getRootLocator()
                            .getRootProcessCompound(getAddress());
        } catch (Exception e) {
            //TODO: Check
            throw new SmartFrogResolutionException(e);
        }

        if (pc != null) {
            return forwardReference(pc, r, index + 1);
        } else {
            throw SmartFrogResolutionException.notComponent(r, null);
        }
    }
}
