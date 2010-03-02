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
}
