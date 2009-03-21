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

import java.security.Permission;

/**
 * Created 28-Oct-2008 13:52:18
 */

public class ExitTrappingSecurityManager extends DummySecurityManager implements ExitTrapping {

    private static volatile boolean systemExitPermitted = false;

    public ExitTrappingSecurityManager() {
    }

    /**
     * Query to see if the security manager permits system exits
     * @return true if the system exit is permitted
     */
    public static boolean isSystemExitPermitted() {
        return systemExitPermitted;
    }

    /**
     * Set the system exit flag
     * @param systemExitPermitted true if exit is to be allowed, false otherwise
     */
    public static void setSystemExitPermitted(boolean systemExitPermitted) {
        ExitTrappingSecurityManager.systemExitPermitted = systemExitPermitted;
    }

    /**
     * Blocks exits if the shared enable exits {@inheritDoc}
     *
     * @param status the exit status.
     * @throws SecurityException if the calling thread does not have permission to halt the Java Virtual Machine with
     *                           the specified status.
     * @see Runtime#exit(int) exit
     * @see #checkPermission(Permission) checkPermission
     */
    @Override
    public void checkExit(int status) {
        if (isSystemExitPermitted()) {
            super.checkExit(status);
        } else {
            throw new SystemExitException(status);
        }
    }

    /**
     * Returns a string representation of the object. 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ExitTrappingSecurityManager, systemExitPermitted=" + isSystemExitPermitted();
    }

    /**
     * Tells you if an exit trapping security manager is installed. 
     * @return true if there is a security manager and it is exit trapping
     */
    public static boolean isSecurityManagerRunning() {
        SecurityManager current = System.getSecurityManager();
        return current != null && current instanceof ExitTrapping;
    }

    /**
     * Register the security manager. 
     * @return true if there is now an ExitTrapping SecurityManager running
     */
    public static boolean registerSecurityManager() {
        return registerSecurityManager(false);
    }

    /**
     * Register the security manager.
     * @param real flag to indicate whether a real or dummy security manager is desired
     * @return true if there is now an ExitTrappingSecurityManager running
     */
    public static boolean registerSecurityManager(boolean real) {
        SecurityManager current = System.getSecurityManager();
        if (current != null) {
            return current instanceof ExitTrapping;
        }
        SecurityManager newManager;
        if (real) {
            newManager = new ExitTrappingRealSecurityManager();
            System.setSecurityManager(newManager);
            return true;
        } else {
            //newManager = new ExitTrappingSecurityManager();
            return false;
        }
    }

    /**
     * An exception that gets returned when someone called system.exit but the exit was blocked.
     */
    public static class SystemExitException extends RuntimeException {
        private int status;

        /**
         * Constructs a new runtime exception with <code>null</code> as its detail message.  The cause is not
         * initialized, and may subsequently be initialized by a call to {@link #initCause}.
         *
         * @param status exit code that was used for this exit request
         */
        public SystemExitException(int status) {
            super("SystemExit with status code " + status + " blocked");
            this.status = status;
        }

        /**
         * The status code that was used with this exception
         *
         * @return the status code.
         */
        public int getStatus() {
            return status;
        }
    }

}
