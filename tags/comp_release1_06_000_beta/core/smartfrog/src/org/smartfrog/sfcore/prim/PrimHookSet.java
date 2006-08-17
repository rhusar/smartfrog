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

package org.smartfrog.sfcore.prim;

import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;


/**
 * This class implements the collection of PrimHooks to be applied to each
 * component in a process when a lifecycle method is invoked.
 *
 */
public class PrimHookSet implements MessageKeys {
    /** The set of PrimHooks. */
    protected Vector theSet;

    /**
     * create an empty set.
     */
    public PrimHookSet() {
        theSet = new Vector();
    }

    /**
     * Add a hook to the set.
     *
     * @param hook the hook to add
     */
    public synchronized void addHook(PrimHook hook) {
        theSet.add(hook);
    }

    /**
     * Remove a hook from the set.
     *
     * @param hook Hook to remove
     *
     * @exception SmartFrogLifecycleException failed to find the hook
     */
    public synchronized void removeHook(PrimHook hook)
        throws SmartFrogLifecycleException {
        theSet.remove(hook);
    }

    /**
     * Applies user written hooks to the component.
     *
     * @param prim The prim on which the hook is being applied
     * @param terminationRecord It is used only in the TerminationWith hooks,
     *        it is a TerminationRecord indicating the cause of the
     *        termination. It is  null in all other hooks
     *
     * @exception SmartFrogLifecycleException thrown by one of the hooks
     */
    public synchronized void applyHooks(Prim prim,
        TerminationRecord terminationRecord) throws
                                                SmartFrogLifecycleException {
        try {
            for (Enumeration e = theSet.elements(); e.hasMoreElements();) {
                ((PrimHook) e.nextElement()).sfHookAction(prim,
                    terminationRecord);
            }
        } catch (Throwable t) { // the hook action will be user code
            throw new SmartFrogLifecycleException(MSG_HOOK_ACTION_FAILED, t);
        }
    }
}
