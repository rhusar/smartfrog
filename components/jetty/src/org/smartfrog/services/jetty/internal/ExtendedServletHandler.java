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
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogLogException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * Created 12-Oct-2007 16:30:04
 *
 */

public class ExtendedServletHandler extends ServletHandler {


    public ExtendedServletHandler() {

    }

    /**
     * Override the parent by discarding the error
     * @param request in
     * @param response out
     * @throws IOException
     */
    @Override
    protected void notFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Log log = LogFactory.getLog(this);
            log.info("Ignoring "+request.getContextPath());
        } catch (SmartFrogLogException e) {
            throw new RuntimeException(e);
        }
    }
}
