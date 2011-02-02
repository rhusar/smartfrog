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

package org.smartfrog.test.system.start;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;


public class InputImpl extends PrimImpl implements Prim, Input, Remote {
    private String me;
    private NetElem function;

    // standard constructor
    public InputImpl() throws RemoteException {
    }

    // Input methods
    public void input(int value) throws RemoteException {
        function.doit(me, value);
    }

    // lifecycle methods
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        try {
            // get the function part; currently the parent
            // (perhaps should be a link...)
            function = (NetElem) (sfParent().sfParent());

            // get my name in the "inputs" context for use as my ID
            me = (String) (sfParent().sfAttributeKeyFor(this));
        } catch (Exception e) {
            try {
                Reference name = sfCompleteName();
                terminateComponent(this, e, name);
                throw new SmartFrogDeploymentException(e, this);
            } catch (Throwable th) { // the call to sfCompleteName has failed
                terminateComponent(this, e, null);
                throw new SmartFrogDeploymentException(e, this);
            }
        }
    }
}
