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

import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * Provides the interface that a lifecycle action hook must implement. The
 * hooks are invoked for each lifecycle method at the beginning for
 * sfDeployWith (with a the context as additional data) at the beginning for
 * sfDeploy, sfStart (no additional data) at the end for sfTerminateWith
 * (given a TerminationRecord as additional data)
 *
 */
public interface PrimHook {
    /**
     * Execute the hook.
     *
     * @param prim The primitive being stepped through the lifecycle phase
     * @param terminationRecord It is used only in the TerminationWith hooks,
     *        it is a TerminationRecord indicating the cause of the
     *        termination. It is  null in all other hooks
     *      
     * @throws SmartFrogException failed to execute the hook       
     */
    public void sfHookAction(Prim prim, TerminationRecord terminationRecord)
        throws SmartFrogException;
}
