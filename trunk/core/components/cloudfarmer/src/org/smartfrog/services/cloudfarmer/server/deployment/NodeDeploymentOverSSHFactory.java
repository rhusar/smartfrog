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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.LogSF;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Random;

/**
 * This factory copies applicatiions over then starts them.
 */
public class NodeDeploymentOverSSHFactory extends AbstractSSHComponent
        implements NodeDeploymentServiceFactory, SSHComponent, SshDefaults {

    private String destDir;
    private JSch jschInstance;
    public static final String ATTR_DEST_DIR = "destDir";
    public static final String ATTR_LOG_LEVEL = "logLevel";
    public static final String ATTR_TEMP_FILE_PREFIX = "tempfilePrefix";
    public static final String ATTR_KEEP_FILES = "keepFiles";
    public static final String ATTR_SF_HOME_DIR = "sf.home.dir";
    public static final String ATTR_PORT_CONNECT_TIMEOUT = "portConnectTimeout";

    public static final String ATTR_STARTUP_SLEEP_TIME = "startupSleepTime";
    public static final String ATTR_STARTUP_PING_SLEEP_TIME = "startupPingSleepTime";
    public static final String ATTR_SLEEP_TIME = "sleepTime";
    public static final String ATTR_STARTUP_LOCATE_ATTEMPTS = "startupLocateAttempts";
    public static final String ATTR_STARTUP_PING_ATTEMPTS = "startupPingAttempts";


    private int outputLogLevel;
    private String tempfilePrefix;
    private Random random = new Random();
    private int portConnectTimeout;
    private boolean keepFiles = false;
    private String sfHomeDir ;
    private int startupSleepTime = STARTUP_SLEEP_TIME;
    private int startupPingSleepTime = STARTUP_PING_SLEEP_TIME;
    private int sleepTime = SLEEP_TIME;
    private int startupLocateAttempts = STARTUP_LOCATE_ATTEMPTS;
    private int startupPingAttempts = STARTUP_PING_ATTEMPTS;


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

        //because the sleep operations take seconds (linux: floating point values) we downconvert from
        //milliseconds to seconds, rounding down the number to the second.
        startupSleepTime = resolveMillisToSeconds(ATTR_STARTUP_SLEEP_TIME);
        startupPingSleepTime = resolveMillisToSeconds(ATTR_STARTUP_PING_SLEEP_TIME);
        sleepTime = resolveMillisToSeconds(ATTR_SLEEP_TIME);
        startupLocateAttempts = sfResolve(ATTR_STARTUP_LOCATE_ATTEMPTS, 0, true);
        startupPingAttempts = sfResolve(ATTR_STARTUP_PING_ATTEMPTS, 0, true);
        
        try {
            jschInstance = createJschInstance();
        } catch (JSchException e) {
            throw forward(e);
        }
    }

    /**
     * Resolve an attribute containing milliseconds, return the value in seconds
     * @param attr attribute to resolve
     * @return the number rounded down to the second
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException network
     */
    private int resolveMillisToSeconds(String attr) throws SmartFrogResolutionException, RemoteException {
        float f = sfResolve(attr, 0f, true);
        int value = (int)(f/1000);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public boolean isNodeDeploymentSupported() throws IOException, SmartFrogException {
        return jschInstance != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException {
        NodeDeploymentOverSSH nodeDirectSSH = new NodeDeploymentOverSSH(this, node);
        return NodeDeploymentHelper.export(nodeDirectSSH);
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
            return getSfHomeDir() + "/bin/" + command;
        }
    }

    public int getStartupSleepTime() {
        return startupSleepTime;
    }

    public void setStartupSleepTime(int startupSleepTime) {
        this.startupSleepTime = startupSleepTime;
    }

    public int getStartupPingSleepTime() {
        return startupPingSleepTime;
    }

    public void setStartupPingSleepTime(int startupPingSleepTime) {
        this.startupPingSleepTime = startupPingSleepTime;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getStartupLocateAttempts() {
        return startupLocateAttempts;
    }

    public void setStartupLocateAttempts(int startupLocateAttempts) {
        this.startupLocateAttempts = startupLocateAttempts;
    }

    public int getStartupPingAttempts() {
        return startupPingAttempts;
    }

    public void setStartupPingAttempts(int startupPingAttempts) {
        this.startupPingAttempts = startupPingAttempts;
    }
}
