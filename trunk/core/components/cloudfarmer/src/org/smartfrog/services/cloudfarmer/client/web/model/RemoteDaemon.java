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

import org.apache.struts.upload.FormFile;
import org.smartfrog.services.cloudfarmer.api.LocalSmartFrogDescriptor;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.ParseFailedException;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.AbstractWorkflowServerActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitMRJobActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitToolActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitWorkflowActionForm;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.WorkflowList;
import org.smartfrog.services.cloudfarmer.server.deployment.NodeDeploymentOverRMI;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;

/**
 * A remote daemon. Defaults to localhost and port 3800.
 */
public class RemoteDaemon extends AbstractEndpoint {

    //we have to reconnect when deserializing
    private static final long serialVersionUID = -3102169331874193302L;

    private transient NodeDeploymentOverRMI nodeDeploy;

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
        nodeDeploy = new NodeDeploymentOverRMI(baseURL);
    }

    public String getHostname() {
        return nodeDeploy.getHostname();
    }

    public int getPort() {
        return nodeDeploy.getPort();
    }


    /**
     * Print the hostname and port
     *
     * @return printable description
     */
    @Override
    public String toString() {
        return "workflow server "+ nodeDeploy.toString();
    }

    /**
     * Bind to the remote node. This may fail with an error
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException        network/RMI trouble
     */
    private ProcessCompound bind() throws SmartFrogException, IOException {
        return nodeDeploy.bind();
    }

    /**
     * Implement on-demand binding
     *
     * @return the process compound
     * @throws SmartFrogException problems binding
     * @throws IOException        network/RMI trouble
     */
    public ProcessCompound bindOnDemand() throws SmartFrogException, IOException {
        return nodeDeploy.bindOnDemand();
    }

    /**
     * Get any process to which we are bound -there is no on demand binding here
     *
     * @return the process or null
     */
    public synchronized ProcessCompound getBoundProcess() {
        return nodeDeploy.getBoundProcess();
    }


    /**
     * Break the connection
     * @throws IOException        on network failure
     * @throws SmartFrogException SF problems
     */
    public synchronized void unbind() throws SmartFrogException, IOException {
        nodeDeploy.terminate();
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
     * @param classname class to use in the CD
     * @return the stub CD
     * @throws SmartFrogRuntimeException on a failure to create
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
     * @throws SmartFrogRuntimeException on a failure to create
     *
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
     * @return a resolved workflow  or null if there was none and mandatory==false
     * @throws IOException        for network problems
     * @throws SmartFrogException any resolution problems
     */
    public Workflow lookup(String name, boolean mandatory) throws IOException, SmartFrogException {
        Prim prim = nodeDeploy.lookupPrim(name, mandatory);
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
        return nodeDeploy.terminateApplication(name, normal, exitText);
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
            parseFormFile(localSF, srcFile);
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

    /**
     * Parse a form file, using the supplied filename as the filename if it is non null and ends with .sf
     *
     * @param descriptor SF descriptor
     * @param file the form file to parse
     * @return true iff it it parsed without errors
     * @throws IOException on any failure
     */
    public boolean parseFormFile(LocalSmartFrogDescriptor descriptor, FormFile file) throws IOException {
        String filename = file.getFileName();
        if (filename == null || !filename.endsWith(".sf")) {
            filename = "uploaded.sf";
        }
        InputStream is = file.getInputStream();
        return descriptor.parseFromInputStream(filename, is);
    }

}
