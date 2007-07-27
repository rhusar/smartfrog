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
package org.smartfrog.services.www.diagnostics;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * Component to look for hosts
 */
public interface HostExists extends Remote {
    String ATTR_HOSTNAME="hostname";
    String ATTR_CHECK_ON_STARTUP="checkOnStartup";
    String ATTR_CHECK_ON_LIVENESS="checkOnLiveness";

    /**
     * Resolve any hostname. This is just a bridge to
     * {@link InetAddress#getByName(String)}. What it can do is be used to check
     * for differences in nslookup behavior across systems. Though when that is
     * happening, things may be going so badly that RMI itself can collapse.
     * @param hostname host to resolve
     * @return the resolved hostname
     * @throws UnknownHostException if it does not resolve
     * @throws RemoteException for RMI problems
     */
    InetAddress resolve(String hostname) throws UnknownHostException,
            RemoteException;

    /**
     * Resolve any hostname. This is just a bridge to
     * {@link InetAddress#getByName(String)}. What it can do is be used to check
     * for differences in nslookup behavior across systems. Though when that is
     * happening, things may be going so badly that RMI itself can collapse.
     * @param hostname host to resolve
     * @return true iff the host exists as far as this process is concerned.
     * @throws RemoteException for RMI problems
     */
    boolean hostExists(String hostname) throws RemoteException;
}
