/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Feb 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.security;

import java.io.IOException;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.avalanche.shared.CAService;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFConfigureSecurity extends PrimImpl implements Prim {
	private String SHDTERMINATE = "shouldTerminate";
	
	private boolean shouldTerminate;
	
	private CAService caService = null ; 
	private String globusLocation; 
	GridSecurity gridSecurity ; 

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFConfigureSecurity() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		// optional attribute
		shouldTerminate = sfResolve(SHDTERMINATE, true, false);
		
		System.out.println("Resolving ....."+ sfResolve("caServerLocator"));
		globusLocation = (String)sfResolve("globusLoc", globusLocation, true);
		caService = (CAService)sfResolve("caServerLocator");
		//System.out.println("caService");
		gridSecurity = new GridSecurity(globusLocation);
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();		
		
		String caCert;		
		try {
			// needs root access
			// gets CA information, creates /etc/grid-security dir and all 
			// files relevant and requests host certificate
			sfLog().info("Trying to get CA certificate from CA");
			caCert = caService.getCaCert();
			String caInfo = caService.caInfo();
			gridSecurity.configureSecurity(caCert, caInfo);			
		} catch (IOException ioe) {
			sfLog().err("Error while setting up grid security", ioe);			
			throw new SmartFrogException("Error while setting up grid security", ioe);			
		} catch (GT4SecurityException gse) {
			sfLog().err("Error while setting up grid security", gse);			
			throw new SmartFrogException("Error while setting up grid security", gse);			
		}
		
		sfLog().info("CA information setup successful");
		
		// terminate synchronously
		if (shouldTerminate) {
			TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
			sfTerminate(tr);
		}
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
	

}
