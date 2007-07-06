/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Dec 8, 2005
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

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Installation {
	private File globusLoc, installerDir;
	private FileUtils fUtil = null;
	private static Log log = LogFactory.getLog(Installation.class);

	/**
	 * 
	 */
	public Installation(String installerDirectory, String globusLocation) {
		super();
		// TODO Auto-generated constructor stub
		this.globusLoc = new File(globusLocation);
		this.installerDir = new File(installerDirectory);
		fUtil = new FileUtils();
	}
	
	public void build() throws GNUBuildException {
		String target[] = null;
		build(target);
	}
	
	public void build(String target) throws GNUBuildException {
		String targets[] = {target};
		build(targets);
		
		/*log.info(installerDir.getAbsolutePath());
		BuildUtils bld = new BuildUtils(installerDir.getAbsolutePath());
		
		try {
			if (null == target) {
				bld.make();
			}
			else {
				bld.make(target);
			}
		} catch (GNUBuildException gbe) {
			try {
				bld.make("distclean");
				DiskUtils.forceDelete(globusLoc);
				throw new GNUBuildException(gbe);
			} catch (IOException ioe) {
				log.warn("Could not clean up partial installation", ioe);
				throw new GNUBuildException(gbe);
			} catch (GNUBuildException g) {
				log.warn("Could not perform 'make distclean' to clean" +
						" partial installation", g);
				throw new GNUBuildException(gbe);
			}
		}*/
	}
	
	public void build(String targets[]) throws GNUBuildException {
		BuildUtils bld = new BuildUtils(installerDir.getAbsolutePath());
		int pathLen = GT4Constants.getPath().length();
		
		try {
			if (pathLen > GT4Constants.defaultPathLen) {
				GT4Constants.envp.add("PATH="+GT4Constants.pathEnv);
				String env[] = new String[GT4Constants.envp.size()];
				GT4Constants.envp.toArray(env);
				
				if (null == targets) {
					bld.make(null, env);
				}
				else {
					bld.make(targets, env);
				}
			}
			else {
				bld.make();
			}
		} catch (GNUBuildException gbe) {
			try {
				bld.make("distclean");
				DiskUtils.forceDelete(globusLoc);
				throw new GNUBuildException(gbe);
			} catch (IOException ioe) {
				log.warn("Could not clean up partial installation", ioe);
				throw new GNUBuildException(gbe);
			} catch (GNUBuildException g) {
				log.warn("Could not perform 'make distclean' to clean" +
						" partial installation", g);
				throw new GNUBuildException(gbe);
			}
		} 
	}
}