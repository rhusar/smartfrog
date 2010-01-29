/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.cloudfarmer.server.deployment;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.tools.ant.util.TeeOutputStream;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.ports.PortUtils;
import org.smartfrog.services.ssh.ScpTo;
import org.smartfrog.services.ssh.SshCommand;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.OutputStreamLog;
import org.smartfrog.sfcore.utils.SFExpandFully;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SSH based deployment, assumes deploy-by-copy to a specified destdir, uses a given login .
 */
public final class NodeDeploymentOverSSH extends AbstractNodeDeployment implements NodeDeploymentService {

    private String hostname;
    private static final String SF_SUFFIX = ".sf";
    private static final String SSH_RESPONSE_CHARSET = "ISO-8859-1";
    private NodeDeploymentOverSSHFactory factory;
    private static final String NOT_EXTERNAL
            = "This node is not externally visible. Unless we are in the cell, deployment will not work";

    public NodeDeploymentOverSSH(NodeDeploymentOverSSHFactory factory, ClusterNode node) {
        super(node);
        this.factory = factory;
        hostname = node.getExternalHostname();
    }


    private String makeSFCommand(String command) {
        return factory.makeSFCommand(command);
    }

    private void info(String text) {
        factory.getLog().info(text);
    }

    /**
     * {@inheritDoc}
     *
     * @param name application name
     * @param cd   component description
     * @throws IOException        IO problems
     * @throws SmartFrogException SF problems
     */
    @Override
    public synchronized void deployApplication(String name, ComponentDescription cd)
            throws IOException, SmartFrogException {
        if (!getClusterNode().isExternallyVisible()) {
            info(NOT_EXTERNAL);
        }
        File localtempfile = File.createTempFile(factory.getTempfilePrefix(), SF_SUFFIX);
        String desttempfile = factory.getDestDir() + factory.getTempfilePrefix() + getNextNumber() + SF_SUFFIX;
        SFExpandFully.saveCDtoFile(cd, localtempfile);
        ArrayList<File> sourceFiles = new ArrayList<File>();
        ArrayList<String> destFiles = new ArrayList<String>();
        sourceFiles.add(localtempfile);
        destFiles.add(desttempfile);
        String connectionDetails = getConnectionDetails();
        info("Deploying application with SSH " + name + " to " + connectionDetails);

        //make a pre-emptive connection to the port; this blocks waiting for things like machines to come up

        PortUtils.checkPort(hostname, factory.getPort(), factory.getPortConnectTimeout());


        Session session = null;
        try {
            session = factory.demandCreateSession(hostname);
            ArrayList<String> commandsList = new ArrayList<String>(1);
            commandsList.add("mkdir -p " + factory.getDestDir());
            commandsList.add("exit");
            sshExec(session, commandsList, true);

            ScpTo scp = new ScpTo(factory.sfLog());
            //copy up the temp files
            info("copying " + localtempfile + " to " + desttempfile);
            scp.doCopy(session, destFiles, sourceFiles);
            String sshCommand;
            sshCommand = makeSFCommand("sfStart") + " " + "localhost" + " " + name + " " + desttempfile;
            info("executing: " + sshCommand);
            commandsList = new ArrayList<String>(1);
            commandsList.add(sshCommand);
            if (!factory.isKeepFiles()) {
                commandsList.add("rm " + desttempfile);
            }
            commandsList.add("exit");
            sshExec(session, commandsList, true);
        } catch (JSchException e) {
            factory.getLog().error("Failed to upload to " + connectionDetails + " : " + e, e);
            throw factory.forward(e, connectionDetails);
        } finally {
            info("Finished deploying application " + name + " to " + connectionDetails);
            endSession(session);
            if (!factory.isKeepFiles()) {
                localtempfile.delete();
            }
        }
    }

    private String getConnectionDetails() {
        return factory.getUserName() + "@" + hostname + ":" + factory.getPort();
    }

    /**
     * Get a new number
     *
     * @return a new number for use in filenames
     */
    private synchronized int getNextNumber() {
        return factory.getNextNumber();
    }

    private void sshExec(Session session, String commandString, boolean checkResponse)
            throws JSchException, IOException, SmartFrogException {
        ArrayList<String> commandsList = new ArrayList<String>(1);
        commandsList.add(commandString);
        sshExec(session, commandsList, checkResponse);
    }

    private int sshExec(Session session, ArrayList<String> commandsList, boolean checkResponse)
            throws JSchException, IOException, SmartFrogException {
        SshCommand command = new SshCommand(factory.sfLog(), null);
        OutputStreamLog outputStreamLog = new OutputStreamLog(factory.getLog(), factory.getOutputLogLevel());
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        TeeOutputStream teeOut = new TeeOutputStream(outputStreamLog, byteOutputStream);
        int exitCode = command.execute(session, commandsList, teeOut, factory.getTimeout());
        if (checkResponse && exitCode != 0) {
            String output = byteOutputStream.toString(SSH_RESPONSE_CHARSET);
            throw new SmartFrogException("Error response on command " + commandsList.get(0)
                    + ":-\n"
                    + output,
                    factory);
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
            info("Terminating application " + name + " for " + exitText);
            session = factory.demandCreateSession(hostname);
            ArrayList<String> commandsList = new ArrayList<String>(1);
            String sshCommand = makeSFCommand("sfTerminate") + " " + "localhost" + " " + name;
            info("executing: " + sshCommand);
            commandsList.add(sshCommand);
            commandsList.add("exit");
            return sshExec(session, commandsList, false) == 0;
        } catch (JSchException e) {
            String connectionDetails = getConnectionDetails();
            factory.getLog().error("Failed to terminate " + name + " on " + connectionDetails + " : " + e, e);
            throw factory.forward(e, connectionDetails);
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
