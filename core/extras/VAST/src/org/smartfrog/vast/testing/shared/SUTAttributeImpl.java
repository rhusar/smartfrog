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
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

public class SUTAttributeImpl extends PrimImpl implements SUTAttribute {
	private String Name;
	private String Host;
	private String Value;

	public SUTAttributeImpl() throws RemoteException {
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();

		sfLog().info("deploying SUTAttributeImpl");

		// resolve stuff
		Name = (String) sfResolve(ATTR_NAME, true);
		Host = (String) sfResolve(ATTR_HOST, true);
		Value = (String) sfResolve(ATTR_VALUE, true);
	}

	public String getHost() throws RemoteException {
		return Host;
	}

	public String getName() throws RemoteException {
		return Name;
	}

	public String getValue() throws RemoteException {
		return Value;
	}
}
