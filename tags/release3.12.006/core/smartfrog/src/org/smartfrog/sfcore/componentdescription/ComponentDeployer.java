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

package org.smartfrog.sfcore.componentdescription;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the access point to deploy a component description. The class
 * implementing this interface should have been parametrized with the
 * description it is to work on
 */
public interface ComponentDeployer {
    /**
     * Deploys description for which this deployer was created.
     *
     * @param name name of embedded description to deploy (can be null)
     * @param parent parent for deployed component
     * @param params parameters to add before deployment (can be null)
     *
     * @return deployer component
     *
     * @exception SmartFrogDeploymentException failed to deploy
     */
    public Prim deploy(Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException;
}
