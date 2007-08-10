/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Mar 20, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.wsgram;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFConfigureSudo extends PrimImpl implements Prim {
	private String globusLoc, myUserName;
	private Vector users;
	private boolean shdTerminate;
	private String[] userNames;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFConfigureSudo() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		ConfigureWSGram wsgram = new ConfigureWSGram(globusLoc);
		
		try {
			wsgram.configureSudo(myUserName, userNames);
		} catch(WSGramException we) {
			sfLog().err("Error in configuring sudo", we);
			throw new SmartFrogException("Error in configuring sudo", we);
		}
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		globusLoc = (String)sfResolve("globusLocation", globusLoc, true);
		myUserName = (String)sfResolve("myUserName", myUserName, true);
		users = (Vector)sfResolve("users", users, true);
		shdTerminate = (boolean)sfResolve("shouldTerminate", true, false);
		
		userNames = (String[])users.toArray(new String[0]);
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
