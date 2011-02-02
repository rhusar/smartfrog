/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dependencies.statemodel.connector;

import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.services.dependencies.statemodel.dependency.DependencyValidation;
import org.smartfrog.services.dependencies.statemodel.state.StateDependencies;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.EXISTS;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.NOT;

public class Connector extends PrimImpl implements Prim, DependencyValidation, StateDependencies {
    protected Vector<DependencyValidation> dependencies = new Vector<DependencyValidation>();
    protected boolean exists =false;
    protected boolean not =false;

    public Connector() throws RemoteException {}

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        exists = sfResolve(EXISTS, false, true);
        not = sfResolve(NOT, false, true);
        super.sfStart();
    }

    public void register(DependencyValidation d) {
		 dependencies.add(d);
	}
	public void deregister(DependencyValidation d) {
	     dependencies.remove(d);
	}
	   
    public String getTransition(){
       return null;
    }

    public boolean isEnabled() throws RemoteException, SmartFrogRuntimeException {
       return false;
    }
}
