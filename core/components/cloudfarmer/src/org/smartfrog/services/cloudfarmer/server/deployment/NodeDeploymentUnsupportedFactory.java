package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * this is used to mark node deployment as unsupported
 */
public class NodeDeploymentUnsupportedFactory extends PrimImpl implements NodeDeploymentServiceFactory {
    public static final String ERROR_UNSUPPORTED = "Unsupported Operation";

    public NodeDeploymentUnsupportedFactory() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException {
        throw new SmartFrogException(ERROR_UNSUPPORTED);
    }
}