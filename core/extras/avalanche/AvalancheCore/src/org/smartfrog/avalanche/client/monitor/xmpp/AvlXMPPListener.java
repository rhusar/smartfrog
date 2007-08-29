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

import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

public class AvlXMPPListener extends XmppListenerImpl {
    public AvlXMPPListener() throws RemoteException {
        super();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {

        // set the login and password
        String strHostname;
        try {
            strHostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new SmartFrogException("Stupid me, I don't know my own hostname!");
        }
        this.setLogin(strHostname);
        this.setPassword(strHostname);

        super.sfStart();

        sfLog().info("AvlXMPPListener started.");

        // send a notification to the avalanche server that the host started
        XMPPEventExtension ext = new XMPPEventExtension();
        ext.setHost(strHostname);
        ext.setInstanceName("None");
        ext.setLastAction("None");
        ext.setMessageType(MonitoringConstants.HOST_STARTED);
        ext.setTimestamp(String.format("%d", Calendar.getInstance().getTimeInMillis()));
        ext.setMsg("Host started.");
        ext.setModuleId("None");
        ext.setModuleState("None");

        if (!sendMessage("avl@" + this.getServer(), "None", "AE", ext))
            sfLog().error("Error sending packet.");
    }


    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        sfLog().info("AvlXMPPListener deployed.");
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        // notify the avlanache server
        XMPPEventExtension ext = new XMPPEventExtension();
        try {
            ext.setHost(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            ext.setHost("Stupid me, I don't know my own hostname!");
        }
        ext.setInstanceName("None");
        ext.setLastAction("None");
        ext.setMessageType(MonitoringConstants.HOST_STARTED);
        ext.setTimestamp(String.format("%d", Calendar.getInstance().getTimeInMillis()));
        ext.setMsg("Host going down.");
        ext.setModuleId("None");
        ext.setModuleState("None");

        sendMessage("avl@" + this.getServer(), "None", "AE", ext);

        super.sfTerminateWith(status);
    }
}
