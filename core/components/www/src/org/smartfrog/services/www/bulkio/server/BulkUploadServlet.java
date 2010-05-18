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

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created 17-May-2010 15:03:10
 */

public class BulkUploadServlet extends AbstractBulkioServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long size = getSize(request);
        long contentLength = getContentLength(request);
        if (size >= 0 && size != contentLength) {
            throw new ServletException(
                    "Content-Length header of " + contentLength + " does not equal the size parameter " + size);
        }
        ServletInputStream inStream = null;
        long bytes;
        try {
            inStream = request.getInputStream();
            for (bytes = 0; bytes < size; bytes++) {
                int value = inStream.read();
            }
        } finally {
            closeQuietly(inStream);
        }
        returnPlainText(response,"Read "+ bytes + " bytes; expected + " + size);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        disableCaching(response);
        response.setContentType(TEXT_HTML);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            out.println("<html>");
            out.println("<head><title>Upload</title></head>");
            out.println("<body><h1>Upload</h1>");
            out.println(" <form ><h2>Upload a file</h2>");
            out.println("  <p>");
            out.println("  <input type='file'></input>");
            out.println("  <p>");
            out.println("  <button type='submit'>submit</button>");
            out.println("  <p>");
            out.println(" </form>");
            out.println(" <form method='post'><h2>POST a file</h2>");
            out.println("  <p>");
            out.println("  <input type='file'></input>");
            out.println("  <p>");
            out.println("  <button type='submit'>submit</button>");
            out.println("  <p>");
            out.println(" </form>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            closeQuietly(out);
        }
    }
}
