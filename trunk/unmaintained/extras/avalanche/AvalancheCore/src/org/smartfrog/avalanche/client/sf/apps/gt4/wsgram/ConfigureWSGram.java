/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Feb 24, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.wsgram;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.gnubuild.GNUBuildException;
import org.smartfrog.avalanche.client.sf.apps.gt4.build.Installation;
import org.smartfrog.avalanche.client.sf.apps.gt4.prereqs.CheckCommand;
import org.smartfrog.avalanche.client.sf.apps.gt4.security.SecurityConstants;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigureWSGram {
	private String globusLoc;
	private static Log log = LogFactory.getLog(ConfigureWSGram.class);
	
	/**
	 * 
	 */
	public ConfigureWSGram(String globusLoc) {
		super();
		this.globusLoc = new String(globusLoc);
	}
	
	/**
	 * When the credentials of the service account and the job submitter 
	 * are different (multi user mode), then GRAM will prepend a call to 
	 * sudo to the local adapter callout command.
	 * @param userNames - list of users to be allowed. If userNames is not
	 * 			provided, it gives permission to all the users.
	 * @throws WSGramException
	 */
	public void configureSudo(String myUserName, String[] userNames) throws WSGramException {
		//String myUserName = System.getProperty("user.name");
		String unames = null;
		if (userNames.length == 0) {
			unames = "ALL";
		}	
		else {
			unames = userNames[0];
			for (int i=1; i<userNames.length; i++) {
				unames = unames + "," + userNames[i];
			}
		}
		unames = "ALL=(" + unames + ")";
		String gridmapExec = globusLoc + File.separatorChar + "libexec" + 
						File.separatorChar + "globus-gridmap-and-execute";
		String jobMgrScript = globusLoc + File.separatorChar + "libexec" +
						File.separatorChar + "globus-job-manager-script.pl";
		String localProxyTool = globusLoc + File.separatorChar + "libexec" +
						File.separatorChar + "globus-gram-local-proxy-tool";
		
		String sudoEntry1 = myUserName + "\t" + unames + "\tNOPASSWD: " +
			gridmapExec + " -g " + SecurityConstants.gridMapFile + 
			" " + jobMgrScript + " *";
		
		String sudoEntry2 = myUserName + "\t" + unames + "\tNOPASSWD: " +
			gridmapExec + " -g " + SecurityConstants.gridMapFile +
			" " + localProxyTool + " *";
		
		File sudoers = new File("/etc/sudoers");
		try {
			FileUtils.appendString2File(sudoers, sudoEntry1);
			FileUtils.appendString2File(sudoers, sudoEntry2);
		} catch (IOException ioe) {
			log.error("Error while editing /etc/sudoers file", ioe);
			throw new WSGramException("Error while editing /etc/sudoers file", ioe);
		}			
	}
	
	
	/**
	 * Checks if local scheduler is in path.
	 * @return true if scheduler is in path; else false
	 */
	public void checkScheduler(String schdCmd, String options) throws
			WSGramException {
		CheckCommand check = new CheckCommand();
		try {
			check.cmdInPath(schdCmd, options);
		} catch (IOException ie) {
			log.error("Error while checking if scheduler is in PATH",ie);
			throw new WSGramException("Error while checking if scheduler is in PATH",ie);
		} catch (InterruptedException ine) {
			log.error("Error while checking if scheduler is in PATH",ine);
			throw new WSGramException("Error while checking if scheduler is in PATH",ine);
		}		
	}
	
	public void installschedulerAdapter(String installerDir, String target) throws WSGramException {
		try {
		Installation sa = new Installation(installerDir, globusLoc);		
		sa.build(target);
		sa.build("install");
		} catch (GNUBuildException gbe) {
			log.error(gbe);
			throw new WSGramException("Error in installing scheduler adapter", gbe);
		}
	}
	
	public void remoteShellAccessForPBS() throws WSGramException {
		String cmd = globusLoc + "setup/globus/setup-globus-job-manager-pbs --remote-shell=rsh";
		cmd = cmd.replace('/', File.separatorChar);
		cmd = cmd.replace('\\', File.separatorChar);
		
		Process p = null;
		Runtime rt = Runtime.getRuntime();
		int exitVal = 0;
		try {
			p = rt.exec(cmd);
			exitVal = p.waitFor();
		} catch (IOException ie) {
			log.error("Error while configuring remote shell for rsh access for PBS", ie);
			throw new WSGramException("Error while configuring remote shell for rsh access for PBS", ie);
		} catch (InterruptedException ine) {
			log.error("Error while configuring remote shell for rsh access for PBS", ine);
			throw new WSGramException("Error while configuring remote shell for rsh access for PBS", ine);
		}
		
		if (exitVal != 0) {
			log.error("Error while configuring remote shell for rsh access for PBS..." +
					"returned with exit value : " + exitVal);
			throw new WSGramException("Error while configuring remote shell for rsh access for PBS..." +
					"returned with exit value : " + exitVal);
		}
		
		log.info("Configured remote shell for rsh access for PBS.");
	}
	
	public static void main(String args[]) {
		ConfigureWSGram wsgram = new ConfigureWSGram("/home/sandya/globus401");
		String[] unames = {"ALL"};
		try {
			//wsgram.configureSudo(unames);
			wsgram.checkScheduler("qsub", "");
			wsgram.installschedulerAdapter("/home/sandya/gt4.0.1-all-source-installer", "gt4-gram-pbs");
		} catch (WSGramException wsge) {
			log.error("Error : " + wsge);;
		}
	}
}