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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cloudfarmer.client.web.actions.cluster.AbstractClusterAction;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;
import org.smartfrog.sfcore.common.SmartFrogException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created 01-Mar-2010 12:01:19
 */

public abstract class AbstractMombasaServlet extends HttpServlet {
    public static final Log log = LogFactory.getLog(AbstractMombasaServlet.class);
    public static final String ERROR_NO_CONTROLLER = "Web Application has no farm controller";
    public static final String ERROR_NO_HADOOP_MASTER = "Cluster has no hadoop master: ";
    public static final String ERROR_NO_MASTER_HOSTNAME = "Cluster master has no hostname: ";

    public ClusterController getFarmController(HttpServletRequest request) throws SmartFrogException, IOException {
        return AbstractClusterAction.bindToClusterController(request);
    }

    public void tag(StringBuilder builder, String tag, String text) {
        builder.append("<").append(tag).append(">");
        builder.append(text);
        builder.append("</").append(tag).append(">");
    }

    public void error(HttpServletResponse response, int code, String text) throws IOException {
        response.sendError(code, text);
    }

    public void error(HttpServletResponse response, int code, String text, Throwable t) throws IOException {
        log.error(text, t);
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        builder.append("\n\n");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        builder.append(sw.toString());
        disableCaching(response);
        response.sendError(code, builder.toString());
    }

    /**
     * Turn off caching and say that the response expires now
     * @param response the response
     */
    protected void disableCaching(HttpServletResponse response) {
        response.addDateHeader("Expires", System.currentTimeMillis());
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Pragma", "no-cache");
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

        //refresh the lists
        if (refreshHostAndRoles()) {
            try {
                controller.refreshHostList();
                controller.refreshRoleList();
            } catch (SmartFrogException e) {
                //bad link, probably
                error(response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to talk to controller: " + controller.toString(),
                        e);
                return;
            }
        }
        disableCaching(response);
        getAction(request, response, controller);

    }


    /**
     * Override point: should the lists be updated first
     * @return true
     */
    protected boolean refreshHostAndRoles() {
        return true;
    }

    /**
     *  This is the inner GET action bonded to a controller. The host and roles lists are refreshed
     * @param request incoming request
     * @param response outgoing response
     * @param controller the controller
     * @throws IOException IO problems
     */
    protected abstract void getAction(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      ClusterController controller
    ) throws IOException;

    protected void error_no_cluster_controller(HttpServletResponse response, SmartFrogException e) throws IOException {
        error(response, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                ERROR_NO_CONTROLLER,
                e);
    }

    protected void error_no_hadoop_master_hostname(HttpServletResponse response, ClusterController controller)
            throws IOException {
        error(response,
                    HttpServletResponse.SC_NOT_FOUND,
                    ERROR_NO_MASTER_HOSTNAME + controller.toString());
    }

    protected void error_no_hadoop_master(HttpServletResponse response, ClusterController controller) throws IOException {
        error(response, 
                HttpServletResponse.SC_NOT_FOUND,
                ERROR_NO_HADOOP_MASTER + controller.toString());
    }
}
