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
import org.smartfrog.services.cloudfarmer.client.web.exceptions.BadParameterException;
import org.smartfrog.services.cloudfarmer.client.web.forms.cluster.ClusterChangeManagerForm;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterControllerBinding;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterControllerFactory;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstanceList;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.sfcore.common.SmartFrogException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.ConnectException;

/**
 * Created 02-Sep-2009 16:01:35
 */

public abstract class AbstractClusterAction extends AbstractStrutsAction {

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
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        ClusterController controller;
        try {
            controller = bindToClusterController(request);
        } catch (Exception e) {
            return bindFailure(request, mapping, "Failed to bind to the cluster controller", e);
        }
        return execute(mapping, form, request, response, controller);
    }


    /**
     * Do the work
     *
     * @param mapping    mapping
     * @param form       incoming form
     * @param request    incoming request
     * @param response   response to build up
     * @param controller the cluster controller
     * @return the follow-up action
     * @throws Exception any exception to handle server-side
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})

    public abstract ActionForward execute(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          ClusterController controller) throws Exception;

    public static ClusterController getFarmController(HttpServletRequest request) {
        return (ClusterController) getAttributeFromRequestState(request, FARM_CONTROLLER);
    }

    /**
     * extract the value of {@link #ATTR_HOSTID} from the request, look up the host by that ID in the cluster, then
     * return the host. The host is set on the request under the attribute {@link #ATTR_HOSTID}
     *
     * @param request request to manipulate
     * @return the host
     * @throws BadParameterException if the hostId is missing or there is no host with that name
     * @throws IOException        network problems
     * @throws SmartFrogException SF problems
     */
    public static HostInstance bindHostInstance(HttpServletRequest request) throws IOException, SmartFrogException {
        ClusterController cluster = bindToClusterController(request);
        String hostID = parameterToAttribute(request, ATTR_HOSTID, ATTR_HOSTID, true);
        HostInstance host = cluster.lookupHost(hostID);
        if (host == null) {
            throw new BadParameterException("No Host with ID " + hostID + " in the cluster");
        }
        request.setAttribute(ATTR_HOST, host);
        return host;
    }

    /**
     * bind to the cluster controller
     *
     * @param request incoming request
     * @return the controller instance
     * @throws IOException        network problems
     * @throws SmartFrogException problem instantiating the controller
     */
    public static ClusterController bindToClusterController(HttpServletRequest request)
            throws IOException, SmartFrogException {
        ClusterController server = getFarmController(request);
        if (server == null) {
            ClusterControllerFactory factory = new ClusterControllerFactory();
            server = factory.createClusterController(request);
            try {
                server.bind();
            } catch (ConnectException e) {
                throw new ConnectException("Could not connect to " + server + "\n" + e, e);
            }
            setAttribute(request, FARM_CONTROLLER, server);
        }
        return server;
    }

    /**
     * Change to a new cluster controller
     *
     * @param request           incoming request
     * @param changeClusterForm the form
     * @return the controller instance
     * @throws IOException        network problems
     * @throws SmartFrogException problem instantiating the controller
     */
    public static ClusterController switchClusterController(HttpServletRequest request,
                                                            ClusterChangeManagerForm changeClusterForm)
            throws SmartFrogException, IOException {
        ClusterControllerBinding binding = changeClusterForm.createControllerBinding();
        ClusterControllerFactory factory = new ClusterControllerFactory();
        ClusterController server = factory.createClusterController(binding);
        removeAttribute(request, FARM_CONTROLLER);
        //this can fail. In this situation, the current controller has alreaby been removed.
        server.bind();
        setAttribute(request, FARM_CONTROLLER, server);
        return server;
    }

    /**
     * Get the host list then add the {@link #ATTR_HOSTS} and {@link #ATTR_HOSTCOUNT} attributes
     *
     * @param request    request to manipulate
     * @param controller controller to work from
     */
    protected void addHostAttributes(HttpServletRequest request, ClusterController controller) {
        HostInstanceList hosts = controller.getHosts();
        addHostAttributes(request, hosts);
    }

    /**
     * Add the {@link #ATTR_HOSTS} and {@link #ATTR_HOSTCOUNT} attributes
     * @param request    request to manipulate
     * @param hosts list of hosts
     */
    protected void addHostAttributes(HttpServletRequest request, HostInstanceList hosts) {
        request.setAttribute(ATTR_HOSTS, hosts);
        request.setAttribute(ATTR_HOSTCOUNT, hosts.size());
    }

    /**
     * Add info about the master and the hosts
     * @param request    request to manipulate
     * @param controller controller to work from
     */
    protected void addClusterAttributes(HttpServletRequest request, ClusterController controller) {
        addMasterAttributes(request, controller);
        addHostAttributes(request, controller);
    }

    /**
     * Add info about the master and the hosts
     *
     * @param request    request to manipulate
     * @param roleInfo role information
     */
    protected void addRoleAttributes(HttpServletRequest request, ClusterRoleInfo roleInfo) {
        request.setAttribute(ATTR_ROLE, roleInfo.getName());
        request.setAttribute(ATTR_ROLEINFO, roleInfo);
    }

    /**
     * The time to wait for a machine
     * TODO, drive this from configuration properties
     * @return the timeout in milliseconds
     */
    protected int getFarmCreationTimeout() {
        return 10000;
    }
}
