/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4wscore;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFUnInstallWSCore extends PrimImpl implements Prim {
	private final String GLOBUS_LOCATION = "globusLocation";
	private final String TOMCAT_DIR = "tomcatDir";
	private final String TOMCAT_VER = "tomcatVersion";
	private final String WEBAPPNAME = "webAppName";
	
	private String tomcatDir, tomcatVersion;
	private String webAppName, globusLocation;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFUnInstallWSCore() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		try {
			globusLocation = (String)sfResolve(GLOBUS_LOCATION, globusLocation, true);
			tomcatDir = (String)sfResolve(TOMCAT_DIR, tomcatDir, true);
			tomcatVersion = (String)sfResolve(TOMCAT_VER, tomcatVersion, true);
			webAppName = (String)sfResolve(WEBAPPNAME, webAppName, false);			
		}catch (ClassCastException e) {
			sfLog().err("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		UndeployFrmTomcat tomcat = 
			new UndeployFrmTomcat(tomcatDir, tomcatVersion);
		
		try {
			sfLog().info("Undeploying the web app " + webAppName + " ...");
			tomcat.undeployFrmTomcat(webAppName);
			tomcat.cleanXMLFiles();
		} catch (TomcatConfigException tce) {
			sfLog().info("Failed to undeploy GT4 WSCore from Tomcat");
			throw new SmartFrogException("Failed to undeploy GT4 WSCore from Tomcat : " + 
					tce.getMessage());
		}
		sfLog().info("Finished undeploying " + webAppName + " from tomcat.");
		try {
			UnInstallWSCore wscore = new UnInstallWSCore(globusLocation);
		} catch (Exception e) {
			sfLog().err(e.getMessage());
			throw new SmartFrogException(e);
		}
		
		TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
		sfTerminate(tr);
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
