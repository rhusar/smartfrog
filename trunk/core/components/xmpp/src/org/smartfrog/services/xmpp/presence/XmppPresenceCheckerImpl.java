/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp.presence;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.smartfrog.services.xmpp.XmppListenerImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Created 12-Feb-2008 12:21:59
 */

public class XmppPresenceCheckerImpl extends XmppListenerImpl
        implements XmppPresenceChecker, Condition, RosterListener {

    private String target;
    private int startupDelay;
    private long connectDelay;
    private boolean checkOnLiveness;
    public static final String ERROR_NOT_PRESENT = "Not present :";

    public XmppPresenceCheckerImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        target = sfResolve(ATTR_TARGET, "", true);
        startupDelay = sfResolve(ATTR_DELAY, 0, true);
        checkOnLiveness = sfResolve(ATTR_CHECK_ON_LIVENESS, false, true);
        setConnectDelay();
        super.sfStart();
    }

    /**
     * Override point: configure the roster of this connection.
     *
     * @param connection connection to configure
     */
    @Override
    protected void configureRoster(XMPPConnection connection) {
        super.configureRoster(connection);
        setConnectDelay();
        //listen to the roster
        Roster roster = connection.getRoster();
        roster.addRosterListener(this);
        roster.reload();
    }

    /**
     * Get the current roster
     *
     * @return the roster; will be null if not connected
     */
    protected Roster getRoster() {
        return getConnection() != null ? getConnection().getRoster() : null;
    }

    private void setConnectDelay() {
        connectDelay = System.currentTimeMillis() + startupDelay;
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        if (target.length() == 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (!isConnected()) {
            return true;
        } else {
            Presence presence = getRoster().getPresence(target);
            boolean absent;
            absent = presence == null || !(presence.getType().equals(Presence.Type.AVAILABLE));
            return !absent || now < connectDelay;
        }
    }

    /**
     * Override point.
     *
     * @throws SmartFrogLivenessException if the connection is down
     * @see #checkConnection()
     */
    @Override
    protected void innerPing() throws SmartFrogLivenessException {
        super.innerPing();
        //now check the availability of the remote person
        try {
            if (checkOnLiveness && !evaluate()) {
                throw new SmartFrogLivenessException(ERROR_NOT_PRESENT + target);
            }
        } catch (RemoteException e) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
        } catch (SmartFrogException e) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
        }
    }

    /**
     * Called when roster entries are added.
     *
     * @param addresses the XMPP addresses of the contacts that have been added to the roster.
     */
    public void entriesAdded(Collection addresses) {

    }

    /**
     * Called when a roster entries are updated.
     *
     * @param addresses the XMPP addresses of the contacts whose entries have been updated.
     */
    public void entriesUpdated(Collection addresses) {

    }

    /**
     * Called when a roster entries are removed.
     *
     * @param addresses the XMPP addresses of the contacts that have been removed from the roster.
     */
    public void entriesDeleted(Collection addresses) {

    }

    /**
     * Called when the presence of a roster entry is changed.
     *
     * @param XMPPAddress the XMPP address of the user who's presence has changed, including the resource.
     */
    public void presenceChanged(String XMPPAddress) {

    }
}
