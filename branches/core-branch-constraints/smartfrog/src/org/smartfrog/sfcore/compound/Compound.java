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

package org.smartfrog.sfcore.compound;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.ChildMinder;
import org.smartfrog.sfcore.prim.Prim;


/**
 * Defines the compound component interface. A compound deploys component
 * descriptions, and maintains them as part of itself. This includes liveness,
 * termination.
 *
 */
public interface Compound extends Prim, ChildMinder {
    /**
     * An internal SmartFrog method.
     * It deploys a compiled component and makes it an attribute of the
     * parent compound. Also start heartbeating the deployed component
     * if the component registers. Note that the remaining lifecycle methods must
     * still be invoked on the created component - namely sfDeploy() and sfStart().
     * This is primarily an internal method - the preferred method for end users is
     * {@link #sfCreateNewChild}.
     *
     * @param cmp compiled component to deploy
     * @param parent of deployed component
     * @param name name of  attribute which the deployed component should adopt
     * @param parms parameters for description
     *
     * @return deployed component if successful
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfDeployComponentDescription(Object name, Prim parent,
        ComponentDescription cmp, Context parms)
        throws RemoteException, SmartFrogDeploymentException;

    /**
     * A high-level component deployment method - creates a child of this
     * Compound, running it through its entire startup lifecycle. This is the preferred way
     * of creating new child components of a Compound.  The method is safe against
     * multiple calls of lifecycle.
     *
     * @param name name of attribute which the deployed component should adopt
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successful
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewChild(Object name, ComponentDescription cmp, Context parms)
        throws RemoteException, SmartFrogDeploymentException;

    /**
     * A high-level component deployment method - creates a child of this
     * Compound, running it through its entire startup lifecycle. This is the preferred way
     * of creating new child components of a Compound.  The method is safe against
     * multiple calls of lifecycle.
     *

     * @param name name of attribute which the deployed component should adopt
     * @param parent of deployed component
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successful
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewChild(Object name, Prim parent, ComponentDescription cmp, Context parms)
        throws RemoteException, SmartFrogDeploymentException;

    /**
     * A high-level component deployment method - creates a new self-managing application,
     * running it through its entire startup lifecycle. This is the preferred way
     * of creating new applications.
     *
     * @param name name the deployed application should adopt
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successful
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewApp(String name, ComponentDescription cmp, Context parms)
        throws RemoteException, SmartFrogDeploymentException;
}
