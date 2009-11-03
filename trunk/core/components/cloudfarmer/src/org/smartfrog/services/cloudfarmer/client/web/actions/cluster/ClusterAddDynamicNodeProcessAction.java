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
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.ClusterAddDynamicForm;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;


/**
 * Submit a job to the workflow server. Some devious tricks are paid with overloaded methods, as whichever form is
 * received, it is routed over to the daemon to handle
 */
@SuppressWarnings({"RefusedBequest"})
public class ClusterAddDynamicNodeProcessAction extends AbstractClusterAction {

    /**
     * Get the name of this action, used in logging and debugging
     *
     * @return the name
     */

    @Override
    protected String getActionName() {
        return "ClusterAddDynamicNodeProcessAction";
    }

    

    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm aform, HttpServletRequest request,
                                 HttpServletResponse response, ClusterController controller) throws Exception {
        ClusterAddDynamicForm form = (ClusterAddDynamicForm) aform;
        try {
            log.info("Creating workers in range ["+ form.getMinWorkers() +"-"+form.getMaxWorkers() + "]");
            //add a master automatically
            List<ClusterController.RoleAllocationReqest> requests = new ArrayList<ClusterController.RoleAllocationReqest>(2);
            if (controller.getMaster() == null) {
                log.info("Creating a master node");
                requests.add(new ClusterController.RoleAllocationReqest("master", 0, 1, 1));
            }
            requests.add(
                    new ClusterController.RoleAllocationReqest("worker",
                        -1, 
                        form.getMinWorkers(), 
                        form.getMaxWorkers()));
            //TODO, drive this from configuration properties
            long farmCreationTimeout = 10000;
            log.info("Queueing a request, waiting up to "+farmCreationTimeout+" milliseconds for the farm");
            ClusterController.AsynchronousHostCreationThread worker = controller.asyncCreateHosts(requests,
                    farmCreationTimeout);
            addClusterAttributes(request, controller);
            return success(mapping);
        } catch (Exception e) {
            return failure(request, mapping, "Failed to add the hosts : " + e, e);
        }

    }

    
}