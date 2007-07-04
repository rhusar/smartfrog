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

package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;


/**
 * Access point to the deployer infrastructure. At this point,
 * it simply uses either the default deployer or the sfDeployerClass provided
 * as part of the component description that is to be deployed.
 */
public class SFDeployer implements MessageKeys {

    private static ComponentDeployer defaultComponentDeployer = new PrimProcessDeployerImpl();
    private static PrimFactory defaultPrimFactory = new OldAlgorithmPrimFactory();

    /**
     * Deploy description. Constructs the real deployer using getDeployer
     * method and forwards to it. If name is set, name is resolved on target,
     * the new target deploy resolved and deployment forwarded to the new
     * target
     *
     * @param component the description of the component to be deployed
     * @param name name of contained description to deploy (can be null)
     * @param parent parent for deployed component
     * @param params parameters for description
     *
     * @return Reference to component
     *
     * @throws SmartFrogDeploymentException In case failed to forward deployment
     * or deploy
     */
    public static Prim deploy(ComponentDescription component, Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException {
        try {
            // resolve name to description and deploy from there
            if (name != null) {
                Object tmp = component.sfResolve(name);

                if (!(tmp instanceof ComponentDescription))
                    SmartFrogResolutionException.notComponent(name, component.sfCompleteName());

                return deploy((ComponentDescription) tmp, null, parent, params);
            }

            return getDeployer(component).deploy(name, parent, params);

        } catch (SmartFrogException sfex){
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(sfex);
        }
    }

    /**
     * Gets the real deployer for this description target. Looks up
     * sfDeployerClass. If not found. PrimProcessDeployerImpl is used. The
     * constructor used is the one taking a compnent description as an
     * argument
     *
     * @param component the component description to mine for the deployer information
     * @return deployer for target
     *
     * @throws SmartFrogException failed to construct target deployer
     * @see org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl
     */
    private static ComponentDeployer getDeployer(ComponentDescription component)
            throws SmartFrogResolutionException
    {

        ComponentDeployer deployer;
        try {
            Reference deployerRef = (Reference) component.sfResolveHere(SmartFrogCoreKeys.SF_FACTORY);
            deployer = (ComponentDeployer) component.sfResolve(deployerRef);
        } catch (SmartFrogResolutionException e) {
            LogFactory.sfGetProcessLog().ignore(e);
            deployer = defaultDeployer();
        }

        deployer.setTargetComponentDescription(component);
        deployer.setComponentFactory(getComponentFactory(component));
        return deployer;
    }

    private static PrimFactory getComponentFactory(ComponentDescription component)
            throws SmartFrogResolutionException
    {
        ComponentDescription metadata = null;
        try {
            metadata = (ComponentDescription) component.sfResolveHere(SmartFrogCoreKeys.SF_METADATA);
        } catch (SmartFrogResolutionException e) {
            LogFactory.sfGetProcessLog().ignore(e);
        }

        if (metadata != null) {
            // Component using the new sfMeta syntax.
            // The sfFactory attribute needs to be resolved in those two steps because sfMeta is declared as such:
            // sfMeta extends DATA { ... sfFactory LAZY xxx; }
            Reference factoryRef = (Reference) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FACTORY);
            return (PrimFactory) metadata.sfResolve(factoryRef);
        } else {
            // Component using the old sfClass-only syntax
            return defaultFactory();
        }
    }

    private static PrimFactory defaultFactory() {
        return defaultPrimFactory;
    }

    private static ComponentDeployer defaultDeployer() {
        return defaultComponentDeployer;
    }
}
