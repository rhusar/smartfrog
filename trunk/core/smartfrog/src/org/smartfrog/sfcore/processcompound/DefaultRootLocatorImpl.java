/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.processcompound;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.security.SFSecurity;



/**
 * Defines a default root locator for SmartFrog Processes. The root locator
 * knows how to set a process compound to be the root of a host, as well as
 * the method on how to get the root process compound on a given host and
 * port. This implementation uses the rmi registry to set the root process
 * compound in. Root Locators should not allow multiple process compounds to
 * set themselves as root.
 *
 */
public class DefaultRootLocatorImpl implements RootLocator, MessageKeys {
    /** Name under which the root process compound will name itself. */
    protected static String defaultName = "RootProcessCompound";

    /** Port for registry. */
    protected static int registryPort = -1;

    /**
     * Constructs the DefaultRootLocatorImpl object.
     */
    public DefaultRootLocatorImpl() {
    }

    /**
     * Gets the port of RMI registry on which input process compound is running.
     *
     * @param c Instance of process compound
     *
     * @return port number
     *
     * @throws SmartFrogException fails to get the registry port
     * @throws RemoteException In case of network/rmi error
     */
    protected static int getRegistryPort(ProcessCompound c)
        throws SmartFrogException, RemoteException {
        // TODO: check for class cast exception
        if (registryPort == -1) {
            //TODO: This is a troublespot. move to sfResolveID or otherwise fix.
            // (was sfResolve())
            Number port = ((Number) c.sfResolveId(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT));
            if(port==null) {
                throw new SmartFrogResolutionException("Unable to locate registry port from ",c);
            }
            registryPort = port.intValue();
        }

        return registryPort;
    }

    /**
     * Tries to make the requesting process compound the root of the entire
     * host. This might fail since another process compound has already done
     * this.
     *
     * @param c compound which wants to become root for machine
     *
     * @throws SmartFrogException could not create locator or bind compound
     * @throws RemoteException In case of network/rmi error
     *
     * @see #getRootProcessCompound
     */
    public void setRootProcessCompound(ProcessCompound c)
        throws SmartFrogException, RemoteException {
        // Read optional property 
        // "org.smartfrog.ProcessCompound.sfRootLocatorPort"
        String port = System.getProperty(SmartFrogCoreProperty.sfDaemonPort);
        // port defined in default.ini overrides the "sfRootLocatorPort"
        // attribute  defined in processcompound sf description
        if(port != null) { 
            registryPort = Integer.parseInt(port);
        } else {
            registryPort = getRegistryPort(c);
        }
        try {
            Registry reg = SFSecurity.createRegistry(registryPort);
            reg.bind(defaultName, c);
        } catch (Throwable t) {
            if (t instanceof java.rmi.server.ExportException){
                throw new SmartFrogRuntimeException ( MessageUtil.formatMessage(MSG_ERR_SF_RUNNING) , t);

            }
            throw SmartFrogRuntimeException.forward(t);
        }
    }

    /**
     * Gets the root process compound for a given host. If the passed host is
     * null the root process compound for the local host is looked up. Checks
     * if the local process compound is equal to the requested one, and
     * returns the local object instead of the stub to avoid all calls going
     * through RMI
     *
     * @param hostAddress host to look up root process compound
     *
     * @return the root process compound on given host
     *
     * @throws Exception if error locating root process compound on host
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress hostAddress)
        throws Exception {
        final ProcessCompound localCompound = SFProcess.getProcessCompound();

        if(localCompound == null) {
            throw new SmartFrogRuntimeException("No local process compound");
        }
        if (hostAddress == null) {
            hostAddress = InetAddress.getLocalHost();
        }

        if (hostAddress.equals(InetAddress.getLocalHost()) &&
                localCompound.sfIsRoot()) {
            return localCompound;
        }

        Registry reg = SFSecurity.getRegistry(hostAddress.getHostAddress(),
                getRegistryPort(localCompound));
        ProcessCompound pc = (ProcessCompound) reg.lookup(defaultName);

        // Get rid of the stub if local
        if (pc.equals(localCompound)) {
            return localCompound;
        }

        return pc;
    }

    /**
     * Gets the root process compound for a given host on a specified port. If
     * the passed host is null the root process compound for the local host is
     * looked up. Checks if the local process compound is equal to the
     * requested one, and returns the local object instead of the stub to
     * avoid all calls going through RMI
     *
     * @param hostAddress host to look up root process compound
     * @param portNum port to locate registry for root process conmpound if not
     *        default
     *
     * @return the root process compound on given host
     *
     * @throws Exception error locating root process compound on host
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress hostAddress,
        int portNum) throws Exception {
        ProcessCompound localCompound = SFProcess.getProcessCompound();

        if (hostAddress == null) {
            hostAddress = InetAddress.getLocalHost();
        }

        if ((localCompound != null) &&
                hostAddress.equals(InetAddress.getLocalHost()) &&
                localCompound.sfIsRoot()) {
            return localCompound;
        }

        Registry reg = SFSecurity.getRegistry(hostAddress.getHostAddress(),
                portNum);
        ProcessCompound pc = (ProcessCompound) reg.lookup(defaultName);

        // Get rid of the stub if local
        if ((localCompound != null) && pc.equals(localCompound)) {
            return localCompound;
        }

        return pc;
    }
}
