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
package org.smartfrog.services.atom.server;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** created 13-Apr-2007 12:33:16 */

public abstract class AbstractAtomServlet extends HttpServlet {
    private static final String APPLICATION_XML = "application/xml";
    private static final String ATOM_1_0 = "atom_1.0";

    /**
     * This is what subclasses must return a feed for a specific request
     * @param request incoming request
     * @return feed to serve up
     */
    protected abstract SyndFeed lookupFeed(HttpServletRequest request);

    /**
     * process a GET request
     *
     * @param request  request
     * @param response response
     * @throws ServletException if the feed doesn't work
     * @throws IOException      on IO problems
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            SyndFeed feed = lookupFeed(request);
            feed.setFeedType(ATOM_1_0);
            response.setContentType(APPLICATION_XML);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, response.getWriter());
        }
        catch (FeedException e) {
            //wrap and throw; let the stack handle it.
            ServletException se =
                    new ServletException("When creating feed", e);
            throw se;
        }
    }
}
