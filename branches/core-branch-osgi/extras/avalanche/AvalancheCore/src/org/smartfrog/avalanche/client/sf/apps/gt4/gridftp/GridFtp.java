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
package org.smartfrog.avalanche.client.sf.apps.gt4.gridftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.gnubuild.GNUBuildException;
import org.smartfrog.avalanche.client.sf.apps.gt4.build.Configure;
import org.smartfrog.avalanche.client.sf.apps.gt4.build.Installation;

import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GridFtp extends Configure {
	private String installerDir;
	private String globusLoc;
	private static Log log = LogFactory.getLog(GridFtp.class);

	/**
	 * 
	 */
	public GridFtp(String installerDir, String globusLoc) {
		super(installerDir, globusLoc);
		
		this.installerDir = new String(installerDir);
		this.globusLoc = new String(globusLoc);		
	}
	
	public void installGFTP(Properties configProps, String[] makeTargets) 
			throws GNUBuildException {
		// 2. run configure script
		runConfigure(configProps);
		
		// 3. build gridFTP
		Installation gftp = new Installation(installerDir, globusLoc);
		gftp.build(makeTargets);
		
		// 4. make install
		gftp.build("install");
	}
	
	public static void main(String args[]) {
		GridFtp gftp = new GridFtp("/home/sandya/gt4.0.1-all-source-installer", "/home/sandya/globus401");
		
		try {
			gftp.installGFTP(null, null);			
		} catch (GNUBuildException gbe) {
			log.error(gbe);
		}
	}
}
