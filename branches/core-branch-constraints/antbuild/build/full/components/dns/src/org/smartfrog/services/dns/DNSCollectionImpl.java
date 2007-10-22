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

package org.smartfrog.services.dns;

import java.util.Iterator;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.Context;





/**
 * Implements a "collection"  of components, of possibly different types, that 
 * we just want to propagate deployment or actions over them transparently.
 * 
 * 
 */
public class DNSCollectionImpl extends DNSComponentImpl {


    public DNSCollectionImpl() {
        // real configuration in sfDeployWith
    }

    /**
     * The "real" constructor of this component. It will recursively
     * try to instantiate and configure  all the components passed in
     * its context.
     *
     * @param parent A parent in the description hierarchy.
     * @param ctx A context from which to obtain arguments to configure
     * this object.
     * @exception SmartFrogException if an error occurs while initializing
     * this component or any of its sub-components.
     */
    public void sfDeployWith(ComponentDescription parent, Context ctx)
        throws SmartFrogDeploymentException {

        super.sfDeployWith(parent, ctx);
  
        if (!isCollection(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " Collection type " + ctx);
        }

        for (Iterator iter = ctx.sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof ComponentDescription) {
                ComponentDescription cd = (ComponentDescription) obj;
                if (isComponent(cd.sfContext())) {
                    DNSComponent comp = (DNSComponent) deployComponent(this, cd);
                    comp.setParent(this);
                    Object key = ctx.sfAttributeKeyFor(cd);
                    // avoid exception of sfReplaceAttrib
                    sfContext().put(key, comp);
                }
            }
        }
    }
 
    /**
     * Replaces the equivalent component in a hierarchy defined
     * by the given root by this component. 
     *
     * @param root A top level component of the hierarchy that we
     * want to modify.
     * @exception DNSModifierException if an error occurs while 
     * modifying the hierarchy.
     */
    public void replace(DNSComponent root)
        throws DNSModifierException {
      
        for (Iterator iter = sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof DNSComponent) {
                DNSComponent comp = (DNSComponent) obj;
                comp.replace(root);
            }
        }
    }
 
}
