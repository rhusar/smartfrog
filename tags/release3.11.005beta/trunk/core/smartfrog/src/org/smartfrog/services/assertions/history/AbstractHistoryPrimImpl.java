/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.assertions.history;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.services.assertions.SmartFrogAssertionException;

import java.rmi.RemoteException;

/** created 09-Jul-2007 14:20:12 */

public abstract class AbstractHistoryPrimImpl extends PrimImpl {

    public static final String ATTR_HISTORY = "history";
    
    protected AbstractHistoryPrimImpl() throws RemoteException {
    }

    /**
     * Get the log
     * @return the resolved component
     * @throws SmartFrogAssertionException for invalid events
     * @throws RemoteException for RMI-related problems
     */
    protected History resolveHistory() throws SmartFrogResolutionException, RemoteException {
        Prim logPrim = null;
        logPrim = sfResolve(ATTR_HISTORY, logPrim, true);
        History history = (History) logPrim;
        return history;
    }

    protected void queueForTermination(String text) {
        //Workflow integration
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, text, null, null);
    }
}
