/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

import java.rmi.Remote;

/**
 * An interface for listeners for jetty server
 *
 * @author Ritu Sabharwal
 */

public interface JettyConnector extends Remote {
    /**
     * port to listen to {@value}
     */
    String LISTENER_PORT = "port";
    /**
     * attribute name: {@value}
     */
    String SERVER_HOST = "host";

    /**
     * attribute name: {@value}
     */
    String SERVER_NAME = "name";

    /**
     * number of threads to accept requests. {@value}
     */
    String ATTR_THREADS = "threads";

    /**
     * number of threads to accept requests. {@value}
     */
    String ATTR_MAX_THREADS = "maxThreads";

    /**
     * number of threads to accept requests. {@value}
     */
    String ATTR_MIN_THREADS = "minThreads";

    /** {@value} */
    String ATTR_SPAWN_OR_SHRINK_AT = "spawnOrShrinkAt";
    /** {@value} */
    String ATTR_NAME = "name";
    /** {@value} */
    String ATTR_PRIORITY = "priority";

    /**
     * Max time (millis) to wait for a socket <p/> {@value}
     */
    String ATTR_MAX_IDLE_TIME = "maxIdleTime";
    /** {@value} */
    String ATTR_USE_DIRECT_BUFFERS = "useDirectBuffers";
    /** {@value} */
    String ATTR_SOCKET_LINGER_TIME = "socketLingerTime";
    /** {@value} */
    String ATTR_RESPONSE_BUFFER_SIZE = "responseBufferSize";
    /** {@value} */
    String ATTR_HEADER_BUFFER_SIZE = "headerBufferSize";
    /** {@value} */
    String ATTR_REQUEST_BUFFER_SIZE = "requestBufferSize";
    /** {@value} */
    String ATTR_ACCEPTORS = "acceptors";

    /**
     * restrict/control IP settings : {@value}
     */
    String ATTR_ALLOW_IPV4 = "allowIPv4";
    /**
     * restrict/control IP settings : {@value}
     */
    String ATTR_ALLOW_IPV6 = "allowIPv6";
    
}
