/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.jetty.test.system.bulkio;

import org.smartfrog.services.jetty.examples.JettyTestPorts;
import org.smartfrog.test.PortCheckingTestBase;

/**
 * Created 20-May-2010 12:46:43
 */

public abstract class BaseBulkIoTestCase 
        extends PortCheckingTestBase implements JettyTestPorts {
    public static final String BULKIO = "/org/smartfrog/services/www/jetty/test/system/bulkio/";
    public static final String TEST_BULKIO_SIZE = "test.bulkio.size";
    public static final long MB = 1;
    public static final long GB = 1024L * MB;
    public static long SIZE_8MB = 8 * MB;
    public static long SIZE_128MB = 128 * MB;
    public static long LARGE_1GB = 1 * GB;
    public static long LARGE_3GB = 3 * GB;
    public static long LARGE_4GB = 4 * GB;
    public static long LARGE_5GB = 5L * GB;

    public BaseBulkIoTestCase(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        enableFailOnPortCheck();
    }

    public void expectBulkIoRun(String filename, long size) throws Throwable {
        System.setProperty(TEST_BULKIO_SIZE, Long.toString(size));
        addPortCheck("Jetty 1", TEST_JETTY_PORT_1);
        expectSuccessfulTestRun(BULKIO, filename);
    }

    protected abstract String getFile();
}