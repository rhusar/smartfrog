package org.smartfrog.services.cloudfarmer.api;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogRemote;

import java.io.IOException;
import java.rmi.Remote;

/**
 * interface for thing that can deploy or terminate applications on a remote machine
 */
public interface NodeDeploymentService extends Remote {

    /**
     * Deploy an application
     *
     * @param name application name
     * @param cd component description
     * @param remoteLog a remote log, can be null
     * @return output, which can be logged
     * @throws IOException network and IO problems
     * @throws SmartFrogException other problems
     */
    public String deployApplication(String name, ComponentDescription cd, LogRemote remoteLog)
            throws IOException, SmartFrogException;

    /**
     * Terminate a named application
     *
     * @param name application name
     * @param normal flag for normal exit; false for abnormal
     * @param exitText exit text
     * @return true if the app was found and termination initiated
     * @throws IOException network and IO problems
     * @throws SmartFrogException other problems
     */
    public boolean terminateApplication(String name, boolean normal, String exitText)
            throws IOException, SmartFrogException;

    /**
     * Ping a named application
     *
     * @param name application name
     * @throws IOException network and IO problems
     * @throws SmartFrogException other problems
     */

    public void pingApplication(String name) throws IOException, SmartFrogException;

    /**
     * Get the description of the service -implementation, URL, etc.
     *
     * @return a description string
     * @throws IOException network and IO problems
     * @throws SmartFrogException other problems
     */
    public String getServiceDescription() throws IOException, SmartFrogException;

    /**
     * Get an application description -this could be an attribute or some diagnostics
     *
     * @param name application name
     * @return a string description
     * @throws IOException network and IO problems
     * @throws SmartFrogException other problems
     */
    public String getApplicationDescription(String name) throws IOException, SmartFrogException;

    /**
     * Stop work; don't expect to be called again. Try and make this idempotent
     * @throws IOException network and IO problems
     * @throws SmartFrogException other problems
     */
    public void terminate() throws IOException, SmartFrogException;


    /**
     * Get the cluster node information
     * @return the cluster node this deployment is bonded to
     * @throws IOException for trouble
     */
    ClusterNode getClusterNode() throws IOException;

}
