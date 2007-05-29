/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** created 18-Apr-2007 17:11:44 */

public class RelayServlet extends HttpServlet {

    /**
     * do the mapping from a request to a target
     *
     * @param request
     * @return
     */
    LocalServlet getTarget(HttpServletRequest request) {
        return null;
    }


    /**
     * Returns the time the <code>HttpServletRequest</code> object was last modified, in milliseconds since midnight
     * January 1, 1970 GMT. If the time is unknown, this method returns a negative number (the default).
     *
     * <p>Servlets that support HTTP GET requests and can quickly determine their last modification time should override
     * this method. This makes browser and proxy caches work more effectively, reducing the load on server and network
     * resources.
     *
     * @param req the <code>HttpServletRequest</code> object that is sent to the servlet
     * @return a <code>long</code> integer specifying the time the <code>HttpServletRequest</code> object was last
     *         modified, in milliseconds since midnight, January 1, 1970 GMT, or -1 if the time is not known
     */

    protected long getLastModified(HttpServletRequest req) {
        return getTarget(req).getLastModified(req);
    }


    /**
     * Called by the server (via the <code>service</code> method) to allow a servlet to handle a GET request.
     *
     * <p>Overriding this method to support a GET request also automatically supports an HTTP HEAD request. A HEAD
     * request is a GET request that returns no body in the response, only the request header fields.
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     * @throws ServletException if the request for the GET could not be handled
     * @see ServletResponse#setContentType
     */

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getTarget(req).doGet(req, resp);
    }


    /**
     * Called by the server (via the <code>service</code> method) to allow a servlet to handle a POST request.
     *
     * The HTTP POST method allows the client to send data of unlimited length to the Web server a single time and is
     * useful when posting information such as credit card numbers.
     *
     * <p>When overriding this method, read the request data, write the response headers, get the response's writer or
     * output stream object, and finally, write the response data. It's best to include content type and encoding. When
     * using a <code>PrintWriter</code> object to return the response, set the content type before accessing the
     * <code>PrintWriter</code> object.
     *
     * <p>The servlet container must write the headers before committing the response, because in HTTP the headers must
     * be sent before the response body.
     *
     * <p>Where possible, set the Content-Length header (with the {@link ServletResponse#setContentLength} method), to
     * allow the servlet container to use a persistent connection to return its response to the client, improving
     * performance. The content length is automatically set if the entire response fits inside the response buffer.
     *
     * <p>When using HTTP 1.1 chunked encoding (which means that the response has a Transfer-Encoding header), do not
     * set the Content-Length header.
     *
     * <p>This method does not need to be either safe or idempotent. Operations requested through POST can have side
     * effects for which the user can be held accountable, for example, updating stored data or buying items online.
     *
     * <p>If the HTTP POST request is incorrectly formatted, <code>doPost</code> returns an HTTP "Bad Request" message.
     *
     * @param req  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException      if an input or output error is detected when the servlet handles the request
     * @throws ServletException if the request for the POST could not be handled
     * @see ServletOutputStream
     * @see ServletResponse#setContentType
     */

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getTarget(req).doPost(req, resp);
    }


    /**
     * Called by the server (via the <code>service</code> method) to allow a servlet to handle a PUT request.
     *
     * The PUT operation allows a client to place a file on the server and is similar to sending a file by FTP.
     *
     * <p>When overriding this method, leave intact any content headers sent with the request (including Content-Length,
     * Content-Type, Content-Transfer-Encoding, Content-Encoding, Content-Base, Content-Language, Content-Location,
     * Content-MD5, and Content-Range). If your method cannot handle a content header, it must issue an error message
     * (HTTP 501 - Not Implemented) and discard the request. For more information on HTTP 1.1, see RFC 2068 <a
     * href="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc2068.txt"></a>.
     *
     * <p>This method does not need to be either safe or idempotent. Operations that <code>doPut</code> performs can
     * have side effects for which the user can be held accountable. When using this method, it may be useful to save a
     * copy of the affected URL in temporary storage.
     *
     * <p>If the HTTP PUT request is incorrectly formatted, <code>doPut</code> returns an HTTP "Bad Request" message.
     *
     * @param req  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param resp the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     * @throws IOException      if an input or output error occurs while the servlet is handling the PUT request
     * @throws ServletException if the request for the PUT cannot be handled
     */

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getTarget(req).doPut(req, resp);
    }


    /**
     * Called by the server (via the <code>service</code> method) to allow a servlet to handle a DELETE request.
     *
     * The DELETE operation allows a client to remove a document or Web page from the server.
     *
     * <p>This method does not need to be either safe or idempotent. Operations requested through DELETE can have side
     * effects for which users can be held accountable. When using this method, it may be useful to save a copy of the
     * affected URL in temporary storage.
     *
     * <p>If the HTTP DELETE request is incorrectly formatted, <code>doDelete</code> returns an HTTP "Bad Request"
     * message.
     *
     * @param req  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param resp the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     * @throws IOException      if an input or output error occurs while the servlet is handling the DELETE request
     * @throws ServletException if the request for the DELETE cannot be handled
     */

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getTarget(req).doDelete(req, resp);
    }
}
