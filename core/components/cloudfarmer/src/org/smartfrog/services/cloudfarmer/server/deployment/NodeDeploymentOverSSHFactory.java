package org.smartfrog.services.cloudfarmer.server.deployment;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.services.ssh.AbstractSSHComponent;
import org.smartfrog.services.ssh.SSHComponent;
import org.smartfrog.services.ssh.ScpTo;
import org.smartfrog.services.ssh.SshCommand;
import org.smartfrog.services.ports.PortUtils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.OutputStreamLog;
import org.smartfrog.sfcore.utils.SFExpandFully;
import org.apache.tools.ant.util.TeeOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
    @Override
    public NodeDeploymentService createInstance(ClusterNode node) throws IOException, SmartFrogException {
        return new NodeDeploymentOverSSH(node);
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
     * SSH based deployment, assumes deploy-by-copy to a specified destdir, uses a given login
     * <p/>
     * This is an inner
     * class and it uses the parent to do the work
     */
    final class NodeDeploymentOverSSH extends AbstractNodeDeployment implements NodeDeploymentService {

        private String hostname;
        private static final String SF_SUFFIX = ".sf";
        private static final String SSH_RESPONSE_CHARSET = "ISO-8859-1";

        NodeDeploymentOverSSH(ClusterNode node) {
            super(node);
            hostname = node.getExternalHostname();
        }

        /**
         * This is here to stop any code even accidentally asking the superclass for a session
         * @return nothing
         * @throws IllegalStateException always
         */
        private Session getSession() {
            throw new IllegalStateException("getSession not supported");
        }

        private String makeSFCommand(String command) {
            if(sfHomeDir.isEmpty()) {
                return command;
            }
            return sfHomeDir + command;
        }
        /**
         * {@inheritDoc}
         * @param name application name
         * @param cd component description
         * @throws IOException IO problems
         * @throws SmartFrogException SF problems
         */
        @Override
        public synchronized void deployApplication(String name, ComponentDescription cd)
                throws IOException, SmartFrogException {
            if(!getClusterNode().isExternallyVisible()) {
                sfLog().warn("This node is not externally visible. Unless we are in the cell, deployment will not work");
            }
            File localtempfile = File.createTempFile(tempfilePrefix, SF_SUFFIX);
            String desttempfile = destDir + tempfilePrefix + getNextNumber() + SF_SUFFIX;
            SFExpandFully.saveCDtoFile(cd, localtempfile);
            ArrayList<File> sourceFiles = new ArrayList<File>();
            ArrayList<String> destFiles = new ArrayList<String>();
            sourceFiles.add(localtempfile);
            destFiles.add(desttempfile);
            String connectionDetails = getConnectionDetails();
            log.info("Deploying application "+ name + " to " + connectionDetails);
            
            //make a pre-emptive connection to the port; this blocks waiting for things like machines to come up
            
            PortUtils.checkPort(hostname, getPort(), portConnectTimeout);
            
            
            Session session = null;
            try {
                session = demandCreateSession(hostname);
                ArrayList<String> commandsList = new ArrayList<String>(1);
                commandsList.add("mkdir -p " + destDir);
                commandsList.add("exit");
                sshExec(session, commandsList, true);
                
                ScpTo scp = new ScpTo(sfLog());
                //copy up the temp files
                log.info("copying " + localtempfile + " to " + desttempfile);
                scp.doCopy(session, destFiles, sourceFiles);
                String sshCommand;
                sshCommand = makeSFCommand("sfStart") + " " + "localhost" + " " + name + " " + desttempfile;
                log.info("executing: " + sshCommand);
                commandsList = new ArrayList<String>(1);
                commandsList.add(sshCommand);
                if (!keepFiles) {
                    commandsList.add("rm " + desttempfile);
                }
                commandsList.add("exit");
                sshExec(session, commandsList, true);
            } catch (JSchException e) {
                log.error("Failed to upload to " + connectionDetails + " : " + e, e);
                throw forward(e, connectionDetails);
            } finally {
                endSession(session);
                if(!keepFiles) {
                    localtempfile.delete();
                }
            }
        }

        private String getConnectionDetails() {
            return getUserName() +"@" +hostname + ":" + getPort() ;
        }

        /**
         * Get a new number
         * @return a new number for use in filenames
         */
        private synchronized int getNextNumber() {
            return random.nextInt();
        }

        private void sshExec(Session session, String commandString, boolean checkResponse)
                throws JSchException, IOException, SmartFrogException {
            ArrayList<String> commandsList = new ArrayList<String>(1);
            commandsList.add(commandString);
            sshExec(session, commandsList, checkResponse);
        }

        private int sshExec(Session session, ArrayList<String> commandsList, boolean checkResponse)
                throws JSchException, IOException, SmartFrogException {
            SshCommand command = new SshCommand(sfLog(), null);
            OutputStreamLog outputStreamLog = new OutputStreamLog(log, outputLogLevel);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            TeeOutputStream teeOut = new TeeOutputStream(outputStreamLog, byteOutputStream);
            int exitCode = command.execute(session, commandsList, teeOut, getTimeout());
            if (checkResponse && exitCode != 0) {
                String output = byteOutputStream.toString(SSH_RESPONSE_CHARSET);
                throw new SmartFrogException("Error response on command " + commandsList.get(0)
                    +":-\n"
                    + output);
            }
            return exitCode;
        }

        /**
         * End a session
         *
         * @param session session, can be null
         */
        private void endSession(Session session) {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean terminateApplication(String name, boolean normal, String exitText)
                throws IOException, SmartFrogException {
            Session session = null;
            try {
                log.info("Terminating application " + name + " for " + exitText);
                session = demandCreateSession(hostname);
                ArrayList<String> commandsList = new ArrayList<String>(1);
                String sshCommand = makeSFCommand("sfTerminate") + " " + "localhost" + " " + name;
                log.info("executing: " + sshCommand);
                commandsList.add(sshCommand);
                commandsList.add("exit");
                return sshExec(session, commandsList, false) == 0;
            } catch (JSchException e) {
                String connectionDetails = getConnectionDetails();
                log.error("Failed to terminate "+ name+ " on " + connectionDetails + " : " + e, e);
                throw forward(e, connectionDetails);
            } finally {
                endSession(session);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void pingApplication(String name) throws IOException, SmartFrogException {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getServiceDescription() throws IOException, SmartFrogException {
            return "SSH to " + hostname;
        }

        @Override
        public String getApplicationDescription(String name) throws IOException, SmartFrogException {
            return "";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void terminate() throws IOException, SmartFrogException {
        }
    }

}
