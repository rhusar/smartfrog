package org.smartfrog.services.cloudfarmer.server.deployment;

import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.io.IOException;

/**
 * SSH based deployment, assumes deploy-by-copy to a specified destdir, uses a given login
 */
public class NodeDeploymentOverSSH implements NodeDeploymentService {

    private String hostname;
    private String username;
    private String password;
    private File privateKey;
    private String deployDir;


    /**
     * Create an instance
     * @param hostname target host
     * @param username target user
     * @param password password to use
     * @param privateKey private key to use
     * @param deployDir destination deploy directory
     */
    public NodeDeploymentOverSSH(String hostname, String username, String password, File privateKey, String deployDir) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.deployDir = deployDir;
    }


    @Override
    public void deployApplication(String name, ComponentDescription cd) throws IOException, SmartFrogException {
    }

    @Override
    public boolean terminateApplication(String name, boolean normal, String exitText)
            throws IOException, SmartFrogException {
        return false;
    }

    @Override
    public void pingApplication(String name) throws IOException, SmartFrogException {
    }

    @Override
    public String getServiceDescription() throws IOException, SmartFrogException {
        return "SSH to "+hostname+" as "+username;
    }

    @Override
    public String getApplicationDescription(String name) throws IOException, SmartFrogException {
        return "";
    }

    @Override
    public void terminate() throws IOException, SmartFrogException {
    }
}



