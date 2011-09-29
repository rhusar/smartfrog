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
package org.smartfrog.services.jetty.internal;

import org.mortbay.thread.ThreadPool;
import org.mortbay.thread.QueuedThreadPool;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.services.jetty.listeners.JettyConnector;

import java.rmi.RemoteException;

/**
 * Factory for thread pools under Jetty
 */

public final class ThreadPoolFactory {


    /**
     * Create a bounded thread pool from the various thread options.
     * @param owner through which references go
     * @return a thread pool with min/max threads set up
     * @throws SmartFrogResolutionException problems resolving things
     * @throws RemoteException              network trouble
     */
    public static QueuedThreadPool createThreadPool(Prim owner)  throws SmartFrogResolutionException, RemoteException {
        int threads = owner.sfResolve(JettyConnector.ATTR_THREADS, 0, true);
        int minT;
        int maxT;
        if (threads > 0) {
            minT = threads;
            maxT = threads;
        } else {
            minT = owner.sfResolve(JettyConnector.ATTR_MIN_THREADS, 1, true);
            maxT = owner.sfResolve(JettyConnector.ATTR_MAX_THREADS, 1, true);
        }
        QueuedThreadPool pool = new QueuedThreadPool();
        pool.setMinThreads(minT);
        pool.setMaxThreads(maxT);
        pool.setSpawnOrShrinkAt(owner.sfResolve(JettyConnector.ATTR_SPAWN_OR_SHRINK_AT, 0, true));
        pool.setName(owner.sfResolve(JettyConnector.ATTR_NAME, "", true));
        pool.setThreadsPriority(owner.sfResolve(JettyConnector.ATTR_PRIORITY, 0, true));
        pool.setMaxIdleTimeMs(owner.sfResolve(JettyConnector.ATTR_MAX_IDLE_TIME, 0, true));
        pool.setMaxIdleTimeMs(4);
        return pool;
    }
}
