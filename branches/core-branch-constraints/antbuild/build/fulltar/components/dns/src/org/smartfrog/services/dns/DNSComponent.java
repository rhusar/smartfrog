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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;
import java.io.PrintWriter;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;






/**
 * A sub-class of component description that adds specific dns behaviour
 * for parameter checking, linking,  and configuration output.
 * 
 * 
 */
public interface DNSComponent extends ComponentDescription {

    /** An attribute representing a default view. */
    public static final String DEFAULT_VIEW = "default";

    /** An attribute representing a default zone. */
    public static final String DEFAULT_ZONE = "default";

    /** An attribute representing a default reverse zone. */
    public static final String DEFAULT_REVERSE_ZONE = "defaultReverse";

    /** A port number used for contacting the name server.*/
    public static final int DEFAULT_PORT = 53;


    // state enum indicator for this component.

    /** The component is in not init state. */
    public static final int NOT_INIT = -1;

    /** The component is in normal state. */
    public static final int NORMAL = 0;

    /** The component is scheduled to be deleted. */
    public static final int TO_DELETE = 1;

    /** The component is scheduled to be added. */   
    public static final int TO_ADD = 2;

    /** The component has been deleted. */   
    public static final int DELETED = 3;

    /**
     * The "real" constructor of this component. It will recursively
     * try to instantiate and configure  all the components passed in
     * its context.
     *
     * @param parent A parent in the description hierarchy.
     * @param cxt A context from which to obtain arguments to configure
     * this object.
     * @exception SmartFrogException if an error occurs while initializing
     * this component or any of its sub-components.
     */
    public void sfDeployWith(ComponentDescription parent, Context cxt)
        throws SmartFrogDeploymentException;


    /**
     * Writes the named.conf contents to the print writer. It propagates 
     * to its sub-components.
     *
     * @param out A print writer to dump the named.conf that bind needs.
     */
    public void printNamedConfig(PrintWriter out);


    /**
     * A name unique for the enclosing component that identifies this 
     * object.
     *
     * @return A name unique for the enclosing component that identifies this 
     * object.
     */
    public String getName();


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
        throws DNSModifierException;


    
    /**
     * Detaches itself from the parent and sets state to deleted.
     *
     */
    public void delete();

    /**
     * Gets the current state of the component.
     *
     * @return  The current state of the component.
     */
    public int getState();


    /**
     * Sets the current state of the component.
     *
     * @param newState The new state of the component.
     * @return The old state of the component.
     */
    public int setState(int newState);
}
