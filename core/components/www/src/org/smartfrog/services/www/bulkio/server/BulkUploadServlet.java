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

import org.smartfrog.services.www.HttpHeaders;
import org.smartfrog.services.www.bulkio.IoAttributes;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * Created 17-May-2010 15:03:10
 */

public class BulkUploadServlet extends AbstractBulkioServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CRC32 checksum = new CRC32();
        long size = -1;
        // size = getSize(request);
        long contentLength = getContentLength(request);
        String type = request.getContentType();
        
        getLog().info(request.getMethod() + " operation receiving " + contentLength + " bytes of type " + type);
        if (size >= 0 && size != contentLength) {
            throw new ServletException(
                    "Content-Length header of " + contentLength + " does not equal the size parameter " + size);
        }
        ServletInputStream inStream = null;
        long bytes;
        try {
            inStream = request.getInputStream();
            for (bytes = 0; bytes < contentLength; bytes++) {
                int octet = inStream.read();
                checksum.update(octet);
            }
        } finally {
            closeQuietly(inStream);
        }
        long checksumValue = checksum.getValue();
        getLog().info("Checksum : " + checksumValue);
        String summary = "#summary\n"
                + IoAttributes.BYTES_READ + "=" + bytes + "\n"
                + IoAttributes.BYTES_EXPECTED + "=" + size + "\n"
                + IoAttributes.CONTENT_LENGTH + "=" + contentLength + "\n"
                + IoAttributes.CONTENT_TYPE + "=" + type + "\n"
                + IoAttributes.CHECKSUM + "=" + checksumValue + "\n";
        getLog().info(summary);
        returnPlainText(response,summary);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 //       disableCaching(response);
        response.setContentType(HttpHeaders.TEXT_HTML);
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
