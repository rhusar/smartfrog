package org.smartfrog.services.cloudfarmer.server.deployment;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.services.ssh.AbstractSSHComponent;
import org.smartfrog.services.ssh.SSHComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Random;

/**
 * This factory copies applicatiions over then starts them.
 */
public class NodeDeploymentOverSSHFactory extends AbstractSSHComponent
        implements NodeDeploymentServiceFactory, SSHComponent {

    private String destDir;
    private JSch jschInstance;
    public static final String ATTR_DEST_DIR = "destDir";
    public static final String ATTR_LOG_LEVEL = "logLevel";
    public static final String ATTR_TEMP_FILE_PREFIX = "tempfilePrefix";
    public static final String ATTR_KEEP_FILES = "keepFiles";
    public static final String ATTR_SF_HOME_DIR = "sf.home.dir";
    public static final String ATTR_PORT_CONNECT_TIMEOUT = "portConnectTimeout";
    
    private int outputLogLevel;
    private String tempfilePrefix;
    private Random random = new Random();
    private int portConnectTimeout;
    private boolean keepFiles = false;
    private String sfHomeDir ;


    public NodeDeploymentOverSSHFactory() throws RemoteException {
    }

    
    public String getDestDir() {
        return destDir;
    }

    public JSch getJschInstance() {
        return jschInstance;
    }

    public String getSfHomeDir() {
        return sfHomeDir;
    }

    public boolean isKeepFiles() {
        return keepFiles;
    }

    public int getPortConnectTimeout() {
        return portConnectTimeout;
    }

    public String getTempfilePrefix() {
        return tempfilePrefix;
    }

    public int getOutputLogLevel() {
        return outputLogLevel;
    }
    
    public LogSF getLog() {
        return log;
    }

    /**
     * Get a new number
     *
     * @return a new number for use in filenames
     */
    public synchronized int getNextNumber() {
        return random.nextInt();
    }    
    
    /**
     * {@inheritDoc}
     *
     */
    public SmartFrogLifecycleException forward(Throwable thrown, String connectionDetails) {
        return super.forward(thrown, connectionDetails);
    }

    /**
    * At startup, the jsch instance is created
    *
    * @throws SmartFrogException problems -can include a nested JSchException
    * @throws RemoteException    network problems
    */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        destDir = sfResolve(ATTR_DEST_DIR, "", true);
        if (!destDir.endsWith("/")) {
            destDir += "/";
        }
        tempfilePrefix = sfResolve(ATTR_TEMP_FILE_PREFIX, "", true);
        outputLogLevel = sfResolve(ATTR_LOG_LEVEL, LogLevel.LOG_LEVEL_INFO, true);
        keepFiles = sfResolve(ATTR_KEEP_FILES, keepFiles, true);
        sfHomeDir = sfResolve(ATTR_SF_HOME_DIR, "", true);
        portConnectTimeout = sfResolve(ATTR_PORT_CONNECT_TIMEOUT, portConnectTimeout, true);
        try {
            jschInstance = createJschInstance();
        } catch (JSchException e) {
            throw forward(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException {
        return "NodeDeployment over SSH to " + getConnectionDetails() 
                + "; Destdir: " + destDir
                + "; sfHomeDir: " + sfHomeDir
                ;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException {
        return new NodeDeploymentOverSSH(this, node);
    }


    /**
     * Create a session instance if one is needed
     *
     * @param host target host
     * @return a session
     * @throws JSchException connection problems
     */
    public synchronized Session demandCreateSession(String host) throws JSchException {
        Session newSession = createSession(jschInstance, host, getPort());
        newSession.setTimeout(getTimeout());
        newSession.connect(getConnectTimeout());
        return newSession;
    }

    /**
     * Create an SF command resolved to the target SF home directory
     * @param command the command to be run
     * @return the command to run at the far end
     */
    public String makeSFCommand(String command) {
        if (getSfHomeDir().isEmpty()) {
            return command;
        } else {
            return getSfHomeDir() + command;
        }
    }

}
