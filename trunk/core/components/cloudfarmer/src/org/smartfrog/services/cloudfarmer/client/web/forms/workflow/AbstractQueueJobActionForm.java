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
package org.smartfrog.services.cloudfarmer.client.web.forms.workflow;

import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.smartfrog.sfcore.common.SmartFrogException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Jobs to queue. All forms default to having a name, and possibly a file or text
 */
public abstract class AbstractQueueJobActionForm extends AbstractWorkflowServerActionForm {
    private transient FormFile file;
    private String conf;
    private boolean isFileOrTextRequired = false;

    public FormFile getFile() {
        return file;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public boolean isFileOrTextRequired() {
        return isFileOrTextRequired;
    }

    public void setFileOrTextRequired(boolean fileOrTextRequired) {
        isFileOrTextRequired = fileOrTextRequired;
    }

    /**
     * validate the request
     *
     * @param actionMapping      mapping
     * @param httpServletRequest request
     * @return any errors
     */
    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        ActionErrors errors = super.validate(actionMapping, httpServletRequest);
        errors = validateFileAndText(errors, isFileOrTextRequired());
        return errors;
    }

    protected ActionErrors validateFileAndText(ActionErrors errors, boolean required) {
        ActionErrors response = errors;
        if (required && isEmptyOrNull(conf) && isEmptyFile(file)) {
            response = addError(response, "text", "error.noText");
        } else if (!isEmptyOrNull(conf) && !isEmptyFile(file)) {
            response = addError(response, "text", "error.textAndFile");
        }
        return response;
    }

    /**
     * Call this to queue the job. subclasses should call the type-specific RemoteDaemon.queue method
     *
     * @param daemon daemon to work with
     * @return the deployed workflow
     * @throws SmartFrogException SmartFrog problems
     * @throws IOException        Network or other I/O issues
     */
    public abstract Workflow queue(RemoteDaemon daemon) throws SmartFrogException, IOException;
}
