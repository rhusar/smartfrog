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
package org.smartfrog.services.jetty.listeners;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.thread.QueuedThreadPool;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Jetty6 has a new select channel connector; this is it
 * Created 11-Oct-2007 12:14:08
 *
 */

public class SelectChannelConnectorImpl extends AbstractConnectorImpl implements JettySocketConnector {


    public SelectChannelConnectorImpl() throws RemoteException {
    }

    public SelectChannelConnector getSelectChannelConnector() {
        return (SelectChannelConnector) getConnector();
    }

    protected void configureConnector() throws SmartFrogException, RemoteException {
        SelectChannelConnector channel = getSelectChannelConnector();

        setMaxIdleTime(channel);
        bindConnectorToPortAndHost(channel);
        // set up all the threads;
        QueuedThreadPool pool = createBoundedThreadPool();
        channel.setThreadPool(pool);
        channel.setUseDirectBuffers(sfResolve(ATTR_USE_DIRECT_BUFFERS,true,true));
        channel.setSoLingerTime(sfResolve(ATTR_SOCKET_LINGER_TIME, -1, true));
        channel.setHeaderBufferSize(sfResolve(ATTR_HEADER_BUFFER_SIZE,0,true));
        channel.setRequestBufferSize(sfResolve(ATTR_REQUEST_BUFFER_SIZE, 0, true));
        channel.setResponseBufferSize(sfResolve(ATTR_RESPONSE_BUFFER_SIZE, 0, true));

    }

    protected Connector createConnector() throws SmartFrogException, RemoteException {
        return new SelectChannelConnector();
    }
}
