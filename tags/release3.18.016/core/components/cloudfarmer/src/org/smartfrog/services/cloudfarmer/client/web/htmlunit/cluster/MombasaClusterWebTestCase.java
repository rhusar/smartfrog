/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.htmlunit.cluster;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.smartfrog.services.cloudfarmer.client.web.htmlunit.WebTestCase;

import java.io.IOException;

/**
 * Created 30-Nov-2009 14:16:41
 */

public class MombasaClusterWebTestCase extends WebTestCase implements ClusterAnchors {
    private static final String ANCHOR_WORKFLOW_LIST = "workflowList";

    public MombasaClusterWebTestCase(String name) {
        super(name);
    }

    public void testRootPageListHosts() throws Throwable {
        HtmlAnchor anchor = getAnchorById(getRootPage(), LIST_HOSTS);
    }

    public void testRootPageListRoles() throws Throwable {
        HtmlAnchor anchor = getAnchorById(getRootPage(), LIST_ROLES);
    }

    public void testRootPageAdd() throws Throwable {
        HtmlAnchor anchor = getAnchorById(getRootPage(),ADD_HOST);
    }

    protected HtmlPage getRootPage() throws IOException {
        try {
            WebClient browser = getWebClient();
            HtmlPage clusterPage = browser.getPage(getClusterURL("view.do"));
            return clusterPage;
        } catch (FailingHttpStatusCodeException e) {
            IOException ioException = extractError(e);
            throw ioException;
        }
    }

}
