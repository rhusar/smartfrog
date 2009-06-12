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
package org.smartfrog.avalanche.client.sf.apps.gt4.build;

import org.smartfrog.avalanche.client.sf.apps.gnubuild.GNUBuildException;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.CheckPrereqs;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.PrereqException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFBuildGT4 extends PrimImpl implements Prim {
	private final String INSTALLERDIR = "installerDir";
	private final String GLOBUSLOC = "globusLoc";
	private final String CONFIGOPTS = "configureOpts";
	private final String MAKETARGETS = "makeTargets";
	private final String SHDTERMINATE = "shouldTerminate";
	
	
	private final String JAVAPATH = "javaPath";
	private final String JAVAVERSION = "javaVersion";
	private final String ANTPATH = "antPath";
	private final String ANTVERSION = "antVersion";
	private final String CVERSION = "cVersion";
	private final String TARVENDOR = "tarVendor";
	private final String SEDVENDOR = "sedVendor";
	private final String MAKEVENDOR = "makeVendor";
	private final String SUDO = "sudo";
	private final String POSTGRESVER = "postgresVersion";
	private final String PERLPATH = "perlPath";
	private final String PERLVERSION = "perlVersion";
	
	private String installerDir, globusLoc;
	private String configureOpts, makeTargets;
	private boolean shouldTerminate;
	
	
	private String javaPath, javaVersion;
	private String antPath, antVersion;
	private String cVersion,tarVendor;
	private String sedVendor, makeVendor;
	private String sudo, postgresVersion;
	private String perlPath, perlVersion;

	private Properties confOpts;
	private String[] targets;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFBuildGT4() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		confOpts = new Properties();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		javaPath = (String)sfResolve(JAVAPATH, javaPath, true);
		javaVersion = (String)sfResolve(JAVAVERSION, javaVersion, true);
		antPath = (String)sfResolve(ANTPATH, antPath, true);
		antVersion = (String)sfResolve(ANTVERSION, antVersion, true);
		cVersion = (String)sfResolve(CVERSION, cVersion, true);
		tarVendor = (String)sfResolve(TARVENDOR, tarVendor, true);
		sedVendor = (String)sfResolve(SEDVENDOR, sedVendor, true);
		makeVendor = (String)sfResolve(MAKEVENDOR, makeVendor, true);
		sudo = (String)sfResolve(SUDO, sudo, true);
		//postgresVersion = (String)sfResolve(POSTGRESVER, postgresVersion, true);
		perlPath = (String)sfResolve(PERLPATH, perlPath, true);
		perlVersion = (String)sfResolve(PERLVERSION, perlVersion, true);
			
		
		installerDir = sfResolve(INSTALLERDIR, installerDir, true);
		globusLoc = sfResolve(GLOBUSLOC, globusLoc, true);
				
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
		
		/*String opt = null;
		Enumeration e = configureOpts.elements();
		confOpts = new Properties();
		while (e.hasMoreElements()) {
			opt = (String)e.nextElement();
			String s[] = opt.split("=");
			if (s.length == 2)
				confOpts.setProperty(s[0], s[1]);
			else
				confOpts.setProperty(s[0], "");			
		}
		
		targets = (String[])makeTargets.toArray(new String[0]);
		*/		
						
		//optional attribute
		shouldTerminate = (boolean)sfResolve(SHDTERMINATE, shouldTerminate, true);		
	}	
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		CheckPrereqs chk = new CheckPrereqs();
		
		try {
			chk.checkCmd(javaPath, "java", javaVersion, null);
			chk.checkCmd(antPath, "ant", antVersion, null);
			chk.checkCmd("tar", null, tarVendor);
			chk.checkCmd("sed", null, sedVendor);
			chk.checkCmd("cc", cVersion);
			chk.checkCmd("make", null, makeVendor);
			chk.checkCmd("sudo", null, null);
			//chk.checkCmd("postgres", postgresVersion);
			//chk.checkCmd("perl", null, null);
			chk.checkCmd(perlPath,"perl", perlVersion, null);
			
		} catch(IOException ioe) {
			sfLog().err("Exception in checking pre-requisites", ioe);
			throw new SmartFrogException("Exception in checking pre-requisites", 
					ioe);
		} catch (PrereqException pe) {
			sfLog().err(pe);			
			throw new SmartFrogException(pe);
		} catch (InterruptedException ie) {
			sfLog().err(ie);
			throw new SmartFrogException(ie);
		}
		
		sfLog().info("All pre-requisites for GT4 satisfied");
		
		Configure configure = new Configure(installerDir, globusLoc);
		Installation gt4 = new Installation(installerDir, globusLoc);
		
		try {
			configure.runConfigure(confOpts);
			sfLog().info("./configure script executed successfully.");
			sfLog().info("Starting build...");
			gt4.build(targets);
			sfLog().info("Installing in GLOBUS_LOCATION");
			gt4.build("install");
		}catch (GNUBuildException gbe) {
			sfLog().err("Error while installing GT4", gbe);
			throw new SmartFrogException("Error while isntalling GT4", gbe);
		}
		
		sfLog().info("GT4 Built successfully");
		
		// terminate synchronously
		if (shouldTerminate) {
			TerminationRecord tr = TerminationRecord.normal( "Terminating ...", sfCompleteName());
			sfTerminate(tr);
		}
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
