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
package org.smartfrog.services.deployapi.alpineclient.model;

import org.smartfrog.projects.alpine.transport.Session;
import org.smartfrog.projects.alpine.transport.TransmitQueue;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;

import java.util.concurrent.Executor;

/**
 * created 10-Apr-2006 17:04:23
 */

public class ServerSession extends Session {

    /**
     * Creates a client endpoint with a queue bound to the executor
     *
     * @param endpoint   destination
     * @param validating validating of messages?
     * @param executor   queue executor
     */
    public ServerSession(AlpineEPR endpoint, boolean validating, Executor executor) {
        super(endpoint, null, validating);
        setQueue(new TransmitQueue(executor));
    }

    public PortalSession createPortalSession() {
        return new PortalSession(getEndpoint(), isValidating(), getQueue());
    }

    public SystemSession createSystemSession(AlpineEPR system) {
        return new SystemSession(system, isValidating(), getQueue());
    }
}
