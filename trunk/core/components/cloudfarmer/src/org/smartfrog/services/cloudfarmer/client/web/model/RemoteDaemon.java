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


package org.smartfrog.services.cloudfarmer.client.web.model;

import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.AbstractWorkflowServerActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitMRJobActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitToolActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitWorkflowActionForm;
import org.smartfrog.services.cloudfarmer.client.web.model.AbstractEndpoint;
import org.smartfrog.services.cloudfarmer.client.web.model.Constants;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.WorkflowList;
import org.apache.struts.upload.FormFile;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.ParseFailedException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.DefaultRootLocatorImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * A remote daemon. Defaults to localhost and port 3800.
 */
public class RemoteDaemon extends AbstractEndpoint {

    private String hostname = Constants.DEFAULT_DAEMON_HOST;
    private int port = Constants.DEFAULT_DAEMON_PORT;
    //we have to reconnect when deserializing
    private transient ProcessCompound boundProcess;
    private static final long serialVersionUID = -3102169331874193302L;


    public RemoteDaemon() {
    }

    /**
     * Extract hostname and port from the URL
     *
     * @param baseURL URL of the daemon
     * @throws MalformedURLException on a bad URL
     */
    public RemoteDaemon(String baseURL) throws MalformedURLException {
        super(baseURL);
        URL url = new URL(baseURL);
        setHostname(url.getHost());
        if (url.getPort() != -1) {
            setPort(url.getPort());
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Print the hostname and port
     *
     * @return printable description
     */
    @Override
    public String toString() {
        return "workflow server at " + hostname + ":" + port;
    }

    /**
     * Bind to the remote node. This may fail with an error
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException        network/RMI trouble
     */
    private ProcessCompound bind() throws SmartFrogException, IOException {
        InetAddress addr = InetAddress.getByName(hostname);
        DefaultRootLocatorImpl defaultRootLocator = new DefaultRootLocatorImpl();
        try {
            return defaultRootLocator.getRootProcessCompound(addr, port);
        } catch (SmartFrogException e) {
            throw e;
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * Implement on-demand binding
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException        network/RMI trouble
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public synchronized ProcessCompound bindOnDemand() throws SmartFrogException, IOException {
        if (boundProcess == null) {
            boundProcess = bind();
        }
        return boundProcess;
    }

    /**
     * Get any process to which we are bound -there is no on demand binding here
     *
     * @return the process or null
     */
    public synchronized ProcessCompound getBoundProcess() {
        return boundProcess;
    }


    /**
     * Break the connection
     */
    public synchronized void unbind() {
        boundProcess = null;
    }

    /**
     * List all active workflows. This is a remote operation, fairly chatty.
     *
     * @return a list of workflows
     * @throws IOException        on network failure
     * @throws SmartFrogException SF problems
     */
    public WorkflowList listWorkflows() throws IOException, SmartFrogException {
        ProcessCompound root = getBoundProcess();
        WorkflowList result = new WorkflowList(this);
        Enumeration<Liveness> children = root.sfChildren();
        while (children.hasMoreElements()) {
            Liveness liveness = children.nextElement();
            Object key = root.sfAttributeKeyFor(liveness);
            Workflow childApp = new Workflow(this, key, (Prim) liveness);
            result.add(childApp);
        }
        return result;
    }


    /**
     * Create a workflow
     *
     * @param name workflow name
     * @param cd   components to deploy
     * @return the deployed workflow
     * @throws IOException        on network failure
     * @throws SmartFrogException SF problems
     */
    public Workflow createWorkflow(String name, ComponentDescription cd) throws IOException, SmartFrogException {
        log.info("Creating a workflow " + name);
        ProcessCompound root = getBoundProcess();
        Prim app = root.sfCreateNewApp(name, cd, null);
        Workflow workflow = new Workflow(this, name, app);
        return workflow;
    }

    /**
     * Creates a local CD that is then pushed over
     *
     * @return a blank CD
     */
    private ComponentDescription createCD() {
        ComponentDescription cd = new ComponentDescriptionImpl(null, null, false);
        return cd;
    }

    /**
     * Create a CD of a given classname
     *
     * @param classname
     * @return the stub CD
     * @throws SmartFrogRuntimeException
     */
    private ComponentDescription createCD(String classname) throws SmartFrogRuntimeException {
        ComponentDescription cd = createCD();
        cd.sfAddAttribute("sfClass", classname);
        return cd;
    }

    /**
     * Creates a local CD that is then pushed over
     *
     * @return a blank CD
     */
    private ComponentDescription createCompoundCD() throws SmartFrogRuntimeException {
        return createCD("org.smartfrog.sfcore.compound.CompoundImpl");
    }

    /**
     * @param form form to use as a source
     * @param cd   CD to create from
     * @return a new workflow
     * @throws IOException        on network failure
     * @throws SmartFrogException SF problems
     */
    private Workflow createWorkflow(AbstractWorkflowServerActionForm form, ComponentDescription cd)
            throws IOException, SmartFrogException {
        return createWorkflow(form.getName(), cd);
    }

    /**
     * retrieve a workflow by name. This is a remote operation.
     *
     * @param name      string to look up
     * @param mandatory true iff the name is required
     * @return a resolved workflow
     * @throws IOException        for network problems
     * @throws SmartFrogException any resolution problems
     */
    public Workflow lookup(String name, boolean mandatory) throws IOException, SmartFrogException {
        ProcessCompound root = getBoundProcess();
        Object resolved = root.sfResolveHere(name, mandatory);
        if (resolved == null) {
            return null;
        }
        if (resolved instanceof ComponentDescription) {
            throw new SmartFrogResolutionException("The name " + name + " resolves to an undeployed component");
        }
        if (!(resolved instanceof Prim)) {
            throw new SmartFrogResolutionException("The name " + name
                    + " resolves to " + resolved.toString()
                    + " and not a deployed workflow");
        }
        Prim prim = (Prim) resolved;
        return new Workflow(this, name, prim);
    }


    /**
     * Resolve and terminate a workflow
     *
     * @param name     the workflow name
     * @param normal   flag for normal exit; false for abnormal
     * @param exitText exit text
     * @return true if the resolution succeeded and the termination begain
     * @throws IOException        for network problems
     * @throws SmartFrogException any resolution problems
     */
    public boolean terminate(String name, boolean normal, String exitText) throws IOException, SmartFrogException {
        Workflow workflow = lookup(name, false);
        if (workflow == null) {
            return false;
        }
        workflow.terminate(normal, exitText);
        return true;
    }

    /**
     * Process the form
     *
     * @param form the form
     * @return the new workflow
     * @throws IOException        network trouble
     * @throws SmartFrogException problems on the SmartFrog side of things
     */
    public Workflow queue(SubmitMRJobActionForm form) throws IOException, SmartFrogException {
        ComponentDescription cd = createCompoundCD();
        Workflow workflow = createWorkflow(form, cd);
        return workflow;
    }

    /**
     * Process the form
     *
     * @param form the form
     * @return the new workflow
     * @throws IOException        network trouble
     * @throws SmartFrogException problems on the SmartFrog side of things
     */
    public Workflow queue(SubmitToolActionForm form) throws SmartFrogException, IOException {
        ComponentDescription cd = createCompoundCD();
        Workflow workflow = createWorkflow(form, cd);

        return workflow;
    }

    /**
     * Process the form
     *
     * @param form the form
     * @return the new workflow
     * @throws IOException        network trouble
     * @throws SmartFrogException problems on the SmartFrog side of things
     */
    public Workflow queue(SubmitWorkflowActionForm form) throws SmartFrogException, IOException {
        LocalSmartFrogDescriptor localSF = new LocalSmartFrogDescriptor();
        FormFile srcFile = form.getFile();

        if (srcFile != null) {
            localSF.parseFormFile(srcFile);
        } else {
            //it will have to be text, won't it
            localSF.parseText(form.getConf());
        }
        if (localSF.hasErrors()) {
            throw new ParseFailedException("Failed to parse", localSF);
        }
        ComponentDescription cd = localSF.getComponentDescription();
        Workflow workflow = createWorkflow(form, cd);
        return workflow;
    }

}
