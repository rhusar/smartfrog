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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

public class SUTActionImpl extends PrimImpl implements SUTAction {
	private String Host;
	private String ScriptName;
	private int Wait;
	private String Name;

	public SUTActionImpl() throws RemoteException {

	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		sfLog().info("deploying SUTActionImpl");

		// resolve stuff
		Host = (String) sfResolve(ATTR_HOST, true);
		ScriptName = (String) sfResolve(ATTR_SCRIPT_NAME, true);
		Wait = (Integer) sfResolve(ATTR_WAIT, true);
		Name = (String) sfResolve(ATTR_NAME);
	}

	public String getName() throws RemoteException {
		return Name;
	}

	public int getWait() throws RemoteException {
		return Wait;
	}

	public String getHost() throws RemoteException {
		return Host;
	}

	public String getScriptName() throws RemoteException {
		return ScriptName;
	}
}
