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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.www.HttpAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet base for bulk IO. This servlet is designed to live outside SmartFrog; logs through commons-logging Created
 * 17-May-2010 15:02:27
 */

public abstract class AbstractBulkioServlet extends HttpServlet {

    public static final String ATTR_LIMIT = "limit";
    public static final String ATTR_SIZE = "size";
    public static final String ATTR_FORMAT = "format";
    public static final String FORMAT_BINARY = "application/binary";

    protected static final Log log = LogFactory.getLog(AbstractBulkioServlet.class);
    protected static final String TEXT_HTML = "text/html";
    protected static final String TEXT_PLAIN = "text/plain";

    protected long getLimit() {
        return Long.valueOf(getInitParameter(ATTR_LIMIT));
    }

    protected void disableCaching(HttpServletResponse response) {
        response.addHeader("Cache-Control","none");
        response.addHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
    }

    /**
    * Get the request size
    *
    * @param request request
    * @return the value or -1 for no value supplied.
    * @throws ServletException if the parse failed
    */
    protected long getSize(HttpServletRequest request) throws ServletException {
        return getParameterAsLong(request, ATTR_SIZE);
    }

    protected long getContentLength(HttpServletRequest request) throws ServletException {
        String contentL = request.getHeader(HttpAttributes.HEADER_CONTENT_LENGTH);
        return contentL == null ? -1 :
                parseToLong(" the header " + HttpAttributes.HEADER_CONTENT_LENGTH, contentL);
    }

    /**
     * Get some parameter as long
     *
     * @param request
     * @param param
     * @return
     * @throws ServletException
     */
    protected long getParameterAsLong(HttpServletRequest request, String param) throws ServletException {
        return parseToLong(" the parameter " + ATTR_SIZE, request.getParameter(param));
    }

    protected long parseToLong(String source, String longValue) throws ServletException {
        try {
            return longValue == null ? -1 : Long.valueOf(longValue);
        } catch (NumberFormatException e) {
            throw new ServletException("Failed to parse the value of " +
                    source
                    + " to a long :\"" + longValue + "\"" + e, e);
        }
    }

    protected void returnPlainText(HttpServletResponse response, String message) throws IOException {
        response.setContentType(TEXT_PLAIN);
        PrintWriter writer = response.getWriter();
        try {
            writer.append(message);
        } finally {
            closeQuietly(writer);
        }
    }

    /**
     * Close quietly, log on an exception, do nothing on null
     *
     * @param c thing to close
     */
    protected void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                log.warn(e);
            }
        }
    }
}
