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
import java.util.HashMap;

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDeployer;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.PrimDeployerImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
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

    /** ProcessLog. This log is used to log into the core log: SF_CORE_LOG */
    private LogSF  sflog = LogFactory.sfGetProcessLog();

    /**
     * Constructs a component deployer for given description.
     *
     * @param descr target description
     */
    public AsynPrimDeployerImpl (ComponentDescription descr) {
        super (descr);
    }

    /**
     *
     * @param primClass
     * @return a new instance
     * @throws ProActiveException
     * @throws IOException
     */
     protected Prim createPrimInstance(Class primClass) throws Exception {
        Prim dComponent = null;
        ProActiveDescriptor descriptorPad = ProActive.getProactiveDescriptor("RootNode.xml");

        descriptorPad.activateMappings();

        VirtualNode vnode = descriptorPad.getVirtualNode("RootNode");
        Node[] nodes = vnode.getNodes();

        dComponent = (Prim) ProActive.newActive(primClass.getName(), null, nodes[0]);
        ProActive.register(dComponent, "RootProcessCompound");
        dComponent = (ProcessCompound) ProActive.lookupActive(ProcessCompoundImpl.class.getName(), "RootProcessCompound");
    //
        return dComponent;
    }

}
