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
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.security.ExitTrappingSecurityManager;
import org.smartfrog.sfcore.security.DummySecurityManager;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.security.Policy;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.PropertyPermission;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Created 10-Mar-2009 12:18:11
 */

public class CheckSecurityManagerLiveImpl extends PrimImpl {

    public static final String ATTR_REQUIRE_EXIT_TRAPPING = "requireExitTrapping";
    public static final String ATTR_REQUIRE_SECURITY_MANAGER = "requireSecurityManager";
    public static final String ATTR_TEST_SYSTEM_EXIT = "testSystemExit";
    public static final String ATTR_SECURITY_MANAGER_TO_STRING = "securityManagerToString";
    public static final String ATTR_SECURITY_MANAGER_CLASSNAME = "securityManagerClassname";
    public static final String ATTR_SECURITY_MANAGER_FOUND = "securityManagerFound";

    public static final String ATTR_PRINT_POLICY_INFO = "printPolicyInfo";
    public static final String ATTR_ASSERT_POLICY_CAN_ADD_PERMISSIONS = "assertPolicyCanAddPermissions";

    public static final String ERROR_NOT_EXIT_TRAPPING = "Security manager is not a SmartFrog exit-trapping manager";
    public static final String ERROR_NO_SECURITY_MANAGER = "No security manager installed";
    private boolean assertPolicyCanAddPermissions;
    private CodeSource source;
    public static final String ERROR_PERMISSION_ADD_FAILED = "Failed to add a new permission.";

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
        boolean requireSecurityManager = sfResolve(ATTR_REQUIRE_SECURITY_MANAGER, true, true);
        boolean requireExitTrapping = sfResolve(ATTR_REQUIRE_EXIT_TRAPPING, true, true);
        boolean testSystemExit = sfResolve(ATTR_TEST_SYSTEM_EXIT, true, true);
        boolean printPolicyInfo = sfResolve(ATTR_PRINT_POLICY_INFO, true, true);
        assertPolicyCanAddPermissions = sfResolve(ATTR_ASSERT_POLICY_CAN_ADD_PERMISSIONS, true, true);
        SecurityManager manager = System.getSecurityManager();

        boolean hasManager = manager != null;
        sfReplaceAttribute(ATTR_SECURITY_MANAGER_FOUND, hasManager);

        if (!hasManager) {
            if (requireSecurityManager) {
                throw new SmartFrogDeploymentException(ERROR_NO_SECURITY_MANAGER);
            }
        } else {
            Class<? extends SecurityManager> classname = manager.getClass();
            String securityString = manager.toString();
            sfReplaceAttribute(ATTR_SECURITY_MANAGER_CLASSNAME, classname);
            sfReplaceAttribute(ATTR_SECURITY_MANAGER_TO_STRING, securityString);
            String description = securityString + " classname " + classname;
            if(manager instanceof DummySecurityManager) {
                description = description + " (this is a dummy manager)";
            }
            sfLog().info("Current security manager " + description);
            if (requireExitTrapping && !ExitTrappingSecurityManager.isSecurityManagerRunning()) {
                throw new SmartFrogDeploymentException(
                        ERROR_NOT_EXIT_TRAPPING + description);
            }
            if (testSystemExit) {
                try {
                    System.exit(-1);
                } catch (Throwable t) {
                    sfLog().debug("Exit call intercepted ", t);
                }
            }

        }
        if (printPolicyInfo) {
            Policy policy = Policy.getPolicy();
            sfLog().info("Policy " + policy + " class " + policy.getClass());
        }
        try {
            source = new CodeSource(new URL("http://example.org"), (Certificate[]) null);
        } catch (MalformedURLException e) {
            throw new SmartFrogDeploymentException(e);
        }
        if (assertPolicyCanAddPermissions) {
            checkPolicyPermissionAdd();
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate("normal", "Security Manager is " + manager,
                sfCompleteNameSafe(),
                null);
    }

    /**
     * the liveness test checks that if {@link #assertPolicyCanAddPermissions} is set, then the permission
     * can be added.
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (assertPolicyCanAddPermissions) {
            try {
                checkPolicyPermissionAdd();
            } catch (SmartFrogDeploymentException e) {
                throw new SmartFrogLivenessException(e);
            }
        }
    }

    /**
     * check that the policy supports the addition of permissions
     * @throws SmartFrogDeploymentException if permissions cannot be added
     */
    public void checkPolicyPermissionAdd() throws SmartFrogDeploymentException {
        if (assertPolicyCanAddPermissions) {
            Policy policy = Policy.getPolicy();
            try {
                
                PermissionCollection permissions = policy.getPermissions(source);
                Permission perm=new PropertyPermission("org.smartfrog","read");
                permissions.add(perm);
            } catch (SecurityException e) {
                throw new SmartFrogDeploymentException(ERROR_PERMISSION_ADD_FAILED 
                        + " This could be due to a subclassed policy : "+ policy +" - " + e,
                        e);
            }
        }
    }
}
