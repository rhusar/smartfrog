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

import junit.framework.TestCase;
import org.smartfrog.services.xunit.base.TestContextInjector;
import org.smartfrog.services.junit.junit3.TestCaseWithContext;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Base class for all web test cases; they take their URL from a system/context property
 * 
 * These tests are expected to be deployed and run against different targets, hence they are included in the
 * Main distributable, and their pattern *TestCase is designed to not be picked up by the *Test pattern.
 */

public abstract class WebTestCase extends TestCaseWithContext {
    
    /** {@value} */
    public static final String TEST_WEB_URL ="test.web.url";
    
    
    protected WebTestCase(String name) {
        super(name);
    }

    /**
     * Get a required property, asserts it is not null
     * @param key key to look for 
     * @return the value
     */
    public String getRequiredProperty(String key) {
        String s = getProperty(key, null);
        assertNotNull("No entry for " + key, s);
        return s;
    }

    protected String getClusterURL() {
        return getRequiredProperty(TEST_WEB_URL) + "/cluster";
    }

    protected String getWorkflowURL() {
        return getRequiredProperty(TEST_WEB_URL) + "/workflow";
    }

    protected WebClient getWebClient() {
        return new WebClient();
    }
}
