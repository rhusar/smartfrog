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

import org.smartfrog.avalanche.client.sf.apps.gnubuild.GNUBuildException;
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
public class SFInstallGridFTP extends PrimImpl implements Prim {
	private final String INSTDIR = "installerDir";
	private final String GLOC = "globusLoc";
	private final String CONFIGOPTS = "configureOpts";
	private final String MAKETARGETS = "makeTargets";
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String installerDir, globusLoc;
	private String configureOpts, makeTargets;
	private boolean shdTerminate = true;
	
	private Properties configProps;
	private String[] targets;
	
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFInstallGridFTP() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		installerDir = (String)sfResolve(INSTDIR, installerDir, true);
		globusLoc = (String)sfResolve(GLOC, globusLoc, true);
		configureOpts = (String)sfResolve(CONFIGOPTS, configureOpts, false);
		makeTargets = (String)sfResolve(MAKETARGETS, makeTargets, true);
		shdTerminate = (boolean)sfResolve(SHDTERMINATE, shdTerminate, false);
		
		String[] opts = configureOpts.split(",");
		configProps = new Properties();
		for (int i=0; i<opts.length; i++) {
			String s[] = opts[i].split("=");
			if (s.length == 2)
				configProps.setProperty(s[0], s[1]);
			else
				configProps.setProperty(s[0], "");
		}
		
		targets = makeTargets.split(",");
		
		/*Enumeration e = configureOpts.elements();
		configProps = new Properties();
		String opt = null;
		while (e.hasMoreElements()) {
			opt = (String)e.nextElement();
			String s[] = opt.split("=");
			if (s.length == 2)
				configProps.setProperty(s[0], s[1]);
			else
				configProps.setProperty(s[0], "");
		}
		
		targets = (String[])makeTargets.toArray(new String[0]);
		*/
		
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		try {
			GridFtp gftp = new GridFtp(installerDir, globusLoc);
			sfLog().info("Starting installation of Grid FTP...");
			sfLog().info("Making target : " + targets[0]);
			gftp.installGFTP(configProps, targets);
		} catch (GNUBuildException gbe) {
			sfLog().err("Error in installing GridFTP", gbe);
			throw new SmartFrogException("Error in installing GridFTP", gbe);
		}
		
		sfLog().info("Successfully installed Grid FTP");
		
		//terminate synchronously
		TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
		sfTerminate(tr);
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
