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
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.ClusterChangeManagerForm;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Terminate a cluster entry
 */
public class ClusterChangeManagerProcessAction extends AbstractClusterAction {

    @Override
    protected String getActionName() {
        return "ClusterChangeManagerAction";
    }

    /**
     * Bind to the cluster controller and then hand the work off to the bonded execute method
     *
     * @param mapping  mapping
     * @param form     incoming form
     * @param request  incoming request
     * @param response response to build up
     * @return the follow-up action
     * @throws Exception any exception to handle server-side
     */
    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        ClusterController controller;
        try {
            controller = bindToClusterController(request);
            log.info("Shutting down the cluster");
            controller.shutdownCluster();
        } catch (Exception e) {
            log.warn("No controller ", e);
        }
        try {
            log.info("Changing Manager");
            ClusterController newController = switchClusterController(request, (ClusterChangeManagerForm) form);
            log.info("New cluster controller: " + newController.getDescription());
            return success(mapping);
        } catch (Exception e) {
            return bindFailure(request, mapping, "Failed to change cluster controller", e);
        }
    }

    /**
     * This is no longer used, and is therefore stubbed out
     *
     * @param mapping    mapping
     * @param form       incoming form
     * @param request    incoming request
     * @param response   response to build up
     * @param controller the cluster controller
     * @return null, always
     * @throws Exception
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response, ClusterController controller) throws Exception {

        return null;
    }
}