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

import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.shared.CAService;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFSetupHostCerts extends PrimImpl implements Prim {
	private String SHDTERMINATE = "shouldTerminate";
	
	private boolean shouldTerminate;
	
	private CAService caService = null ;
        private String caHost;
	private String caLocator;	
	private String globusLocation, userName; 
	GridSecurity gridSecurity ; 

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFSetupHostCerts() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		// optional attribute
		shouldTerminate = sfResolve(SHDTERMINATE, true, false);
		
		System.out.println("Resolving .....");
	//	System.out.println("Resolving ....."+ sfResolve("caServerLocator"));
		globusLocation = (String)sfResolve("globusLoc", globusLocation, true);
		
	//	caService = (CAService)sfResolve("caServerLocator");
		caHost = (String)sfResolve("caServerHost", caHost, true);
		caLocator = (String)sfResolve("caServerLocator", caLocator, true);

		try{	
			Compound cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(caHost));
			Prim app = (Prim)cp.sfResolveHere(caLocator);
			caService = (CAService) app;
		} catch (Exception ex) {
			sfLog().err("Error while getting reference to CA Service", ex);			
			throw new SmartFrogException("Error while getting reference to CA Service", ex);
		}
		
		gridSecurity = new GridSecurity(globusLocation);
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();		
		
		try {
			// get signed host certificates from CA
			// needs root access
			File hostReqFile = new File("/etc/grid-security/hostcert_request.pem");
			String hostReqStr = FileUtils.file2String(hostReqFile);
			if (hostReqStr == null) {
				sfLog().err("Error in reading file /etc/grid-security/hostcert_request.pem");
				throw new SmartFrogException("Error in reading file /etc/grid-security/hostcert_request.pem");
			}
			String hostCert = caService.signCert(hostReqStr);
			
			// install host certificate on client machine
			// needs root access
			File hostDestDir = new File(SecurityConstants.gridSecurityDir);
			gridSecurity.installSignedCert(hostCert, hostDestDir, "hostcert.pem");
			sfLog().info("Host Certificate setup is done");									
		} catch (FileNotFoundException fnfe) {
			sfLog().err("Error while setting up grid security", fnfe);			
			throw new SmartFrogException("Error while setting up grid security", fnfe);
		} catch (IOException ioe) {
			sfLog().err("Error while setting up grid security", ioe);			
			throw new SmartFrogException("Error while setting up grid security", ioe);			
		} catch (GT4SecurityException gse) {
			sfLog().err("Error while setting up grid security", gse);			
			throw new SmartFrogException("Error while setting up grid security", gse);			
		}
		
		sfLog().info("Host and Container credentials are setup successfully");
		
		// terminate synchronously
		if (shouldTerminate) {
			TerminationRecord tr = TerminationRecord.normal("Terminating ...", sfCompleteName());
			sfTerminate(tr);
		}
	}	

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
	

}
