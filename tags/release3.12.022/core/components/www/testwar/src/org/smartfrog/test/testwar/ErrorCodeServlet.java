/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test.testwar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 */
public class ErrorCodeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String codestr=request.getParameter("status");
        int code=400;
        if(codestr!=null) {
            code=Integer.valueOf(codestr).intValue();
        }
        response.setStatus(code);
        out.println("<html><head><title>Error Page</title</head><body>");
        out.println("<h1>Error Code "+code+"</h1>");

        out.println("<p>Headers</p>");
        out.println("<table>");
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value=request.getHeader(name);
            out.println("<tr><td>" + name+ "</td><td>"+value+"</td></tr>");
        }
        out.println("</table>");
    }

}
