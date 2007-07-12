/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.anubis;

import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.services.anubis.locator.AnubisListener;
import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

public class SFListenerDeployer extends PrimImpl implements Prim {
	
	// make it a list of listeners 
	AnubisListener listener = null ;
	AnubisLocator locator = null; 
	public SFListenerDeployer() throws RemoteException{
		
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		locator = (AnubisLocator)sfResolve("locator") ;
		
		// let it be the default configuration
		listener = new AnubisJMSAdapter(MonitoringConstants.ANUBIS_SHARED_NAME); 
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		// throw null pointer for now
		sfLog().info("Registering Anubis listener ... ");
		locator.registerListener(listener);
		sfLog().info("Anubis listener registered ... ");
		System.out.println("Anubis listener registered. ");
	}
	
}
