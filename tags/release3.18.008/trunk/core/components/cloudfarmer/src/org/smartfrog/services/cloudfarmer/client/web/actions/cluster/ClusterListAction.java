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

package org.smartfrog.services.cloudfarmer.client.web.actions.cluster;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * list available machines
 */
@SuppressWarnings({"RefusedBequest"})
public class ClusterListAction extends AbstractClusterAction {

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name
     */

    protected String getActionName() {
        return "ClusterListAction";
    }

    /**
     * Lists the hosts to the "hosts" attribute
     *
     * @param mapping    mapping
     * @param form       incoming form
     * @param request    incoming request
     * @param response   response to build up
     * @param controller the cluster controller
     * @return the follow-up action
     * @throws Exception any exception to handle server-side
     */
    @Override
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 ClusterController controller) throws Exception {

        try {
            //refresh the list
            controller.refreshHostList();
            //get the values
            addClusterAttributes(request, controller);
            return success(mapping);
        } catch (Exception e) {
            return failure(request, mapping, "Failed to list hosts :" + e, e);
        }
    }

}
