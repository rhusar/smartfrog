/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.test.system.alpine;

import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.projects.alpine.transport.DirectExecutor;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.services.deployapi.alpineclient.model.PortalSession;

/**
 * created 11-Apr-2006 14:57:19
 */

public abstract class AlpineTestBase extends TestCase {


    private AlpineEPR portalEPR;
    private  PortalSession portalSession;
    public static final String ENDPOINT_PROPERTY = "endpoint";
    private boolean validating = false;
    private boolean concurrent=false;
    public static final String CONCURRENT_PROPERTY = "concurrent";
    public static final String VALIDATING_PROPERTY = "validating";


    /**
     * Constructs a test case with the given name.
     */
    protected AlpineTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        String target = getJunitParameter(ENDPOINT_PROPERTY,true);
        bindToPortal(target);
    }

    /**
     * Get a junit parameter. Fail if it is missing and required=true
     * @param property property name
     * @return the property or null for not found
     */
    protected String getJunitParameter(String property,boolean required) {
        String target = System.getProperty(property);
        if (required && target == null) {
            fail("No property " + property);
        }
        return target;
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * bind to a target; set up the endpoint, endpointURL and epr fields from the target URL
     *
     * @param target
     * @throws java.net.MalformedURLException
     */
    protected void bindToPortal(String target) throws MalformedURLException {
        portalEPR = new AlpineEPR(target);
        concurrent = getBoolParameter(CONCURRENT_PROPERTY);
        validating= getBoolParameter(VALIDATING_PROPERTY);
        final Executor executor = createExecutor();
        portalSession=new PortalSession(portalEPR,validating, new TransmitQueue(executor));
    }

    private Boolean getBoolParameter(String property) {
        return Boolean.valueOf(getJunitParameter(property,false));
    }

    public AlpineEPR getPortalEPR() {
        return portalEPR;
    }

    public PortalSession getPortalSession() {
        return portalSession;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    /**
     * Override point: create the executor for this project.
     * @return a direct or concurrent executor.
     */
    protected Executor createExecutor() {
        return concurrent?
                createConcurrentExecutor() :
                new DirectExecutor();
    }

    /**
     * override point; create an executor for concurrent execution
     * defaults to {@link java.util.concurrent.Executors#newSingleThreadExecutor()}
     * @return a concurrent executor
     */
    protected ExecutorService createConcurrentExecutor() {
        return Executors.newSingleThreadExecutor();
    }

}
