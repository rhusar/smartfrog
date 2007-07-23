/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.rpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFRPMInstall extends PrimImpl implements Prim {
	private static final String RPMPACKAGE = "rpmPackage";
	private static final String INSTALLOPTS = "installOptions";
	String rpmPackage;
	String installOptions;
	RPMUtils rpmUtils;
	private static Log log = LogFactory.getLog(SFRPMInstall.class);
	
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFRPMInstall() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub			
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		try{
			rpmUtils.InstallPackage(rpmPackage, installOptions);
		}catch (IOException e) {
			log.error("Unable to Install RPM package "+ rpmPackage,e);
			throw new SmartFrogException("Unable to Install RPM package "+ rpmPackage,e);
		}
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		try {
			rpmPackage = (String)sfResolve(RPMPACKAGE);
			installOptions = (String)sfResolve(INSTALLOPTS);
			rpmUtils = new RPMUtils();
		}catch (ClassCastException e) {
			log.error("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}
		
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
}