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
package org.smartfrog.services.junit.junit3;

import junit.framework.TestCase;
import org.smartfrog.services.xunit.base.TestContextInjector;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This is a test case with context. 
 * 
 * This design is intended to work inside SmartFrog or outside; it creates a stub context which can be overwritten
 * by {@link TestContextInjector#setTestContext(HashMap)} when the SF test runner forces a context
 */

public abstract class TestCaseWithContext extends TestCase implements TestContextInjector {

    protected Map<String, Object> testContext = new HashMap<String, Object>();
    protected boolean contextSet;

    protected Properties properties = new Properties();

    protected TestCaseWithContext(String name) {
        super(name);
    }

    /**
     * Get the test context
     *
     * @return the current test context or null
     */
    public Map<String, Object> getTestContext() {
        return testContext;
    }

    /**
     * {@inheritDoc}
     *
     * @param testContext the test context
     */
    @Override
    public void setTestContext(HashMap<String, Object> testContext) {
        this.testContext = testContext;
        contextSet = testContext != null;
        //set the properties, with a bit of contingency planning
        if(contextSet) {
            properties = (Properties) getContextEntry(TestContextInjector.ATTR_PROPERTIES);
        }
        if (properties == null) {
            properties = new Properties();
        }
    }

    /**
     * Get any entry in the test case context
     *
     * @param key key to search for
     * @return the entry or null for no match
     */
    public Object getContextEntry(String key) {
        return testContext.get(key);
    }

    /**
     * Get a string property. First the context properties used, then the System properties
     *
     * @param key    key to look for
     * @param defVal default value
     * @return a discovered property, or the default values
     */
    public String getProperty(String key, String defVal) {
        String result = properties.getProperty(key, null);
        return result != null ? result :
                System.getProperty(key, defVal);
    }

    /**
     * Set a property
     *
     * @param key   property key
     * @param value value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
