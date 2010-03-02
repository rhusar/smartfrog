/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.servlets;

import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.HostInstance;
import org.smartfrog.services.cloudfarmer.api.NodeLink;
import org.smartfrog.services.cloudfarmer.server.examples.HadoopRoleNames;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created 25-Feb-2010 17:14:03
 */

public class HadoopConfServlet extends AbstractMombasaServlet {

    public static final String ERROR_NO_NAMENODE_LINK = "Could not find namenode link";

    public HadoopConfServlet() {
    }

    /**
     * Handle a GET request by redirecting to any deployed master, returns 404 if not
     * @param request incoming request
     * @param response outgoing response
     * @throws ServletException servlet problems
     * @throws IOException IO problems
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //super.doGet(request, response);
        log.info("Building the Hadoop config file");
        
        //look up the farm controller from the request
        ClusterController controller;
        try {
            controller = getFarmController(request);
            if (controller == null) {
                //express internal 500 concern if it is not there
                error(response, 
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ERROR_NO_CONTROLLER);
                return;
            }
        } catch (SmartFrogException e) {
            error_no_cluster_controller(response, e);
            return;
        }

        //get the master node from the farm
        try {
            controller.refreshRoleList();
        } catch (SmartFrogException e) {
            //bad link, probably
            error(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to talk to controller: " + controller.toString(),
                    e);
            return;
        }
        HostInstance master = controller.getMaster();
        if (master == null) {
            //It's OK to have a farm with no master, but then you get a 404
            error_no_hadoop_master(response, controller);
            return;
        }
        
        //the cluster must have an external hostname
        String hostname = master.getExternalHostname();
        if (hostname == null || hostname.isEmpty()) {
            error_no_hadoop_master_hostname(response, controller);
            return;
        }

        //now look up the nodelink
        try {
            NodeLink nodeLink = master.resolveNodeLink(HadoopRoleNames.LINK_NAMENODE_CONFIGURATION);
            
            //success! redirect!
            disableCaching(response);
            response.sendRedirect(nodeLink.getExternalLink().toString());
        } catch (SmartFrogResolutionException e) {
            //bad link, probably
            error(response, 
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    ERROR_NO_NAMENODE_LINK,
                    e);
            return;
        }

    }

    private void error_no_cluster_controller(HttpServletResponse response, SmartFrogException e) throws IOException {
        error(response, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                ERROR_NO_CONTROLLER,
                e);
    }

    private void error_no_hadoop_master_hostname(HttpServletResponse response, ClusterController controller)
            throws IOException {
        error(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    ERROR_NO_MASTER_HOSTNAME + controller.toString());
    }

    private void error_no_hadoop_master(HttpServletResponse response, ClusterController controller) throws IOException {
        error(response, 
                HttpServletResponse.SC_NOT_FOUND,
                ERROR_NO_HADOOP_MASTER + controller.toString());
    }


}
