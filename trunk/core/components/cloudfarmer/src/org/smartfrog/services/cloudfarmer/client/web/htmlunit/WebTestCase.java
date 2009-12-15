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
package org.smartfrog.services.cloudfarmer.client.web.htmlunit;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.smartfrog.services.junit.junit3.TestCaseWithContext;

import java.io.IOException;

/**
 * Base class for all web test cases; they take their URL from a system/context property
 *
 * These tests are expected to be deployed and run against different targets, hence they are included in the Main
 * distributable, and their pattern *TestCase is designed to not be picked up by the *Test pattern.
 */

public abstract class WebTestCase extends TestCaseWithContext {

    /**
     * {@value}
     */
    public static final String TEST_WEB_URL = "test.web.url";
    public static final String TEST_CLUSTER_URL = "test.cluster.url";
    public static final String TEST_WORKFLOW_URL = "test.workflow.url";
    protected static final String CLUSTER = "cluster/";
    protected static final String WORKFLOW = "workflow/";


    protected WebTestCase(String name) {
        super(name);
    }

    /**
     * Get a required property, asserts it is not null
     *
     * @param key key to look for
     * @return the value
     */
    public String getRequiredProperty(String key) {
        String s = getProperty(key, null);
        assertNotNull("No entry for " + key, s);
        return s;
    }

    public String getURL(String subpath) {
        return getURL(TEST_WEB_URL, subpath);
    }

    protected String getURL(String baseURL, String subpath) {
        String rootURL = getRequiredProperty(baseURL);
        StringBuilder url = new StringBuilder(rootURL.length() + subpath.length() + 1);
        url.append(rootURL);
        if (!rootURL.endsWith("/")) {
            url.append('/');
        }
        url.append(subpath);
        return url.toString();
    }

    protected String getClusterURL(String page) {
        return getURL(TEST_CLUSTER_URL, page);
    }

    protected String getWorkflowURL(String page) {
        return getURL(TEST_WORKFLOW_URL, page);
    }

    protected WebClient getWebClient() {
        return new WebClient();
    }

    protected IOException extractError(FailingHttpStatusCodeException e) {
        StringBuilder fullText = new StringBuilder();
        fullText.append(e.toString()).append("\n");
        fullText.append(e.getResponse().getContentAsString());
        IOException ioException = new IOException(fullText.toString(), e);
        return ioException;
    }

    protected HtmlAnchor getAnchorById(HtmlPage page, String id) {
        return (HtmlAnchor)page.getElementById(id);
    }
}
