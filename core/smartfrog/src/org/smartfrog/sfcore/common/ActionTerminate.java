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
package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * @author steve loughran
 *         created 18-Mar-2004 10:52:51
 */

public class ActionTerminate extends ConfigurationAction{
    /**
     * this has to be implemented by subclasses; execute a configuration command against
     * a specified target.
     * This version looks up the target and notes if it was a root process or not.
     * then
     *
     * @param targetP       target process
     * @param configuration
     */
    public Object execute(ProcessCompound targetP,
                          ConfigurationDescriptor configuration) throws SmartFrogException,
            RemoteException {
        Prim targetC = LookupTarget(targetP, configuration);
        boolean isRootProcess = IsRootProcess(targetC);
        String name = targetC.sfCompleteName().toString();
        try {
            targetC.sfTerminate(new TerminationRecord(TerminationRecord.NORMAL,
                    "External Management Action",
                    targetP.sfCompleteName()));
        } catch (RemoteException ex) {
            HandleTerminationException(ex, isRootProcess, configuration);
        }
        configuration.setSuccessfulResult();
        return targetC;
    }

}
