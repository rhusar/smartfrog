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

import org.smartfrog.services.jetty.utils.ServletUtils;
import org.smartfrog.services.www.HttpHeaders;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class BindingServlet extends HttpServlet {


    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(HttpHeaders.TEXT_HTML);
        ServletContext ctx = getServletContext();
        PrintWriter out = response.getWriter();
        ServletUtils.open(out, "html");
        ServletUtils.element(out, "head", "Context Attributes");
        ServletUtils.open(out, "body");
        ServletUtils.open(out, "table");
        //print the header
        ServletUtils.open(out, "tr");
        ServletUtils.element(out, "td", "key");
        ServletUtils.element(out, "td", "value");
        ServletUtils.close(out, "tr");

        //then go through the values
        Enumeration names = ctx.getAttributeNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement().toString();
            Object value = ctx.getAttribute(key);
            ServletUtils.open(out, "tr");
            ServletUtils.element(out, "td", key);
            ServletUtils.element(out, "td", value.toString());
            ServletUtils.close(out, "tr");
        }
        ServletUtils.close(out, "table");
        ServletUtils.close(out, "body");
        ServletUtils.close(out, "html");
        out.flush();
        out.close();
    }
}
