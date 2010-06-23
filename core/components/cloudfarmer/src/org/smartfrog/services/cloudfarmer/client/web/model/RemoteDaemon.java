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
import org.smartfrog.sfcore.common.LocalSmartFrogDescriptor;
import org.smartfrog.services.cloudfarmer.client.common.BaseRemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.ParseFailedException;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.AbstractWorkflowServerActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitMRJobActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitToolActionForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.workflow.SubmitWorkflowActionForm;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.services.cloudfarmer.server.deployment.NodeDeploymentOverRMI;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * A remote daemon. Defaults to localhost and port 3800.
 */
public class RemoteDaemon extends BaseRemoteDaemon {

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
