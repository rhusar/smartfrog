/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Feb 23, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.gridftp;

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
public class SFConfigureGridFTP extends PrimImpl implements Prim {
	private final String GLOC = "globusLoc";
	private final String PORT = "port";
	private final String CONFTYPE = "configureType";
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String globusLoc, configType;
	private Integer port;
	private int portNumber;
	private boolean shdTerminate = true;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFConfigureGridFTP() throws RemoteException {
		super();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		globusLoc = (String)sfResolve(GLOC, globusLoc, true);
		configType = sfResolve("configureType", "temp", true);
		port = (Integer)sfResolve(PORT, port, true);		
		shdTerminate = (boolean)sfResolve(SHDTERMINATE, shdTerminate, false);
		portNumber = port.intValue();
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		sfLog().info("ConfigType : " + configType);
		try {
			ConfigureGridFTP gftp = new ConfigureGridFTP(globusLoc);
			if (configType.equalsIgnoreCase("xinetd")) 
				gftp.configureWithXinetd(portNumber);
			else if (configType.equalsIgnoreCase("inetd"))
				gftp.configureWithInetd(portNumber);
			else {
				sfLog().err("The configure type - " +
					configType + " is not supported");
				throw new SmartFrogException("The configure type - " +
						configType + " is not supported");
			}
		} catch(IOException ioe) {
			sfLog().err("Error while configuring GridFTP with " + configType, ioe);
			throw new SmartFrogException("Error while configuring GridFTP with " + configType, ioe);
		} catch (GFTPException gftpe) {
			sfLog().err("Error while configuring GFTP with " + configType, gftpe);
			throw new SmartFrogException("Error while configuring GFTP with " + configType, gftpe);			
		}
		
		if (shdTerminate) {
			TerminationRecord tr = TerminationRecord.normal("Terminating ...", sfCompleteName());
			sfTerminate(tr);
		}
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
}