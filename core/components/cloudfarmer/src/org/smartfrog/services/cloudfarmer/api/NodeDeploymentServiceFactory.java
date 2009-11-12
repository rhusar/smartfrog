package org.smartfrog.services.cloudfarmer.api;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.io.IOException;

/**
 *  This interface is primarily for SF components; its a factory
 * interface to provide host-specific deployment services
 */
public interface NodeDeploymentServiceFactory extends Remote {

    /**
     * Create a service that can deploy to this node
     * @param node the node to work with
     * @return a service interface
     * @throws IOException Network and other IO problems
     * @throws SmartFrogException SmartFrog problems
     */
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException;

}
