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
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;
import org.smartfrog.avalanche.client.sf.apps.utils.TxtFileHelper;
import org.smartfrog.avalanche.client.sf.disk.DiskUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigureGridFTP {
	private String globusLoc = null;
	private static final Log log = LogFactory.getLog(ConfigureGridFTP.class);

	/**
	 * 
	 */
	public ConfigureGridFTP(String gLoc) {
		super();
		// TODO Auto-generated constructor stub
		globusLoc = new String(gLoc);
	}	
	
	public boolean checkGFTP() {
		char separator = File.separatorChar;
		String server = new String (globusLoc + separator +
				"sbin" + separator + GFTPConstants.gftpServerName);
		File gftpServer = new File(server);
		if (!gftpServer.exists()) {
			log.error("Grid FTP is not installed");
			return false;
		}
		return true;
	}
	
	public boolean configureWithXinetd(int port) 
		throws IOException, GFTPException {
		
		if (globusLoc == null) {
			log.error("Globus Location is not specified. Cannot proceed");
			return false;			
		}
		
		if (!checkGFTP()) {
			log.error("GridFTP is not installed");
			return false;
		}
		
		GFTPConstants.xinetdGsiFTPStr = GFTPConstants.xinetdGsiFTPStr.replaceAll("\\(globus_location\\)", globusLoc);
		GFTPConstants.xinetdGsiFTPStr = GFTPConstants.xinetdGsiFTPStr.replace('\\', File.separatorChar);
        GFTPConstants.xinetdGsiFTPStr = GFTPConstants.xinetdGsiFTPStr.replace('/', File.separatorChar);
		//log.info(str);
		
		GFTPConstants.xinetdGsiFtpFile = GFTPConstants.xinetdGsiFtpFile.replace('\\', File.separatorChar);
		GFTPConstants.xinetdGsiFtpFile = GFTPConstants.xinetdGsiFtpFile.replace('/', File.separatorChar);
		
		File gsiFtp = new File(GFTPConstants.xinetdGsiFtpFile);
		if (!gsiFtp.exists()) {
			if (!FileUtils.createFile(gsiFtp)) {
				log.error("Cannot create file " + GFTPConstants.xinetdGsiFtpFile);
				throw new GFTPException("Cannot create file " + GFTPConstants.xinetdGsiFtpFile);
			}
		}
		
		if (!FileUtils.checkFile(gsiFtp)) {
			log.error("GridFTP cannot be configured");
			throw new GFTPException("Error in accessing file " + gsiFtp.getAbsolutePath());
		}
		
		if (!FileUtils.writeString2File(GFTPConstants.xinetdGsiFTPStr, gsiFtp)) {
			log.error("Error in writing to file " + 
					GFTPConstants.xinetdGsiFtpFile);
			// clean partial configuration in case of failure
			DiskUtils.forceDelete(gsiFtp);
			
			throw new GFTPException("Error in writing to file " + 
					GFTPConstants.xinetdGsiFtpFile);
		}
		
		String line = GFTPConstants.gftpServiceName + "\t\t" + port + "/tcp";
		GFTPConstants.servicesFile = GFTPConstants.servicesFile.replace('\\', File.separatorChar);
		GFTPConstants.servicesFile = GFTPConstants.servicesFile.replace('/', File.separatorChar);
		File etcServices = new File(GFTPConstants.servicesFile);
		
		TxtFileHelper txt = new TxtFileHelper(GFTPConstants.servicesFile);
		if (!txt.deleteLine(GFTPConstants.gftpServiceName)) {
			log.error("Could not delete duplicate " + 
					GFTPConstants.gftpServiceName + " entry in services file");
			throw new GFTPException("Could not delete duplicate " + 
					GFTPConstants.gftpServiceName + " entry in services file");			
		}
		
		FileUtils.appendString2File(etcServices, line);
		
		if (!restartXinetd()) {
			log.error("Could not start GridFTP.");
			if (!txt.deleteLine(GFTPConstants.gftpServiceName)) {
				log.warn("Could not delete duplicate " + 
						GFTPConstants.gftpServiceName + " entry in services file");							
			}
			throw new GFTPException("Could not restart xinetd to " +
					"start GridFTP");
		}			
		return true;
	}
	
	public boolean restartXinetd() throws IOException {
		Runtime rt = Runtime.getRuntime();
		
		String cmd = GFTPConstants.xinetd + " restart";
		Process p = rt.exec(cmd);
		
		try {
			if (p.waitFor() != 0) {
				log.error("Failed to restart xinetd");
				return false;								
			}
		} catch (InterruptedException ie) {
			log.error(ie);
			return false;
		}		
		return true;
	}
	
	public boolean configureWithInetd(int port) 
			throws IOException, GFTPException {
		if (globusLoc == null) {
			log.error("Globus Location is not specified. Cannot proceed");
			throw new GFTPException("Globus Location is not specified. Cannot proceed");			
		}
		
		if (!checkGFTP()) {
			log.error("GridFTP is not installed");
			throw new GFTPException("GridFTP is not installed");
		}
		
		GFTPConstants.inetdGsiFtpStr = GFTPConstants.inetdGsiFtpStr.replaceAll("\\(globus_location\\)", "/mnt/misc/gt401");
        GFTPConstants.inetdGsiFtpStr = GFTPConstants.inetdGsiFtpStr.replace('\\', File.separatorChar);
        GFTPConstants.inetdGsiFtpStr = GFTPConstants.inetdGsiFtpStr.replace('/', File.separatorChar);
		
        GFTPConstants.inetdConfFile = GFTPConstants.inetdConfFile.replace('\\', File.separatorChar);
        GFTPConstants.inetdConfFile = GFTPConstants.inetdConfFile.replace('/', File.separatorChar);
        File inetdConf = new File(GFTPConstants.inetdConfFile);
        if (!FileUtils.checkFile(inetdConf)) {
			log.error("GrifFTP cannot be configured");
			throw new GFTPException("GrifFTP cannot be configured");
		}
        
        if (!FileUtils.writeString2File(GFTPConstants.inetdGsiFtpStr, inetdConf)) {
			log.error("Error in writing to file " + 
					GFTPConstants.inetdConfFile);
			throw new GFTPException("Error in writing to file " + 
					GFTPConstants.inetdConfFile);
		}
		
        String line = GFTPConstants.gftpServiceName + "\t" + port + "/tcp";
		GFTPConstants.servicesFile = GFTPConstants.servicesFile.replace('\\', File.separatorChar);
		GFTPConstants.servicesFile = GFTPConstants.servicesFile.replace('/', File.separatorChar);
		File etcServices = new File(GFTPConstants.servicesFile);
		
		TxtFileHelper txt = new TxtFileHelper(GFTPConstants.servicesFile);
		if (!txt.deleteLine(GFTPConstants.gftpServiceName)) {
			log.error("Could not delete duplicate " + 
					GFTPConstants.gftpServiceName + " entry in services file");
			throw new GFTPException("Could not delete duplicate " + 
					GFTPConstants.gftpServiceName + " entry in services file");			
		}
		
		FileUtils.appendString2File(etcServices, line);
		
		if (!restartInet()) {
			log.error("Cannot start Grid FTP.");
			if (!txt.deleteLine(GFTPConstants.gftpServiceName)) {
				log.warn("Could not delete duplicate " + 
						GFTPConstants.gftpServiceName + " entry in services file");
			}
			throw new GFTPException("Could not restart inet service. " + 
					"Hence gridFTP is not started");
		}                
		return true;
	}
	
	public boolean restartInet() throws IOException {
		String os = System.getProperty("os.name");
		Runtime rt = Runtime.getRuntime();
		
		String cmd = null;
		Process p = null;
		GFTPConstants gftpConst = new GFTPConstants();
		int osCode = ((Integer)(gftpConst.osNames.get(os))).intValue();
		switch (osCode) {
			case GFTPConstants.LINUX:
				cmd = new String ("/etc/rc.d/init.d/inetd restart");
				cmd = cmd.replace('\\', File.separatorChar);
				cmd = cmd.replace('/', File.separatorChar);
				
				p = rt.exec(cmd);
				try {
					if (p.waitFor() != 0) {
						log.error("Failed to restart inetd");
						return false;								
					}
				} catch (InterruptedException ie) {
					log.error(ie);
					return false;
				}
				break;
				
			case GFTPConstants.HPUX:
				cmd = new String ("inetd -c");
				p = rt.exec(cmd);
				try {
					if (p.waitFor() != 0) {
						log.error("Failed to restart inetd");
						return false;								
					}
				} catch (InterruptedException ie) {
					log.error(ie);
					return false;
				}
				break;
				
			default:
				log.error("Currently " + os + " is not supported");
				return false;				
		}
		return true;
	}
	// TODO Include code to run from the container
	
	public static void main(String args[]) {
		ConfigureGridFTP gftp = new ConfigureGridFTP("/home/sandya/globus401");
		
		try {
			gftp.configureWithXinetd(2811);
			gftp.restartXinetd();
		} catch (IOException ioe) {
			log.error(ioe);
		} catch (GFTPException gftpe) {
			log.error(gftpe);
		}
	}
}
