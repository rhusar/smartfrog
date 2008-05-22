/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.monitor.xmpp;

import org.smartfrog.services.xmpp.XmppListenerImpl;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.smartfrog.services.xmpp.MonitoringConstants;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

public class AvlXMPPListener extends XmppListenerImpl {
    private String hostname;

    public AvlXMPPListener() throws RemoteException {
        super();
    }




    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        sfLog().info("AvlXMPPListener started.");

        // send a notification to the avalanche server that the host started
        XMPPEventExtension ext = new XMPPEventExtension();
        ext.setHost(hostname);
        ext.setInstanceName("None");
        ext.setLastAction("None");
        ext.setMessageType(MonitoringConstants.HOST_STARTED);
        ext.setTimestamp(String.format("%d", Calendar.getInstance().getTimeInMillis()));
        ext.setMsg("Host started.");
        ext.setModuleId("None");
        ext.setModuleState("None");

        if (!sendMessage("avl@" + getServer(), "None", "AE", ext))
            sfLog().error("Error sending packet.");
    }

    /**
     * Determine the local hostname. If there is more than one port,
     * we use the network card that RMI is running on
     * @throws SmartFrogException failure to determine our hostname
     */
    private void determineHostname(boolean inIP) throws SmartFrogException {
        InetAddress localhost = SFProcess.sfDeployedHost();
        if (inIP)
            hostname = localhost.getHostAddress();
        else
            hostname = localhost.getHostName();
    }


    /**
     * Some classes may override this
     *
     * @throws SmartFrogException resolution problems
     * @throws RemoteException    network problems
     */
    @Override
    protected void readLoginAndPassword() throws SmartFrogException, RemoteException {

        setLogin(hostname);
        setPassword(hostname);
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        boolean bUseIP = sfResolve("useIpInsteadOfHostname", false, true);

        sfLog().info("AvlXMPPListener deployed.");
        determineHostname(bUseIP);
    }

    /**
     * Send a message out when we are terminating
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        // notify the avalanche server
        XMPPEventExtension ext = new XMPPEventExtension();
        ext.setHost(hostname);
        ext.setInstanceName("None");
        ext.setLastAction("None");
        ext.setMessageType(MonitoringConstants.HOST_STARTED);
        ext.setTimestamp(String.format("%d", Calendar.getInstance().getTimeInMillis()));
        ext.setMsg("Host going down.");
        ext.setModuleId("None");
        ext.setModuleState("None");

        sendMessage("avl@" +  getServer(), "None", "AE", ext);

        super.sfTerminateWith(status);
    }
}
