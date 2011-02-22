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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created 25-Feb-2010 17:14:03
 */

public class HadoopConfServlet extends AbstractMombasaServlet {

    public static final String ERROR_NO_NAMENODE_LINK = "Could not find namenode link";

    public HadoopConfServlet() {
    }


    /**
     *  This is the inner GET action bonded to a controller. The host and roles lists are refreshed
     * @param request incoming request
     * @param response outgoing response
     * @param controller the controller
     * @throws IOException IO problems
     */
    @Override
    protected void getAction(HttpServletRequest request, 
                             HttpServletResponse response,
                             ClusterController controller) throws IOException {
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


}
