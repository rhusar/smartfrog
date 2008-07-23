/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.vast.testing.shared;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.rmi.RemoteException;

public class SUTTestSequenceImpl extends CompoundImpl implements SUTTestSequence {
	private SUTState Result;
	private ArrayList<SUTAction> Actions = new ArrayList<SUTAction>();

	public SUTTestSequenceImpl() throws RemoteException {
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		sfLog().info("deploying SUTTestSequenceImpl");

		Result = (SUTState) sfResolve(ATTR_RESULT, (Object)null, true);
	}

	protected void sfDeployWithChildren() throws SmartFrogDeploymentException {
		super.sfDeployWithChildren();

		try {
			// retrieve the attribute names
			Vector<String> actions = (Vector<String>) sfResolve(ATTR_ACTIONS, true);

			// resolve them
			for (String actKey : actions)
				Actions.add( (SUTAction) sfResolve(actKey, true) );

		} catch (Exception e) {
			sfLog().error(e);
			throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
		}
	}

	public ArrayList<SUTAction> getActions() throws RemoteException {
		return Actions;
	}

	public SUTState getResult() throws RemoteException {
		return Result;
	}
}
