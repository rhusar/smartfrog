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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.net.InetAddress;

/**
 * Created 03-Mar-2009 14:41:31
 */

public class LocalhostPrimImpl extends PrimImpl implements Prim {
    public static final String ATTR_HOSTNAME = "hostname";
    public static final String ATTR_ADDRESS = "address";
    public static final String ATTR_DEPLOYED_HOSTNAME = "deployedHostname";
    public static final String ATTR_DEPLOYED_HOST_ADDRESS = "deployedHostAddress";
    public static final String ATTR_TARGET = "target";

    public LocalhostPrimImpl() throws RemoteException {
    }

    /**
     * Add all our hostname attributes.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        String address = HostnameUtils.getLocalHostAddress();
        String hostname = HostnameUtils.getLocalHostname();
        Prim target=sfResolve(ATTR_TARGET,this,false);
        target.sfReplaceAttribute(ATTR_HOSTNAME,hostname);
        target.sfReplaceAttribute(ATTR_ADDRESS, address);
        InetAddress deployedHost = SFProcess.sfDeployedHost();
        target.sfReplaceAttribute(ATTR_DEPLOYED_HOSTNAME, deployedHost.getHostName());
        target.sfReplaceAttribute(ATTR_DEPLOYED_HOST_ADDRESS, deployedHost.getHostAddress());
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, null, null, null);
    }
}
