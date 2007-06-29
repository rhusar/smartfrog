/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Mar 16, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.utils;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFAddUser extends PrimImpl implements Prim {
	private String homeDir = "";
	private String shell = "";
	private String passwd = "";
	private String user;
	private boolean shdTerminate = true;
	private Properties props;
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFAddUser() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		if ((user.length() == 0) || (null == user)) {
			sfLog().err("User name cannot be null");
			throw new SmartFrogException("User name cannot be null");
		}
		
		try {
			SystemUtils.addUser(user, props);
		} catch (UtilsException ue) {
			sfLog().err(ue);
			throw new SmartFrogException("Error in adding user", ue);
		}
		
		if (shdTerminate) {
			TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
			sfTerminate(tr);
		}
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		homeDir = (String)sfResolve("homeDir", homeDir, true);
		shell = (String)sfResolve("shell", shell, true);
		passwd = (String)sfResolve("passwd", passwd, true);
		user = (String)sfResolve("user", user, true);
		
		shdTerminate = sfResolve("shdTerminate", true, false);
		
		props = new Properties();
		props.setProperty("-p", passwd);
		props.setProperty("-d", homeDir);
		props.setProperty("-s", shell);
		props.setProperty("-m", "");
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
