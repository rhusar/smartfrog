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

package org.smartfrog.services.cloudfarmer.client.web.actions.workflow;

import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.WorkflowList;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * List the workflows on this server
 */
@SuppressWarnings({"RefusedBequest"})
public class WorkflowListAction extends AbstractWorkflowAction {

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name
     */

    @Override
    protected String getActionName() {
        return "WorkflowListAction";
    }

    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        RemoteDaemon daemon;
        try {
            daemon = bindToRemoteDaemon(request);
        } catch (Exception e) {
            return failure(request, mapping, "Failed to Bind to the daemon :" + e, e);
        }
        try {
            final WorkflowList workflows = daemon.listWorkflows();
            request.setAttribute(ATTR_WORKFLOW_LIST, workflows);
            return success(mapping);
        } catch (Exception e) {
            return failure(request, mapping, "Failed to list workflows :" + e, e);
        }
    }

}
