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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;





/**
 * Implements a "opaque" option that we don't know how to interpret but
 * just passes to the DNS options directly.
 *
 * 
 * 
 */
public class DNSOpaqueImpl extends DNSComponentImpl {

    /** A vector of strings to be appended to the options section.*/
    Vector opaque = null; 

    public DNSOpaqueImpl() {
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
  
        if (!isOpaque(ctx)) {
            throw new SmartFrogDeploymentException("DNS context is not of"
                                                   + " Opaque type " + ctx);
        }

        opaque = new Vector();
        
        for (Iterator iter = ctx.sfValues(); iter.hasNext(); ) {
            Object obj = iter.next();
            if (obj instanceof Vector) {
                Vector vec = (Vector) obj;
                opaque.addAll(vec);
            }
        }
    }
       
    /**
     * Writes the named.conf contents to the print writer. It propagates 
     * to its sub-components.
     *
     * @param out A print writer to dump the named.conf that bind needs.
     */
    public void printNamedConfig(PrintWriter out) {

        for (Iterator iter = opaque.iterator(); iter.hasNext(); ) {
            out.println(iter.next());
        }
    }

}
