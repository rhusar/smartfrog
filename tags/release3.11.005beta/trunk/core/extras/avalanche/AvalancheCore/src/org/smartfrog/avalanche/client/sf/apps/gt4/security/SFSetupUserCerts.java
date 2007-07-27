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
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFSetupUserCerts extends PrimImpl implements Prim {
	private String SHDTERMINATE = "shouldTerminate";
	
	private boolean shouldTerminate;
	
	private CAService caService = null ;
        private String caHost;
	private String caLocator;

	private String globusLocation, userPassphrase, user ; 
	GridSecurity gridSecurity ; 

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFSetupUserCerts() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		// optional attribute
		shouldTerminate = sfResolve(SHDTERMINATE, true, false);
	
		System.out.println("Resolving .....");	
		//System.out.println("Resolving ....."+ sfResolve("caServerLocator"));
		globusLocation = (String)sfResolve("globusLoc", globusLocation, true);
		
		//caService = (CAService)sfResolve("caServerLocator");
		
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
		
		userPassphrase = (String)sfResolve("userPassphrase", userPassphrase, true);

		user = (String) sfResolve("user", user, true);
		
		gridSecurity = new GridSecurity(globusLocation);
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();		
		
		String caCert;		
		try {
			// request user certificate
			Properties usrProps = new Properties();
			usrProps.setProperty("-passphrase", userPassphrase);
			usrProps.setProperty("-cn", user);
			//gridSecurity.reqUserCert(usrProps);
			gridSecurity.reqUserCert(usrProps, user);
			
			// get signed user certificate from CA
			// needs user access

			String userCertReq = File.separatorChar + "home" + File.separatorChar + user + File.separatorChar +
				File.separatorChar + ".globus" + File.separatorChar + 
				"usercert_request.pem";
			//File userReqFile = new File(SecurityConstants.userCertReq);
			File userReqFile = new File(userCertReq);
			String userReqStr = FileUtils.file2String(userReqFile);
			String userCert = caService.signCert(userReqStr);
			
			// install user certificate on client machine
			// needs user access
		//	String destDir = System.getProperty("user.home") + File.separatorChar +
		//				".globus" + File.separatorChar;
			String destDir = File.separatorChar + "home" + File.separatorChar + user + File.separatorChar +
						".globus" + File.separatorChar;
			File userDestDir = new File(destDir);
			gridSecurity.installSignedCert(userCert, userDestDir, "usercert.pem");
			sfLog().info("User Certificate setup is done");			
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
		
		sfLog().info("Grid security setup done successfully");
		
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
