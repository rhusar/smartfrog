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

package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.ComponentDeployer;
import org.smartfrog.sfcore.deployer.ComponentFactory;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Enumeration;


/**
 * This class implements the deployment semantics for primitives. This means
 * looking up the sfClass attribute and creating an instance of that class.
 * After this the rest of the deployment is left to the instance. The deployer
 * implements the ComponentDeployer interface.
 *
 */
public class PrimDeployerImpl implements ComponentDeployer, MessageKeys {

    /** The target description to work off. */
    protected ComponentDescription target;

    /** The factory used to get the component instance. */
    private ComponentFactory componentFactory;

    /**
     * Constructs a component deployer for given description.
     *
     * @param descr target description
     */
    public PrimDeployerImpl(ComponentDescription descr) {
        target = descr;
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    /**
     * Does the basic deployment. The instance created and the deployment
     * forwarded to the primitive. Subclasses can override this to provide
     * different deployment implementations.
     *
     * @param parent parent for deployed component
     *
     * @return Prim
     *
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     */
    protected Prim deploy(Prim parent) throws SmartFrogDeploymentException {
        Context cxt = null;

        try {
            // create instance
            Prim dComponent = componentFactory.getComponent(target);

            // deploy component after wiping out the parentage of any
            // descriptions in the context. Prim is not a valid parent, so
            // lose the parent baggage
            cxt = target.sfContext();

            for (Enumeration e = cxt.keys(); e.hasMoreElements();) {
                Object value = cxt.get(e.nextElement());

                if (value instanceof ComponentDescription) {
                    ((ComponentDescription) value).setParent(null);
                }
            }

            dComponent.sfDeployWith(parent, cxt);

            return dComponent;
        } catch (SmartFrogException sfdex){
            throw ((SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfdex));
        } catch (Throwable t) {
            throw new SmartFrogDeploymentException("Error when trying to deploy component", t, null, cxt);
        }
    }

    //
    // ComponentDeployer
    //

    /**
     * Deploy target description for which this deployer was created. This
     * implementation resolves the given name, forwarding if non-null. In case
     * of forwarding the resulting component is deploy resolved. If the name
     * is null the parameters are added to the description. The description is
     * NOT type, place and deploy since this is expected from higher level
     * functionality. Deployement happens via the internal deploy method
     *
     * @param name name of contained description to deploy (can be null)
     * @param parent parent for deployed component
     * @param params parameters for description
     *
     * @return Prim
     *
     * @exception SmartFrogDeploymentException failed to deploy description
     */
    public Prim deploy(Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException {
        // add parameters
        if (params != null) {
            for (Enumeration e = params.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                try {
                  target.sfReplaceAttribute(key, params.get(key));
                } catch (SmartFrogRuntimeException ex) {
                  throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(ex);
                }
            }
        }
        return deploy(parent);
    }
}
