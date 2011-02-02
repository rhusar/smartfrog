/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.apps.gnubuild;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Properties;

public class SFBuild extends PrimImpl implements Prim {
	private final String INSTALLERDIR = "installerDir";
	private final String INSTALLATIONDIR = "installationDir";
	private final String CONFIGOPTS = "configureOpts";
	private final String MAKETARGETS = "makeTargets";
	private final String SHDTERMINATE = "shouldTerminate";
	
	
	private String installerDir, installationDir;
	private String configureOpts, makeTargets;
	private boolean shouldTerminate;
	
	private Properties confOpts;
	private String[] targets;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFBuild() throws RemoteException {
		super();
		confOpts = new Properties();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		installerDir = sfResolve(INSTALLERDIR, installerDir, true);
		installationDir = sfResolve(INSTALLATIONDIR, installationDir, true);
				
		configureOpts = sfResolve(CONFIGOPTS, configureOpts, false);
		makeTargets = sfResolve(MAKETARGETS, makeTargets, false);

		String[] opts = configureOpts.split(",");
		confOpts = new Properties();
		for (int i=0; i<opts.length; i++) {
			String s[] = opts[i].split("=");
			if (s.length == 2)
				confOpts.setProperty(s[0], s[1]);
			else
				confOpts.setProperty(s[0], "");
		}
		
		targets = makeTargets.split(",");
		
		//optional attribute
		shouldTerminate = sfResolve(SHDTERMINATE, shouldTerminate, true);
	}	
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		BuildUtils build  = new BuildUtils(installerDir);
		
		try {
			build.configure(confOpts, null);
			sfLog().info("./configure script executed successfully.");
			sfLog().info("Starting build...");
			String env[] = {"PATH=/usr/bin:/usr/sbin:/usr/local/bin:/sbin/" +
					"/usr/local/sbin:/bin"	
			};
			build.make(targets, env);
			build.make("install");
		}catch (GNUBuildException gbe) {
			sfLog().err("Error while installation", gbe);
			throw new SmartFrogException("Error while installation", gbe);
		}
		
		sfLog().info("Build successfull");
		
		// terminate synchronously
		if (shouldTerminate) {
            new ComponentHelper(this).targetForTermination();
		}
	}


}

