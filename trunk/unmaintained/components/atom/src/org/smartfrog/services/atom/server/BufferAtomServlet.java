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
import com.sun.syndication.feed.synd.SyndFeedImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * This implementation of the servlet hosts a buffer of things to serve up, which
 * it proceeds to do whenever it has to
 * Created 15-Aug-2007 13:45:17
 *
 */

public class BufferAtomServlet extends AbstractAtomServlet {

    private SyndFeed feed=new SyndFeedImpl();

    /**
     * This is what subclasses must return a feed for a specific request
     *
     * @param request incoming request
     * @return feed to serve up
     */
    protected SyndFeed lookupFeed(HttpServletRequest request) {
        return feed;
    }

    /**
     * Return the single feed
     * @return the feed
     */
    public SyndFeed getFeed() {
        return feed;
    }
}
