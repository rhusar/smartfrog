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

import java.io.Serializable;

import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;


/**
 * Implements the commn reference part. A reference part is relative to the
 * context the reference is sitting in.  Programs can use the static creation
 * methods (eg. ReferencePart.root()) to create the common referenceparts.
 *
 */
public abstract class ReferencePart implements Copying, Cloneable, Serializable {
    
	/**Resolving History*/
	public static boolean maintainResolutionHistory=false;
	public static ReferenceResolver resolutionParentDescription=null;
	/***/
	
	public ReferencePart() {
    }

    /**
     * Creates a reference part to the current component.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart here(Object s) {
        return new HereReferencePart(s);
    }

    /**
     * Creates a reference part to the parent component.
     *
     * @return reference part
     */
    public static ReferencePart parent() {
        return new ParentReferencePart();
    }

    /**
     * Creates a reference part to the root component.
     *
     * @return reference part
     */
    public static ReferencePart root() {
        return new RootReferencePart();
    }

    /**
     * Creates a reference part to a attribute of a containing component.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart attrib(Object s) {
        return new AttribReferencePart(s);
    }

    /**
     * Creates a reference part to the containing component.
     *
     * @return reference part
     */
    public static ReferencePart thisref() {
        return new ThisReferencePart();
    }


    /**
     * Creates a property part which resolves to a system property.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart property(Object s) {
        return new PropertyReferencePart(s);
    }

    /**
     * Creates an integer property part which resolves to a system environment property.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart ienvproperty(Object s) {
        return new IEnvPropertyReferencePart(s);
    }
    /**
     * Creates a property part which resolves to a system environment property.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart envproperty(Object s) {
        return new EnvPropertyReferencePart(s);
    }

    /**
     * Creates an integer property part which resolves to a system property.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart iproperty(Object s) {
        return new IPropertyReferencePart(s);
    }

   /**
     * Creates an const reference part which resolves to a constant.
     *
     * @param s id of attribute
     *
     * @return reference part
     */
    public static ReferencePart constant(String s) {
        return new ConstantReferencePart(s);
    }

    /**
     * Creates a reference part which resolves to a host's root process
     * compound component.
     *
     * @param s id of host
     *
     * @return reference part
     */
    public static ReferencePart host(String s) {
        return new HostReferencePart(s);
    }

    /**
     * Creates a reference part which resolves to the process compound
     * component.
     *
     * @return reference part
     */
    public static ReferencePart process() {
        return new ProcessReferencePart();
    }


    /**
     * Returns a string representation of the reference part.
     * Overrides Object.toString.
     * 
     * @return stringified reference part
     */
    public abstract String toString();

    /**
     * Returns a copy of the reference part. The result of clone is returned.
     * Subclasses that hold more than immutable objects should override this
     * to make a deep copy of those.
     *
     * @return copy of part
     */
    public Object copy() {
        return clone();
    }

    /**
     * Returns a clone of this reference part or null if not cloneable.
     * Overrides Object.clone.
     *
     * @return clone of this reference part
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cex) {
            // should not happen
        }

        return null;
    }


    /**
     * Return a string which is a representation of the reference part in a reference in the 
     * index position given. It defaults to calling the "toString()" method, but may be overridden
     * in a sub-class. An index of 0 indicate the fist part of a reference.
     *
     * @param index the position in the reference
     * @return the representation
     */
    public String toString(int index) {
	return toString();
    }


    /**
     * Compares this reference part with another one.
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public abstract boolean equals(Object refPart);

    /**
     * ReferenceParts need to implement hashCode.
     * Overrides Object.hashCode.
     *
     * @return hash code for part
     */
    public abstract int hashCode();

    /**
     * Resolves this reference part using the reference resolver. The
     * originating reference and index are needed to enable request forwarding
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    public abstract Object resolve(ReferenceResolver rr, Reference r, int index)
        throws SmartFrogResolutionException;

    /**
     * Resolves this reference part using the remote reference resolver. The
     * originating reference and index are needed to enable request forwarding
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    public abstract Object resolve(RemoteReferenceResolver rr, Reference r,
        int index) throws SmartFrogResolutionException;

    /**
     * Used internally to forward references once this reference part has been
     * resolved. Currently only ReferenceResolver and RemoteReferenceResolver
     * interfaces are recognized
     *
     * @param rr reference resolver to forward to
     * @param r reference to resolve
     * @param index index into reference to resolve next
     *
     * @return Object
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    protected Object forwardReference(Object rr, Reference r, int index)
        throws SmartFrogResolutionException {
    	
    	if (maintainResolutionHistory){
    		resolutionParentDescription= (ReferenceResolver) rr;
    	}
    	
    	
        if (index >= r.size()) {
            return rr;
        } else if (rr instanceof ReferenceResolver) {
            return ((ReferenceResolver) rr).sfResolve(r, index);
        } else if (rr instanceof RemoteReferenceResolver) {
            try {
                return ((RemoteReferenceResolver) rr).sfResolve(r, index);
            } catch (Exception ex){
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(ex);
            }
        } else {
            throw SmartFrogResolutionException.notComponent(r, null);
        }
    }
}
