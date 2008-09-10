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

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 */
public class HostExistsImpl extends PrimImpl implements HostExists, Condition {

    private String host;
    private boolean checkOnStartup;
    private boolean checkOnLiveness;


    /**
     * constructor
     * @throws RemoteException in the superclass
     */
    public HostExistsImpl() throws RemoteException {
    }

    /**
     * Resolve any hostname. This is just a bridge to {@link
     * InetAddress#getByName(String)}. What it can do is be used to
     * check for differences in nslookup behavior across systems. Though when
     * that is happening, things may be going so badly that RMI itself can
     * collapse.
     *
     * @param hostname host to resolve
     *
     * @return the resolved hostname
     *
     * @throws UnknownHostException if it does not resolve
     */
    public InetAddress resolve(String hostname)
            throws UnknownHostException {
        return InetAddress.getByName(hostname);
    }

    /**
     * Resolve any hostname. This is just a bridge to {@link
     * InetAddress#getByName(String)}. What it can do is be used to
     * check for differences in nslookup behavior across systems. Though when
     * that is happening, things may be going so badly that RMI itself can
     * collapse.
     *
     * @param hostname host to resolve
     *
     * @return true iff the host exists as far as this process is concerned.
     *
     */
    public boolean hostExists(String hostname) {
        try {
            resolve(hostname);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException for RMI/Networking problems
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        host=sfResolve(ATTR_HOSTNAME,(String)null,false);
        checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, true, true);
        checkOnLiveness = sfResolve(ATTR_CHECK_ON_LIVENESS, true, true);
        if(checkOnStartup && host!=null && !hostExists(host)) {
            throw new SmartFrogDeploymentException("Unknown host "+host);
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(
                TerminationRecord.NORMAL,
                "HostExists", this.sfCompleteNameSafe(), null
                );
    }


    /**
     * Check if this component is still alive.
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (checkOnLiveness && host != null && !hostExists(host)) {
            throw new SmartFrogLivenessException("Unknown host " + host);
        }
    }


    /**
     * check for the host existing.
     *
     * @return true if it is successful, false if not
     * @throws SmartFrogException for deployment problems
     * @throws RemoteException for RMI/Networking problems
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        return hostExists(host);
    }
}
