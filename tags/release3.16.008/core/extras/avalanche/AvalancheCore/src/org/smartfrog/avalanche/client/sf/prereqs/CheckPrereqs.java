/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Nov 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.prereqs;

import org.smartfrog.avalanche.client.sf.exec.ant.AntUtils;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CheckPrereqs extends CheckCommand implements PrereqConstants {
	private Hashtable tools = null; 
	private static Log log = LogFactory.getLog(CheckCommand.class);
	private String ver = new String();
			
	/**
	 * 
	 */
	public CheckPrereqs() {
		super();
		tools = new Hashtable();
		tools.put("java", new Integer(JAVA));
		tools.put("ant", new Integer(ANT));
		tools.put("cc", new Integer(CC));
		tools.put("tar", new Integer(TAR));
		tools.put("sed", new Integer(SED));
		tools.put("make", new Integer(MAKE));
		tools.put("sudo", new Integer(SUDO));
		tools.put("zlib", new Integer(ZLIB));
		tools.put("postgres", new Integer(POSTGRES));
	}
	
	public boolean checkCmd(String cmd, String reqVersion, String vendor) 
				throws IOException {
		return checkCmd(null, cmd, reqVersion, vendor);		
	}
	
	public boolean checkCmd(String cmd, String reqVersion) throws IOException {
		return checkCmd(null, cmd, reqVersion, null);
	}
	
	public boolean checkCmd(String cmd) throws IOException {
		return checkCmd(null, cmd, null, null);
	}
	
	/**
	 * Checks if the command cmd is in path, if it does not exist in path, 
	 * checks it in dir/bin if dir is defined. If cmd is found, then checks
	 * if version of this cmd is >= reqVersion. 
	 * 
	 * @param dir
	 * @param cmd
	 * @param reqVersion
	 * @param vendor
	 * @return
	 * @throws IOException
	 */
	public boolean checkCmd(String dir, String cmd, String reqVersion, String vendor)
				throws IOException {	
		int limit;
		
		if (null == cmd) {
			log.error("Pls provide the command to be checked!");
			return false;
		}
		
		String errStr = null;
		String cmdOut = null;
		
		int cmdCode = ((Integer)(tools.get(cmd))).intValue();
		boolean found  = true;
		switch (cmdCode) {
			case JAVA:
				if (!cmdInPath(cmd, javaVerOpt)) {
					found  = false;
					if (dir != null) {
						if (!cmdInDir(dir, cmd, javaVerOpt)) {
							found = false;				
						}
						else {
							found = true;
						}
						
					}					
				}
				if (!found) {
					log.error(cmd + " not found on the machine");
					return false;
				}
				
				
				if (null != cmdError) {
					errStr = cmdError.readLine();
				}
				
				/*if (null != cmdOutput) {
					cmdOut = cmdOutput.readLine();
				}*/
				
				if ((null != errStr) && (!errStr.startsWith("java version"))) {
					log.error("Error in executing " + cmd + " " + javaVerOpt);
					log.error(errStr);
					return false;
				}
				
				if (null == reqVersion) {
					return true;
				}
				
				String javaVersion = System.getProperty("java.version");
								
				if (!checkVersion(reqVersion,javaVersion)) {
					log.error("Java version needs to be >= " + reqVersion);
					return false;
				}
				log.info("Java Version is proper");
				break;
				
			case ANT:
				if (!cmdInPath(cmd, antVerOpt)) {
					found  = false;
					if (dir != null) {
						if (!cmdInDir(dir, cmd, antVerOpt)) {
							found = false;				
						}
						else
							found = true;
					}					
				}
				if (!found) {
					log.error(cmd + " not found on the machine");
					return false;
				}
								
				if (null != cmdError) {
					errStr = cmdError.readLine();
				}
				
				/*if (null != cmdOutput) {
					cmdOut = cmdOutput.readLine();
				}*/
				
				if (null != errStr) {
					log.error("Error in executing " + cmd + " " + antVerOpt);
					log.error(errStr);
					return false;
				}				
				
				if (null == reqVersion) {
					//log.error("Please provide the required version of the command " + cmd);
					return true;
				}
				
				String antVersion = AntUtils.getAntVersion();
				
				if (!checkVersion(reqVersion, antVersion)) {
					log.error("Ant version needs to be >= " + reqVersion);
					return false;
				}			
				log.info("Ant Version is proper"); 
				break;
				
			case CC:
				if (!cmdInPath(cmd, null)) {
					log.error(cmd + " not found in path");
					return false;
				}
				//TODO: 
				//Need to add code to check if gcc version is 3.2
				break;
				
			case TAR:
			case SED:
			case MAKE:
				if (!cmdInPath(cmd, gnuVerOpt)) {
					log.error(cmd + " not found in path");
					return false;
				}
				
				if (null != cmdError) {
					errStr = cmdError.readLine();
				}
				
				if (null != cmdOutput) {
					cmdOut = cmdOutput.readLine();
				}
				
				if (null != errStr) {
					log.error("Error in executing " + cmd + " "+ gnuVerOpt);
					log.error(errStr);
					return false;
				}
				
				if (null == cmdOut) {
					log.error("No output for the command " + cmd + " --version");
					return false;
				}
				
				if (!gnuVendor(vendor, cmdOut)) {
					log.error("Needs " + vendor + " " + cmd);
					return false;
				}
				log.info(cmd + " is in path");
				break;
				
			case SUDO:
				if (!cmdInPath(cmd, sudoVerOpt)) {
					log.error(cmd + " not found in path");
					return false;
				}
				
				if (null != cmdError) {
					errStr = cmdError.readLine();
				}
				
				if (null != cmdOutput) {
					cmdOut = cmdOutput.readLine();
				}
				
				if (null != errStr) {
					log.error("Error in executing " + cmd + " " + sudoVerOpt);
					log.error(errStr);
					return false;
				}
				
				if (null == cmdOut) {
					log.error("No output for the command " + cmd + " " + sudoVerOpt);
					return false;
				}
				log.info(cmd + " is in path");
				break;
				
			case POSTGRES:
				if (!cmdInPath(cmd, pgresVerOpt)) {
					found  = false;
					if (dir != null) {
						if (!cmdInDir(dir, cmd, pgresVerOpt)) {
							found = false;				
						}
						else
							found = true;
					}					
				}
				if (!found) {
					log.error(cmd + " not found on the machine");
					return false;
				}
								
				if (null != cmdError) {
					errStr = cmdError.readLine();
				}
				
				if (null != cmdOutput) {
					cmdOut = cmdOutput.readLine();
				}
				
				if (null != errStr) {
					log.error("Error in executing " + cmd + " " + pgresVerOpt);
					log.error(errStr);
					return false;
				}				
				
				if (reqVersion == null) {
					return true;
				}
				
				String[] arrStr = cmdOut.split("\\s");
				String pgresVer;
				if (arrStr.length == 3) {
					pgresVer = arrStr[2];									 
				}
				else {
					log.error("Error in reading output of " + cmd + " " + pgresVerOpt);
					return false;
				}
				
				if (!checkVersion(reqVersion, pgresVer)) {
					log.error("Postgres version needs to be >= " + reqVersion);
					return false;
				}			
				log.info("Postgres Version is proper"); 
				break;
				
			default:
				log.info("Checking of " + cmd + " is not yet included");			
				return false;				
		}				
		
		cmdError.close();
		cmdOutput.close();
		 
		return true;
	}
	
	private ArrayList getArrList(String version[]) {
		ArrayList al = new ArrayList();
		int n;
		
		for (int i=0; i<version.length; i++) {
			String v[] = version[i].trim().split("_");				
			for (int j=0; j<v.length; j++) {
				n = Integer.parseInt(v[j].trim());						
				al.add(new Integer(n));
			}					
		}
		
		if (al.isEmpty()) {
			return null;
		}
		
		/*Iterator iter = al.iterator();
		while (iter.hasNext()) {
			int num = ((Integer)iter.next()).intValue();					
			log.info("Elements: " + num + "\n");
		}*/
		
		return al;
	}
	
	public boolean checkVersion(String reqVersion, String currVersion) {
		String ver[]=null;
		String reqVer[] = null;
		ArrayList verArrList, reqVerArrList;
		int limit;
		
		ver = (currVersion.trim()).split("\\.");
		reqVer = reqVersion.split("\\.");
		verArrList = getArrList(ver);
		reqVerArrList = getArrList(reqVer);
		
		if (verArrList.size() < reqVerArrList.size()) {
			limit = verArrList.size();
		}
		else {
			limit = reqVerArrList.size();
		}
			
		for (int i=0; i<limit; i++) {
			int verNum = ((Integer)verArrList.get(i)).intValue();
			int reqVerNum = ((Integer)reqVerArrList.get(i)).intValue();
			
			if (verNum < reqVerNum) {
				return false;
			}
		}		
		return true;
	}	
		
	public boolean gnuVendor(String vendor, String cmdOut) {
		cmdOut = cmdOut.replaceAll("\\s", "").toUpperCase();
		if (cmdOut.indexOf(vendor.toUpperCase()) == -1) {			
			return false;
		}
		return true;
	}
	
	public static void main(String args[]) {
		CheckPrereqs chk = new CheckPrereqs();
		
		try {
			chk.checkCmd("java", "1.4.2_03", null);
			chk.checkCmd("ant", "1.6.1", null);
			chk.checkCmd("tar", null, "GNU");
			chk.checkCmd("sed", null, "GNU");
			chk.checkCmd("make", null, "GNU");
			chk.checkCmd("sudo", null, null);
			chk.checkCmd("postgres", "7.1");
		} catch(IOException ioe) {
			log.error("Exception : " + ioe);
		}
	}

}
