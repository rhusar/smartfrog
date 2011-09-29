/** (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty.examples.servlets;

import org.smartfrog.services.jetty.contexts.delegates.DelegateHelper;
import org.smartfrog.services.jetty.utils.ServletUtils;
import org.smartfrog.services.www.HttpHeaders;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Servlet that resolves the path supplied
 */
public class ResolveServlet extends HttpServlet {

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(HttpHeaders.TEXT_HTML);
        ServletContext ctx = getServletContext();
        PrintWriter out = response.getWriter();
        String path = request.getParameter("path");


        Enumeration names = ctx.getAttributeNames();
        ServletUtils.open(out, "html");
        ServletUtils.element(out, "head", "Resolving");
        ServletUtils.open(out, "body");

        ServletUtils.element(out, "p", "Resolving path " + path);
        try {
            Prim owner = DelegateHelper.retrieveOwner(ctx);

            Object result;
            if (path != null) {
                result = owner.sfResolve(path);
            } else {
                result = owner;
            }

            ServletUtils.element(out, "p", "Resolved class " + result.getClass());
            ServletUtils.element(out, "p", "Resolved string value" + result);
        } catch (SmartFrogException e) {
            ServletUtils.element(out, "pre:", e.toStringAll("\n"));
        }
        ServletUtils.close(out, "body");
        ServletUtils.close(out, "html");
        out.flush();
        out.close();
    }

}
