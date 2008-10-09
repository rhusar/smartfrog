/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.components.other;

import org.apache.hadoop.util.MockService;
import org.apache.hadoop.util.Service;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.HadoopServiceImpl;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 09-Oct-2008 16:54:36
 */

public class MockServiceImpl extends HadoopServiceImpl implements HadoopCluster {

    public static final String ATTR_FAIL_ON_START = "failOnStart";
    public static final String ATTR_FAIL_ON_PING = "failOnPing";
    public static final String ATTR_FAIL_ON_CLOSE = "failOnClose";

    public MockServiceImpl() throws RemoteException {
    }


    /**
     * {@inheritDoc}
     *
     * @return the name of the Hadoop service deployed
     */
    @Override
    protected String getName() {
        return "MockService";
    }


    /**
     * Get the underlying job tracker
     *
     * @return the job tracker or null
     */
    public MockService getMockService() {
        return (MockService) getService();
    }

    /**
     * Create the specific service
     *
     * @param configuration configuration to use
     * @return the
     * @throws IOException
     * @throws SmartFrogException
     */
    protected Service createTheService(ManagedConfiguration configuration) throws IOException, SmartFrogException {
        MockService service = new MockService();
        service.setFailOnStart(configuration.getBoolean(ATTR_FAIL_ON_START,false));
        service.setFailOnPing(configuration.getBoolean(ATTR_FAIL_ON_PING, false));
        service.setFailOnClose(configuration.getBoolean(ATTR_FAIL_ON_CLOSE, false));
        return service;
    }

    /**
     * Start the service deployment in a new thread
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        createAndDeployService();
    }
}
