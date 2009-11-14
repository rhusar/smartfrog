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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.OutputStreamLog;
import org.smartfrog.sfcore.utils.SFExpandFully;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This factory copies applicatiions over then starts them.
 */
public class NodeDeploymentOverSSHFactory extends AbstractSSHComponent
        implements NodeDeploymentServiceFactory, SSHComponent {

    private String destDir;
    protected JSch jschInstance;
    public static final String ATTR_DEST_DIR = "destDir";
    public static final String ATTR_LOG_LEVEL = "logLevel";
    public static final String ATTR_TEMP_FILE_PREFIX = "tempfilePrefix";
    public static final String ATTR_KEEP_FILES = "keepFiles";
    protected int outputLogLevel;
    protected int counter = 0;
    protected String tempfilePrefix;

    protected boolean keepFiles = false;


    public NodeDeploymentOverSSHFactory() throws RemoteException {
    }


    /**
    * At startup, the jsch instance is created
    *
    * @throws SmartFrogException problems -can include a nested JSchException
    * @throws RemoteException    network problems
    */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        destDir = sfResolve(ATTR_DEST_DIR, "", true);
        if (!destDir.endsWith("/")) {
            destDir += "/";
        }
        tempfilePrefix = sfResolve(ATTR_TEMP_FILE_PREFIX, "", true);
        outputLogLevel = sfResolve(ATTR_LOG_LEVEL, LogLevel.LOG_LEVEL_INFO, true);
        keepFiles = sfResolve(ATTR_KEEP_FILES, keepFiles, true);
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

        NodeDeploymentOverSSH(ClusterNode node) {
            super(node);
            hostname = node.getHostname();
        }

        /**
         * This is here to stop any code even accidentally asking the superclass for a session
         * @return nothing
         * @throws IllegalStateException always
         */
        private Session getSession() {
            throw new IllegalStateException("getSession not supported");
        }

        @Override
        public synchronized void deployApplication(String name, ComponentDescription cd)
                throws IOException, SmartFrogException {

            File localtempfile = File.createTempFile(tempfilePrefix, SF_SUFFIX);
            String desttempfile = destDir + tempfilePrefix + incCounter() + SF_SUFFIX;
            SFExpandFully.saveCDtoFile(cd, localtempfile);
            ArrayList<File> sourceFiles = new ArrayList<File>();
            ArrayList<String> destFiles = new ArrayList<String>();
            sourceFiles.add(localtempfile);
            destFiles.add(desttempfile);
            String connectionDetails = hostname + ":" + getPort();
            Session session = null;
            try {
                log.info("Deploying application "+ name + " to " + connectionDetails);
                session = demandCreateSession(hostname);
                sshExec(session, "mkdir -p " + destDir, false);
                ScpTo scp = new ScpTo(sfLog());
                //copy up the temp files
                scp.doCopy(session, destFiles, sourceFiles);
                String sshCommand = "sfStart " + "localhost" + " " + name + " " + desttempfile;
                log.info("executing: " + sshCommand);
                sshExec(session, sshCommand, true);
                if(!keepFiles) {
                    sshExec(session, "rm " + desttempfile, true);
                }
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

        private synchronized int incCounter() {
            return counter++;
        }

        private void sshExec(Session session, String commandString, boolean checkResponse)
                throws JSchException, IOException, SmartFrogException {
            SshCommand command = new SshCommand(sfLog(), null);
            OutputStreamLog outputStream = new OutputStreamLog(log, outputLogLevel);
            ArrayList<String> commandsList = new ArrayList<String>(1);
            commandsList.add(commandString);
            int exitCode = command.execute(session, commandsList, outputStream, getTimeout());
            if (exitCode != 0) {
                throw new SmartFrogException("Error response on command " + commandString);
            }
        }

        /**
         * End a session
         *
         * @param session session, can be null
         */
        private void endSession(Session session) {
            if (session != null) {
                session.disconnect();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean terminateApplication(String name, boolean normal, String exitText)
                throws IOException, SmartFrogException {
            return false;
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
