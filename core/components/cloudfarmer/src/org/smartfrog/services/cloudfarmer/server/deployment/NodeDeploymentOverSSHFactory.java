package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.services.ssh.SSHComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This factory copies applicatiions over then starts them.
 */
public class NodeDeploymentOverSSHFactory extends PrimImpl implements NodeDeploymentServiceFactory, SSHComponent {
    private String hostname;
    private String username;
    private String password;
    private File privateKey;
    private String destDir;

    public NodeDeploymentOverSSHFactory() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDeploymentService createInstance(String hostname) throws IOException, SmartFrogException {
        return new NodeDeploymentOverSSH(hostname, username, password, privateKey, destDir);
    }
}