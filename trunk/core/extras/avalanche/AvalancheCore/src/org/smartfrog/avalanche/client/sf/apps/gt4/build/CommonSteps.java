/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Dec 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.gnubuild.BuildUtils;
import org.smartfrog.avalanche.client.sf.apps.gnubuild.GNUBuildException;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.CheckPrereqs;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.PrereqException;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonSteps extends GT4Constants {
	public BuildUtils bld = null;	
	public String installerDir, globusLoc = null;
	
	private static Log log = LogFactory.getLog(CommonSteps.class);
	
	/**
	 * 
	 */
	public CommonSteps(String dir, String gLoc) {
		super();
		// TODO Auto-generated constructor stub
		installerDir = dir;
		bld = new BuildUtils(installerDir);
		globusLoc = new String (gLoc);
		globusLoc = globusLoc.replace('\\', File.separatorChar);
		globusLoc = globusLoc.replace('/', File.separatorChar);
	}
	
	public void checkPreReqs() throws GT4Exception {
		CheckPrereqs preReq = new CheckPrereqs();
		
		try {
			// JAVA
			preReq.checkCmd("java", "1.4.2");
				
			// ANT
			String javaVer = System.getProperty("java.version");
			if (preReq.checkVersion("1.5", javaVer)) {
				preReq.checkCmd("ant", "1.6.1");					
			}
			else {
				preReq.checkCmd("ant", "1.5.1");					
			}
			
			// C Compiler
			preReq.checkCmd("cc", "--version");
						
			// TAR
			preReq.checkCmd("tar", null, "gnu");
			
			// SED
			preReq.checkCmd("sed", null, "gnu");
						
			// MAKE
			preReq.checkCmd("make", null, "gnu");
			
			// SUDO
			preReq.checkCmd("sudo");
			
			// POSTGRES
			//preReq.checkCmd("postgres", "7.1");							
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4Exception(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4Exception(ie);
		} catch (PrereqException pe) {
			log.error(pe);
			throw new GT4Exception(pe);
		}		
	}
	
	public void runConfigure() throws GNUBuildException {
		runConfigure(null);
	}
	
	public void runConfigure(Properties props) 
			throws GNUBuildException {
		//globusLoc = props.getProperty("--prefix");
		
		if (props == null) {
			props = new Properties();			
		}
		
		if (globusLoc == null) {
			log.error("Please provide GLOBUS_LOCATION");
			throw new GNUBuildException("GLOBUS_LOCATION is not defined");
		}
		props.setProperty("--prefix", globusLoc);
			
		// Create the dir globusLoc
		if (!FileUtils.createDir(globusLoc)) {
			throw new GNUBuildException("Error in creating directory " + 
					globusLoc);
		}
		
		// Run configure script		
		bld.configure(props, null);		
	}
}