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

package org.smartfrog.services.asyndeployer;

import java.io.IOException;

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.deployer.ComponentDeployer;
import org.smartfrog.sfcore.deployer.ComponentFactory;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.PrimDeployerImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompoundImpl;

import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.ProActive;


/**
 * This class implements the deployment semantics for primitives. This means
 * looking up the sfClass attribute and creating an instance of that class.
 * After this the rest of the deployment is left to the instance. The deployer
 * implements the ComponentDeployer interface.
 *
 */
public class AsynPrimDeployerImpl extends PrimDeployerImpl implements ComponentDeployer, MessageKeys {

    /**
     * Constructs a component deployer for given description.
     *
     * @param descr target description
     */
    public AsynPrimDeployerImpl (ComponentDescription descr) {
        super(descr);
    }

    public void setComponentFactory(ComponentFactory componentFactory) {
        // Discard the component factory from above and use ours.
        // Temporary fix: the real fix would be switching to a proper ComponentFactory,
        // declared by the sfMeta:sfFactory attribute.
        super.setComponentFactory(new ProActiveComponentFactory());
    }

    private class ProActiveComponentFactory implements ComponentFactory {

        /**
         *
         * @param askedFor
         * @return a new instance
         * @throws ProActiveException
         * @throws IOException
         */
        public Prim getComponent(ComponentDescription askedFor) throws SmartFrogDeploymentException {
            try {
                String primClassName = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);

                Prim dComponent;
                ProActiveDescriptor descriptorPad = ProActive.getProactiveDescriptor("RootNode.xml");

                descriptorPad.activateMappings();

                VirtualNode vnode = descriptorPad.getVirtualNode("RootNode");
                Node[] nodes = vnode.getNodes();

                dComponent = (Prim) ProActive.newActive(primClassName, null, nodes[0]);
                ProActive.register(dComponent, "RootProcessCompound");
                // Huh. Is this really meant to return a ProcessCompound instead of the component instance ?
                dComponent = (Prim) ProActive.lookupActive(ProcessCompoundImpl.class.getName(), "RootProcessCompound");

                return dComponent;

            } catch (Exception e) {
                throw new SmartFrogDeploymentException(e);
            }
        }
    }    
}
