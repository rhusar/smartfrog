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
package org.smartfrog.services.jetty.internal;

import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.Prim;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * this is a servlet handler which has extra features.
 * Created 12-Oct-2007 16:30:04.
 */

public class ExtendedServletHandler extends ServletHandler {
    private Log log;
    private Prim owner;

    public ExtendedServletHandler(final Prim owner, Log log) {
        this.log = log;
        this.owner = owner;
    }

    /**
     * Override the parent by discarding the error
     *
     * @param request in
     * @param response out
     * @throws IOException
     */
    @Override
    protected void notFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("404 \"" + request.getRequestURI() + "\""
                + " from " + request.getRemoteAddr());
        super.notFound(request, response);
    }


    @Override
    public String toString() {
        if (_string == null) {
            StringBuffer details = new StringBuffer("ExtendedServletHandler ");
            ServletHolder[] servlets = getServlets();
            if (servlets != null) {
                for (ServletHolder sh : servlets) {
                    details.append(sh.toString()).append("; ");
                }
            }
            _string = details.toString();
        }
        return _string;
    }

    /**
     * Customize a servlet.
     *
     * What we add here is a binding between the SF owner and the servlet, if that is supported
     * @param servlet the servlet to customise.
     * @return the customised servlet.
     * @throws Exception on any perceived problem.
     */
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public Servlet customizeServlet(Servlet servlet)
            throws Exception {
        if (servlet instanceof BindToOwner) {
            BindToOwner bind = (BindToOwner) servlet;
            bind.bindToOwner(owner);
        }
        return servlet;
    }


}
