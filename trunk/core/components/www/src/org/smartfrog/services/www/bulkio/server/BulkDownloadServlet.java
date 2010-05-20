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
package org.smartfrog.services.www.bulkio.server;

import org.smartfrog.services.www.HttpAttributes;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Bulk D/L Servlet serves bullk quantities of data
 */

public class BulkDownloadServlet extends AbstractBulkioServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        disableCaching(response);
        long size = getSize(request);
        if (size == -1) {
            //response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No " + ATTR_SIZE + " parameter");
            response.setContentType(TEXT_HTML);
            ServletOutputStream out = null;
            try {
                out = response.getOutputStream();
                out.println("<html>");
                out.println("<head><title>Download</title></head>");
                out.println("<body><h1>Download</h1>");
                out.println(" <form method='get'><h2>Download a file</h2>");
                out.println("  <p> Size:");
                out.println("  <input type='text'name='"+ ATTR_SIZE+"'/>");
                out.println("  <p>");
                out.println("  <p> Format:");
                out.println("  <input type='text'name='"+ ATTR_FORMAT+"' value='text/plain'/>");
                out.println("  <p>");
                out.println("  <button type='submit'>submit</button>");
                out.println("  <p>");
                out.println(" </form>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                closeQuietly(out);
            }
        } else {
            String format = request.getParameter(ATTR_FORMAT);
            if (format == null) {
                format = TEXT_PLAIN;
            }
            response.setContentType(format);
            response.setHeader(HttpAttributes.HEADER_CONTENT_LENGTH, Long.toString(size));
            response.setHeader("X-" + HttpAttributes.HEADER_CONTENT_LENGTH, Long.toString(size));
            ServletOutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                for (long bytes = 0; bytes < size; bytes++) {
                    outputStream.write(getByteFromCounter(bytes));
                }
            } finally {
                closeQuietly(outputStream);
            }
        }

    }

}