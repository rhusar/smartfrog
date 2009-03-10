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
package org.smartfrog.services.utils.security;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.security.ExitTrappingSecurityManager;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * Created 10-Mar-2009 12:18:11
 */

public class CheckSecurityManagerLiveImpl extends PrimImpl {

    public static final String ATTR_REQUIRE_EXIT_TRAPPING = "requireExitTrapping";
    public static final String ATTR_REQUIRE_SECURITY_MANAGER = "requireSecurityManager";
    public static final String ATTR_TEST_SYSTEM_EXIT = "testSystemExit";

    private boolean testSystemExit, requireExitTrapping, requireSecurityManager;

    public CheckSecurityManagerLiveImpl() throws RemoteException {
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
        super.sfStart();
        requireSecurityManager = sfResolve(ATTR_REQUIRE_SECURITY_MANAGER, true, true);
        requireExitTrapping = sfResolve(ATTR_REQUIRE_EXIT_TRAPPING, true, true);
        testSystemExit = sfResolve(ATTR_TEST_SYSTEM_EXIT, true, true);
        SecurityManager current = System.getSecurityManager();

        if (requireSecurityManager && current == null) {
            throw new SmartFrogDeploymentException("No security manager installed");
        }
        sfLog().info("Current security manager " + current);
        if (requireExitTrapping && !ExitTrappingSecurityManager.isSecurityManagerRunning()) {
            throw new SmartFrogDeploymentException(
                    "Security manager " + current + " is not a SmartFrog exit trapping manager");
        }
        if (testSystemExit) {
            try {
                System.exit(-1);
            } catch (Throwable t) {
                sfLog().info("Exit call intercepted ", t);
            }
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate("normal", "Security Manager is "+current, 
                sfCompleteNameSafe(), 
                null);
    }

}
