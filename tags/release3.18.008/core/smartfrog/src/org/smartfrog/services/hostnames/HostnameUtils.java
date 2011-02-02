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
package org.smartfrog.services.hostnames;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created 03-Mar-2009 12:19:36
 */

public class HostnameUtils {

    private static InetAddress localHost = null;
    public static final String LOCALHOST = "localhost";
    public static final String LOOPBACK = "127.0.0.1";

    protected HostnameUtils() {
    }


    /**
     * Get a cached copy of the local host. If we cannot determine our hostname (it happens), fall back
     * to using {@link #LOCALHOST} as the name, and look that up
     * @see InetAddress#getLocalHost()
     * @return the host address
     * @throws UnknownHostException if we cannot even resolve the address of "localhost"
     */
    public static synchronized InetAddress getLocalHost() throws UnknownHostException {
        if (localHost == null) {
            try {
                localHost = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                localHost = InetAddress.getByName(LOCALHOST);
            }
        }
        return localHost;
    }

    /**
     * Get the local hostname. If there is a failure to do this, return {@link #LOCALHOST}
     * @return a hostname
     */
    public static synchronized String getLocalHostname() {
        try {
            return getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
            return LOCALHOST;
        }
    }

    /**
     * Get the dotted IP address of the local host,
     * @see InetAddress#getHostAddress()
     * @return the host address as a string
     */
    public static synchronized String getLocalHostAddress() {
        try {
            return getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
            return LOOPBACK;
        }
    }
}
