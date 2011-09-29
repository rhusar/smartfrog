package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Deploy over RMI
 */
public class NodeDeploymentOverRMIFactory extends PrimImpl implements NodeDeploymentServiceFactory {
    
    public NodeDeploymentOverRMIFactory() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException {
        return NodeDeploymentHelper.export(new NodeDeploymentOverRMI(node, NodeDeploymentOverRMI.DEFAULT_PORT));
    }

    /**
     * {@inheritDoc}
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException {
        return "NodeDeploymentOverRMI";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNodeDeploymentSupported() throws IOException, SmartFrogException {
        return true;
    }

}
