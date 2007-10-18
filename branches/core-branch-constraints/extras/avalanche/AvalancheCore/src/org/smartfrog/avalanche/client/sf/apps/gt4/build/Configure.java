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
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Configure extends GT4Constants {
	public BuildUtils bld = null;	
	public String installerDir, globusLoc = null;
	//private GT4Constants gt4Consts;
	
	private static Log log = LogFactory.getLog(Configure.class);
	
	/**
	 * 
	 */
	public Configure(String installerDir, String globusLoc) {
		super();
		// TODO Auto-generated constructor stub
		this.installerDir = new String(installerDir);
		bld = new BuildUtils(installerDir);
		this.globusLoc = new String(globusLoc);
		globusLoc = globusLoc.replace('\\', File.separatorChar);
		globusLoc = globusLoc.replace('/', File.separatorChar);
		//gt4Consts = new GT4Constants();
	}
	
	public void runConfigure() throws GNUBuildException {
		runConfigure(null);
	}
	
	public void runConfigure(Properties props) 
			throws GNUBuildException {
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
		int pathLen = GT4Constants.getPath().length();	
		try {
			if (pathLen > GT4Constants.defaultPathLen) {
				/*
				 * Some of the pre-requisite software is not in path.
				 */
				GT4Constants.envp.add("PATH="+GT4Constants.pathEnv);
				String env[] = new String[GT4Constants.envp.size()];
				GT4Constants.envp.toArray(env);
				
				bld.configure(props, env);
			}
			else {
				// All pre-requisite software is in path
				bld.configure(props, null);
			}
		} catch (GNUBuildException gbe) {
			File gLoc = new File(globusLoc);
			try {
				DiskUtils.forceDelete(gLoc);
				throw new GNUBuildException(gbe);
			} catch (IOException ioe) {
				log.warn("Cannot delete globus location " + globusLoc + " directory", ioe);
				throw new GNUBuildException(gbe);
			}
		}
	}
}