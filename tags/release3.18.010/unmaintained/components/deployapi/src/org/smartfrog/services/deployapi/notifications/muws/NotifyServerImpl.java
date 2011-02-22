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
package org.smartfrog.services.deployapi.notifications.muws;

import org.smartfrog.services.deployapi.components.DeploymentServer;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * A store of muws event relays (weak references). When the creator goes away, so does the reference here.
 *
 * created 10-Oct-2006 15:34:02
 */

public class NotifyServerImpl extends PrimImpl implements NotifyServer {

    private int id=1;

    private HashMap<String, MuwsEventReceiver> entries =
            new HashMap<String, MuwsEventReceiver>();


    private static NotifyServerImpl singleton;
    private String protocol="http";
    private String hostname;
    private int port=80;
    private String baseurl;

    public NotifyServerImpl() throws RemoteException {
        //declare ourself as the sole singleton. This is a bit naughty, and its
        //because I havent implemented dynamic configuration of endpoints properly.
        singleton=this;
    }

    /**
     * Get the static singleton instance, unique for this process.
     * @return a process-specific singleton, or null, for 'none defined'.
     */
    public static NotifyServerImpl getSingleton() {
        return singleton;
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException
     *                                  failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        protocol = sfResolve(DeploymentServer.ATTR_PROTOCOL,
                protocol, false);
        hostname = sfResolve(DeploymentServer.ATTR_HOSTNAME,
                hostname, false);
        if (hostname==null || hostname.length() == 0) {
            hostname = Constants.LOCALHOST;
        }
        port = sfResolve(DeploymentServer.ATTR_PORT,
                port, true);
        String ctx = sfResolve(DeploymentServer.ATTR_CONTEXTPATH,
                Constants.CONTEXT_PATH, false);
        String servicespath = sfResolve(DeploymentServer.ATTR_SERVICESPATH,
                Constants.SERVICES_PATH, false);
        String notifypath = sfResolve(ATTR_NOTIFICATION_PATH,
                "", true);
        StringBuffer buff=new StringBuffer(256);
        buff.append(protocol);
        buff.append("://");
        buff.append(hostname);
        buff.append(':');
        buff.append(port);
        buff.append(ctx);
        buff.append(servicespath);
        buff.append(notifypath);
        baseurl=buff.toString();
    }

    public String getURL(String receiverID) {
        return baseurl+"?"+ NotifyServer.EVENT+"="+ receiverID;
    }

    /**
     * Create and register a new receiver
     * @return the new receiver
     */
    public MuwsEventReceiver createReceiver() {
        MuwsEventReceiver receiver=new MuwsEventReceiver(this);
        add(receiver);
        return receiver;
    }

    /**
     * Add a new receiver. an id will be assigned if there is none
     * @param receiver the receiver
     */
    public synchronized void add(MuwsEventReceiver receiver) {
        if(receiver.getId()==null) {
            int newid = id++;
            receiver.setId(Integer.toString(newid));
        }
        receiver.setURL(getURL(receiver.getId()));
        entries.put(receiver.getId(), receiver);
    }


    /**
     * look up a receiver from a key.
     * @param key key to look up
     * @return the receiver or null if there is no match
     */
    public MuwsEventReceiver lookup(String key) {
        if(key == null || key.length() == 0) {
            return null;
        }
        synchronized (this) {
            return entries.get(key);
        }
    }

    /**
     * Remove a specific receiver from the list
     * @param receiver
     */
    public synchronized void remove(MuwsEventReceiver receiver) {
        entries.remove(receiver.getId());
    }

}
