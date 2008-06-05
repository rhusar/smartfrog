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
package org.smartfrog.sfcore.security;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 *
 * Created 05-Jun-2008 16:21:54
 *
 */

public class RequireSecurityImpl extends PrimImpl implements Condition {
    public static final String ATTR_REQUIRE_SECURE = "requireSecure";
    public static final String ATTR_REQUIRE_SECURE_RESOURCES = "requireSecureResources";
    public static final String ERROR_INSECURE_DAEMON = "The SmartFrog Daemon is insecure";
    public static final String ERROR_INSECURE_RESOURCE_LOADING
            = "Resource Loading is insecure";
    private boolean requireSecure;
    private boolean requireSecureResources;
    public static final String ATTR_CONDITION = "condition";

    public RequireSecurityImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        requireSecure = sfResolve(ATTR_REQUIRE_SECURE,true,true);
        requireSecureResources = sfResolve(ATTR_REQUIRE_SECURE_RESOURCES, true, true);
        boolean conditional= sfResolve(ATTR_CONDITION, true, true);
        if (!conditional) {
            if(requireSecure && !SFSecurity.isSecurityOn()) {
                throw new SmartFrogLifecycleException(ERROR_INSECURE_DAEMON);
            }
            if (requireSecureResources && SFSecurity.isSecureResourcesOff()) {
                throw new SmartFrogLifecycleException(ERROR_INSECURE_RESOURCE_LOADING);
            }
            new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,null,null,null);
        }
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        return (!requireSecure || SFSecurity.isSecurityOn())
                && (!requireSecureResources || !SFSecurity.isSecureResourcesOff());
    }
}
