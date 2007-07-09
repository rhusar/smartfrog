/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 9, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.client.sf.apps.utils.TxtFileHelper;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SSLUtils extends SecurityConstants {
	private static Log log = LogFactory.getLog(SSLUtils.class);
	String hash = null;

	/**
	 * 
	 */
	public SSLUtils(String hash) {
		super();
		// TODO Auto-generated constructor stub
		this.hash = hash;
	}
	
	public void hostSSLConfig() 
				throws GT4SecurityException {
		TxtFileHelper txtHelper, gridSecTxt;
		
		InputStream src = null; 
		globusHostConf = globusHostConf.replaceAll("__HASH__", hash);
		gridSecConf = gridSecConf.replaceAll("__HASH__", hash);
		File hostConf = new File(globusHostConf);
		File gridSec = new File (gridSecConf);
		try {
			src = getClass().getResourceAsStream(hostSSLConfTmpl);
			DiskUtils.fCopy(src, hostConf);
			txtHelper = new TxtFileHelper(globusHostConf);
			gridSecTxt = new TxtFileHelper(gridSecConf);
			String hostBaseDN = gridSecTxt.getValue("SETUP_GSI_HOST_BASE_DN", "=",
					"#");
			String[] hostBase = hostBaseDN.split(",");
			hostBase[0] = (hostBase[0].replaceAll("\"", "")).trim();
			String orgUnit[] = hostBase[0].split("=");
			
			hostBase[1] = (hostBase[1].replaceAll("\"", "")).trim();
			String org[] = hostBase[1].split("=");
			
			/*hostBase[2] = (hostBase[2].replaceAll("\"", "")).trim();
			String c[] = hostBase[2].split("="); */
			
			
			txtHelper.changeValueAfter("=", "0.organizationName_default", 
					org[1]);
			txtHelper.changeValueAfter("=", "0.organizationalUnitName_default", 
					orgUnit[1]);
			//txtHelper.changeValueAfter("=", "countryName_default",c[1]);
			FileUtils.chgPermissions(globusHostConf, "644");
			/*int idx = globusHostConf.lastIndexOf(File.separatorChar);
			String destFile = globusHostConf.substring(idx);
			destFile = destFile.trim();
			idx = destFile.lastIndexOf(".");
			destFile = destFile.substring(0,idx);
			log.info("DEST FILE " + destFile);
			String link = gridSecurityDir + File.separatorChar + destFile;
			DiskUtils.forceDelete(link);
			if (!FileUtils.softLink(globusHostConf, gridSecurityDir, link)) {
				log.error("Error in creating soft link...");
				throw new GT4SecurityException("Error in creating soft link " +
						" to " + globusHostConf);
			}*/
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new GT4SecurityException(fnfe);			
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);			
		} finally {
			try {
				src.close();
			} catch (IOException ioe) {
				log.error("Error in closing File input stream for " + 
						hostSSLConfTmpl);
			}
		}
	}
	
	public void userSSLConfig() throws GT4SecurityException {
		TxtFileHelper txtHelper, gridSecTxt;
		
		InputStream src = null; 
		globusUserConf = globusUserConf.replaceAll("__HASH__", hash);
		gridSecConf = gridSecConf.replaceAll("__HASH__", hash);
		File userConf = new File(globusUserConf);
		File gridSec = new File (gridSecConf);
		try {
			src = getClass().getResourceAsStream(userSSLConfTmpl);
			DiskUtils.fCopy(src, userConf);
			txtHelper = new TxtFileHelper(globusUserConf);
			gridSecTxt = new TxtFileHelper(gridSecConf);
			String userBaseDN = gridSecTxt.getValue("SETUP_GSI_USER_BASE_DN", "=",
														"#");
			String[] userBase = userBaseDN.split(",");
			userBase[0] = (userBase[0].replaceAll("\"", "")).trim();
			String orgUnit[] = userBase[0].split("=");
			
			userBase[1] = (userBase[1].replaceAll("\"", "")).trim();
			String org[] = userBase[1].split("=");
			
			/*userBase[2] = (userBase[2].replaceAll("\"", "")).trim();
			String c[] = userBase[2].split("="); */
			
			txtHelper.changeValueAfter("=", "0.organizationName_default", 
					org[1]);
			txtHelper.changeValueAfter("=", "0.organizationalUnitName_default", 
					orgUnit[1]);
			//txtHelper.changeValueAfter("=", "countryName_default",c[1]);
			FileUtils.chgPermissions(globusUserConf, "644");
			/*int idx = globusUserConf.lastIndexOf(File.separatorChar);
			String destFile = globusUserConf.substring(idx);
			destFile = destFile.trim();
			idx = destFile.lastIndexOf(".");
			destFile = destFile.substring(0,idx);
			log.info("DEST FILE " + destFile);
			String link = gridSecurityDir + File.separatorChar + destFile;
			DiskUtils.forceDelete(link);
			if (!FileUtils.softLink(globusUserConf, gridSecurityDir, link)) {
				log.error("Error in creating soft link...");
				throw new GT4SecurityException("Error in creating soft link " +
						" to " + globusUserConf);
			}*/
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new GT4SecurityException(fnfe);			
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);			
		} finally {
			try {
				src.close();
			} catch (IOException ioe) {
				log.error("Error in closing File input stream for " + 
						hostSSLConfTmpl);
			}
		}
	}
}