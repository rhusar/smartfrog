/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.combinators;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * Retract is a modified compound in which sub-components are terminated in
 * reverse order in which they are deployed and started.
 */
public class Retract extends EventCompoundImpl implements Compound {
    /**
     * Constructor Retract.
     *
     * @throws RemoteException In case of RMI or network error.
     */
    public Retract() throws RemoteException {
        super();
    }

    /**
     * Performs the retract compound termination behaviour. Terminates children
     * in reverse from their order at deploy and start phase . Termination is
     * synchronous.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        for (int i = sfChildren.size() - 1; i >= 0; i--) {
            try {
                ((Prim) sfChildren.elementAt(i)).sfTerminateQuietlyWith(status);
            } catch (Exception ex) {
              if (sfLog().isErrorEnabled()) {
                  String errStr="Exception while terminating one of the children";
                  sfLog().error(errStr,ex);
              }
            }
        }

        // we've overriden the compound's sfTerminateWith; therefore we need
        // to call the hooks ourselves.
        try {
            sfTerminateWithHooks.applyHooks(this, status);
        } catch (Exception e) {
        }
    }
}
