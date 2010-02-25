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
import org.smartfrog.services.cloudfarmer.client.web.actions.cluster.AbstractClusterAction;
import org.smartfrog.services.cloudfarmer.api.NodeLink;
import org.smartfrog.services.cloudfarmer.server.examples.HadoopRoleNames;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created 25-Feb-2010 17:14:03
 */

public class HadoopConfServlet extends HttpServlet {

    public static final Log log = LogFactory.getLog(HadoopConfServlet.class);
    
    public HadoopConfServlet() {
    }

    public  ClusterController getFarmController(HttpServletRequest request) {
        return AbstractClusterAction.getFarmController(request);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //super.doGet(request, response);
        log.info("Building the Hadoop config file");
        ClusterController controller = getFarmController(request);
        HostInstance master = controller.getMaster();
        if (master == null) {
            error(response, 404,
                    "Cluster has no hadoop master");
            return;
        }
        
        String hostname = master.getExternalHostname();
        if (hostname == null || hostname.isEmpty()) {
            error(response, 404,
                    "Cluster master has no hostname");
            return;
        }

        try {
            NodeLink nodeLink = master.resolveNodeLink(HadoopRoleNames.LINK_NAMENODE_CONFIGURATION);
            response.sendRedirect(nodeLink.getExternalLink().toString());
        } catch (SmartFrogResolutionException e) {
            error(response, 500, e.toString());
        }
/*
        HttpClient httpclient = new HttpClient();
        HttpMethod getRequest = new GetMethod(nodeLink.getPath());
        HttpState state = new HttpState();
        HttpConnection conn = new HttpConnection(hostname,
                nodeLink.getPort(),
                Protocol.getProtocol(nodeLink.getProtocol()));
        //do the work
        getRequest.execute(state, conn);
*/
    }
    
    public void error(HttpServletResponse response, int code, String text) throws IOException {
/*        response.setStatus(code);
        response.setContentType("text/plain");*/
        response.sendError(code, text);
    }
}
