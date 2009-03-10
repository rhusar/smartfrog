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
package org.smartfrog.sfcore.security;

import java.security.Permission;

/**
 * This is a subclass of the Java security manager, one that uses the same exit blocking logic as the 
 * {@link ExitTrappingSecurityManager}. To enable system exits from this security manager, call
 * {@link ExitTrappingSecurityManager#setSystemExitPermitted(boolean)} 
 */

public class ExitTrappingRealSecurityManager extends SecurityManager implements ExitTrapping {

    public ExitTrappingRealSecurityManager() {
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
            throw new ExitTrappingSecurityManager.SystemExitException(status);
        }
    }

    public boolean isSystemExitPermitted() {
        return ExitTrappingSecurityManager.isSystemExitPermitted();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ExitTrappingRealSecurityManager, systemExitPermitted=" + isSystemExitPermitted();
    }

}
