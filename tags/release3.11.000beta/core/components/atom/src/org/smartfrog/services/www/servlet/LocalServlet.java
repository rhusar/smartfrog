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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is an interface that anything that is a local servlet must implement to handle incoming requests created
 * 18-Apr-2007 17:11:00
 */


public interface LocalServlet {

    /**
     * Get the last modified time
     *
     * @param request
     * @return time in seconds since last modified, or -1 for not known
     */
    long getLastModified(HttpServletRequest request);

    void doDelete(HttpServletRequest req, HttpServletResponse resp);

    void doPut(HttpServletRequest req, HttpServletResponse resp);

    void doPost(HttpServletRequest req, HttpServletResponse resp);

    void doGet(HttpServletRequest req, HttpServletResponse resp);
}
