/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.javawscore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;
import org.smartfrog.avalanche.client.sf.exec.ant.AntException;
import org.smartfrog.avalanche.client.sf.exec.ant.AntUtils;

import java.io.File;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JavaWSCore {
	private String globusLocation = null;
	private String buildFile = null;
	
	private static final Log log = LogFactory.getLog(JavaWSCore.class);
	private AntUtils antRunner; 
	
	/**
	 * fileName is build file path
	 * @param globusPath
	 * @param fileName
	 */
	public JavaWSCore(String globusPath, String buildFile) {
		globusLocation = globusPath;
		globusLocation = globusLocation.replace('\\', File.separatorChar);
		globusLocation = globusLocation.replace('/', File.separatorChar);
		
		this.buildFile = new String(buildFile);
		buildFile = buildFile.replace('\\', File.separatorChar);
		buildFile = buildFile.replace('/', File.separatorChar);
		
		antRunner = new AntUtils();
	}
	
	/**
	 * Properties can be null. All mandatory properties are defined. 
	 * User can define additional properties that are to be passed to the ant
	 * script using Properties.
	 * Additional Properties include,
	 * 		-DwindowsOnly=false - generate launch scripts for standard Globus 
	 * 							tools such as grid-proxy-init, etc. (Unix/Linux only)
	 * 
	 * 		-Dall.scripts=true - generate Windows and Unix launch scripts
	 * 
	 * 		-Denable.container.desc - create and configure the container with a 
	 * 							global security descriptor
	 * @param props
	 * @return
	 */
	public boolean buildFromSource(Properties props) throws WSCoreException
	{
		File bFile = new File(buildFile); 
		
		if (!bFile.exists()) {
			log.error("The build file " + buildFile + "does not exist.");
			return false;
		}		
		if (!bFile.isFile()) {
			log.error("The file " + buildFile + " is not a file.");
			return false;
		}
		
		if (props == null) {
			props = new Properties();			 
		}
		
		// setting mandatory property if it is not set
		props.setProperty("deploy.dir", globusLocation);
		
		try {
		antRunner.runAntTarget(bFile, "all", props);
		} catch (AntException ae) {
			log.error("Error building WSCore", ae);
			throw new WSCoreException("Error building WSCore", ae);
		}
			
		
		log.info("Successfully installed Java WS Core");
		return true;
	}
	
	public void uninstall() throws Exception {
		File globusLoc = new File(globusLocation);
		if (!globusLoc.exists()) {
			log.error("Cannot un-install globus....");
			log.error("Directory " + globusLocation + " does not exist");
			throw new Exception("Directory " + globusLocation + " does not exist");
		}
		
		File gridSec = new File("/etc/grid-security");
		
		DiskUtils.forceDelete(globusLoc);
		DiskUtils.forceDelete(gridSec);
	}
	
	public static void main(String args[]) {
		JavaWSCore wscore = new JavaWSCore("/home/sandya/wscore", 
				"/home/sandya/ws-core-4.0.0/build.xml");
		try {
			wscore.buildFromSource(null);
		} catch (WSCoreException be) {
			log.error("Build Error : " + be);			
		}		
	}
}
