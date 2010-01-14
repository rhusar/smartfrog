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
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.client.web.clusters.ClusterAllocationHandler;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.ClusterAddDynamicForm;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.ClusterCreateRoleInstanceForm;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequest;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.RoleAllocationRequestList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Submit a job to the workflow server. Some devious tricks are paid with overloaded methods, as whichever form is
 * received, it is routed over to the daemon to handle
 */
@SuppressWarnings({"RefusedBequest"})
public class CreateRoleInstanceProcessAction extends AbstractClusterAction {

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name
     */

    @Override
    protected String getActionName() {
        return "CreateRoleInstanceProcessAction";
    }


    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm aform, HttpServletRequest request,
                                 HttpServletResponse response, ClusterController controller) throws Exception {
        ClusterCreateRoleInstanceForm form = (ClusterCreateRoleInstanceForm) aform;
        try {
            String role;
            //role = parameterToAttribute(request, ATTR_ROLE, ATTR_ROLE, false);
            role = form.getRole();
            if (isEmptyOrNull(role)) {
                logParameters(request);
                return failure(request, mapping, "No role provided");
            }
            ClusterRoleInfo roleInfo = controller.getRole(role);
            if (roleInfo == null) {
                return failure(request, mapping, "Role " + role + " is not available");
            }
            HostInstanceList hil = controller.lookupHostsByRole(role);
            log.info("Creating nodes of role \"" + role + "\" in range [" + form.getMinNodes() + "-" +
                    form.getMaxNodes() + "]");
            ClusterAllocationHandler
                    handler = new ClusterAllocationHandler(controller);
            RoleAllocationRequestList requests = new RoleAllocationRequestList(1);
            RoleAllocationRequest workers = new RoleAllocationRequest(role,
                    hil.size(),
                    form.getMinNodes(),
                    form.getMaxNodes(),
                    null);
            requests.add(workers);

            long farmCreationTimeout = getFarmCreationTimeout();
            log.info("Queueing a request, waiting up to " + farmCreationTimeout + " milliseconds for the farm");
            ClusterController.HostCreationThread worker = controller.asyncCreateHosts(requests,
                    farmCreationTimeout,
                    handler,
                    null);
            addClusterAttributes(request, controller);
            return success(mapping);
        } catch (Exception e) {
            return failure(request, mapping, "Failed to add the hosts : " + e, e);
        }

    }


}
