/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
 * Creates a SecurityManager that does not enforce any checks, but could print
 * some extra debuggingsecurity information.
 *
 */
public class DummySecurityManager extends SecurityManager {
    /** A debugging utility to print messages. */
    private SFDebug debug;

    /**
     * Constructs DummySecurityManager.
     */
    public DummySecurityManager() {
        super();
        debug = SFDebug.getInstance("DummySecurityManager");
    }

    /**
     * Throws a <code>SecurityException</code> if the requested access,
     * specified by the given permission, is not permitted based on the
     * security policy currently in effect.
     *
     * <p>
     * This method will trace the call and always allow the computation to
     * continue.
     * </p>
     *
     * @param perm the requested permission.
     */
    public void checkPermission(Permission perm) {
        try {
            super.checkPermission(perm);

            if (debug != null) {
                debug.println("In checkPermission " + perm +
                    " checked");
            }
        } catch (SecurityException e) {
            // We allow it anyway ...
            if (debug != null) {
                debug.println("In  checkPermission " + perm +
                    " checked Not allowed");
                debug.println(e);
            }
        }
    }

    /**
     * Throws a <code>SecurityException</code> if the requested access,
     * specified by the given permission and context is not permitted based on
     * the security policy currently in effect.
     *
     * <p>
     * This method will trace the call and always allow the computation to
     * continue.
     * </p>
     *
     * @param perm the requested permission.
     * @param context the requested context.
     */
    public void checkPermission(Permission perm, Object context) {
        try {
            super.checkPermission(perm, context);

            if (debug != null) {
                debug.println("OOKKK!!! in checkPermission " + perm +
                    " checked");
            }
        } catch (SecurityException e) {
            // We allow it anyway ...
            if (debug != null) {
                debug.println("BADDDD!!! in checkPermission " + perm +
                    " checked");
                debug.println("Not allowed!!");
                e.printStackTrace();
            }
        }
    }
}
