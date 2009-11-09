package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 *
 */
public class NodeDeploymentOverRMIFactory extends PrimImpl implements NodeDeploymentServiceFactory {
    public NodeDeploymentOverRMIFactory() throws RemoteException {
    }

    /** {@inheritDoc} */
    @Override
    public NodeDeploymentService createInstance(String hostname) throws IOException, SmartFrogException {
        return new NodeDeploymentOverRMI(hostname, NodeDeploymentOverRMI.DEFAULT_PORT);
    }
}
