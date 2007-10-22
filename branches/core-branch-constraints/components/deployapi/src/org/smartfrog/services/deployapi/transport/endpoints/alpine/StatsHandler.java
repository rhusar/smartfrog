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
package org.smartfrog.services.deployapi.transport.endpoints.alpine;

import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.handlers.HandlerBase;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.services.deployapi.engine.ServerInstance;

/**
 * This class adds statistic gathering.
 * If there is a server instance running we log to it, otherwise we skip. Why would this endpoint be live
 * without a server? When we run on a client
 * created 02-May-2006 16:09:28
 */

public class StatsHandler extends HandlerBase {

    private int requests = 0;

    private int failures = 0;

    /**
     * Message handler
     *
     * @param messageContext message
     * @param endpointContext endpoint
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public void processMessage(MessageContext messageContext, EndpointContext endpointContext) {
        incrementRequests();
    }

    private void incrementRequests() {
        synchronized (this) {
            requests++;
        }
        ServerInstance serverInstance = ServerInstance.getServerInstanceOrNull();
        if (serverInstance != null) {
            serverInstance.incrementRequests();
        }
    }

    private void incrementFailures() {
        synchronized (this) {
            failures++;
        }
        ServerInstance serverInstance = ServerInstance.getServerInstanceOrNull();
        if(serverInstance!=null) {
            serverInstance.incrementFailures();
        }
    }

    /**
     * This is called for handlers when an exception gets thrown.
     * It tells them that something else in the chain faulted.
     * It is even thrown for their own {@link #processMessage} call,
     * which can be expected to set things up properly.
     * <p/>
     * It is not calles for handlers that did not (yet) get given the message to process. These
     * are never invoked if something upstream handles it.
     *
     * @param messageContext
     * @param endpointContext
     * @param fault
     * @return the same (or potentially a different) exception. This is the exception that will be passed up.
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *          if something went wrong internally. If this happens, it is logged to the
     *          INTERNAL_ERRORS category, and then discarded. The primary fault is what is sent up the wire, not
     *          something that went wrong during processing.
     */
    public synchronized Fault faultRaised(MessageContext messageContext, EndpointContext endpointContext, Fault fault) {
        incrementFailures();
        return super.faultRaised(messageContext, endpointContext, fault);
    }

    public synchronized void resetStatistics() {
        failures = 0;
        requests = 0;
    }

    public int getRequests() {
        return requests;
    }

    public int getFailures() {
        return failures;
    }
}
