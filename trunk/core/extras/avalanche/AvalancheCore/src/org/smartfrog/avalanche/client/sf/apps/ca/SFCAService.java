/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.apps.ca;

import org.smartfrog.avalanche.shared.CAService;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

public class SFCAService extends PrimImpl implements Prim,CAService {

	private CAServiceImpl caService = null; 
	String caPassphrase = null ;
	String opensslDir, caDir;
	
	
	public SFCAService() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		caDir = sfResolve("caDir", caDir, true);
		opensslDir = sfResolve("opensslDir", opensslDir, true);
		caService = new CAServiceImpl(caDir, opensslDir);
		caPassphrase = sfResolve("caPassphrase", caPassphrase ,true);
		sfLog().info("Setting ca passphrase");
		caService.setPassphrase(caPassphrase);				
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		sfLog().info("Starting CA Service...");
	}

	public synchronized void sfTerminateWith(TerminationRecord arg0) {
		super.sfTerminateWith(arg0);
	}

	public String getCaCert() throws RemoteException {
		String cert = null ; 
		try {
			cert = caService.getCaCert();
		}catch(CAException ex){
			sfLog().err("Error in getting CA public key", ex); 
			throw new RemoteException ("Error in getting CA public key", ex);
		}
		return  cert ;
	}
	
	public String signCert(String certReq) throws RemoteException {
		String cert = null ; 
		try{
			cert = caService.signCert(certReq);
		}catch(CAException ex){
			sfLog().err("Error in signing certificate", ex);
			throw new RemoteException ("Error in signing certificate", ex);
		}
		return cert ;
	}
	
	public String caInfo() throws RemoteException {
		String info = null;
		
		try {
			info = caService.caInfo();			
		} catch (CAException cae) {
			sfLog().err("Error in getting ca Information...", cae);
			throw new RemoteException("Error in getting ca Information...", cae);
		}		
		return info;
	}
}
