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

import java.io.File;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GT4Installation extends CommonSteps {
	private File globusLoc, installerDir;
	private FileUtils fUtil = null;
	private static final Log log = LogFactory.getLog(GT4Installation.class);

	/**
	 * 
	 */
	public GT4Installation(String loc, String inst) {
		super(inst, loc);
		// TODO Auto-generated constructor stub
		globusLoc = new File(loc);
		installerDir = new File(inst);

		fUtil = new FileUtils();
	}
	
	public void installCompleteGT4() 
			throws GNUBuildException, GT4Exception {
		
			// 1. Check pre-requisites
			checkPreReqs();
			
			// 2. $configure --prefix=$GLOBUS_LOCATION
			runConfigure();
			
			/* 3. $cd <GT4 Installer>
			 *    $make
			 */
			buildGT4();
			
			// 4. $make install
			buildGT4("install");
			
			// 5. Security setup
			/*GridSecurity sec = new GridSecurity(globusLoc.getAbsolutePath());
			try {				
				sec.setup();
				Properties props = new Properties();
				props.setProperty("-force", "");
				sec.reqHostCert(props);
				SetupSecurityConstants secConst = new SetupSecurityConstants();
				File hostCertReqFile = new File(SetupSecurityConstants.hostCertReq);
				File hostSignedCertDir = new File(SetupSecurityConstants.gridSecurityDir);
				sec.getSignedCert(hostCertReqFile, hostSignedCertDir, "hostcert.pem");
				
				//sec.reqUserCert(null);
				File userCertReqFile = new File(SetupSecurityConstants.userCertReq);
				String userHome = System.getProperty("user.home");
				String userCertDir = userHome + File.separatorChar + ".globus";
				File userSignedCertDir = new File(userCertDir);
				sec.getSignedCert(userCertReqFile, userSignedCertDir, "usercert.pem");
			} catch (GT4SecurityException se) {
				throw new GT4Exception(se);
			}*/		
	}
	
	public void buildGT4() throws GNUBuildException {
		String target = null;
		buildGT4(target);
	}
	
	public void buildGT4(String target) throws GNUBuildException {
		log.info(installerDir.getAbsolutePath());
		BuildUtils bld = new BuildUtils(installerDir.getAbsolutePath());
		if (null == target) {
			bld.make();			
		}
		else {
			bld.make(target);
		}
	}
	
	public void buildGT4(String targets[]) throws GNUBuildException {
		BuildUtils bld = new BuildUtils(installerDir.getAbsolutePath());
		bld.make(targets);
	}
	
	public static void main(String args[]) {
		String globusLocation = "/home/sandya/globus401";
		String instDir =  "/home/sandya/gt4.0.1-all-source-installer";
		GT4Installation gt4 = new GT4Installation(globusLocation, instDir);
		
		try {
			gt4.installCompleteGT4();
		} catch (GNUBuildException gbe) {
			log.error(gbe);
			gbe.printStackTrace();
		} catch (GT4Exception ge) {
			log.error(ge);
		}
	}
}