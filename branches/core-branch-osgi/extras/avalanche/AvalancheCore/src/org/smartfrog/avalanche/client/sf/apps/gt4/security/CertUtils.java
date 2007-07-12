/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CertUtils {
	private static Log log = LogFactory.getLog(CertUtils.class);
	private String globusLoc = null;
	private String globusBin = null;
	private Runtime rt = null;

	/**
	 * 
	 */
	public CertUtils(String globusLoc) {
		super();
		// TODO Auto-generated constructor stub
		this.globusLoc = globusLoc;
		this.globusBin = globusLoc + File.separatorChar + "bin";
		rt = Runtime.getRuntime();
	}
	
	public void gridCertReq(Properties props, String user) 
			throws GT4SecurityException {
		String cmd = new String(globusBin + File.separatorChar + 
				"grid-cert-request");
		Process p = null;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;

		String userHome = null;
		if (user != null)
			userHome =File.separatorChar + "home" + File.separatorChar + user;
		else
			userHome = System.getProperty("user.home");	
		
		String[] envs = {"GLOBUS_LOCATION="+globusLoc, "HOME="+userHome};
		
		try {
			if (null == props) {
				log.info("No arguments passed for grid-cert-req ...");
				props = new Properties();
				props.setProperty("-force", "");
				p = rt.exec(cmd, envs, null);
			}
			else {
				String args = null;
				props.setProperty("-force", "");
				Enumeration e = props.keys();
				String val = null;
				String k = null;
				if (e.hasMoreElements()) {
					k = (String)e.nextElement();
					val = (String)props.getProperty(k);
					args = k + " " + val;
				}
				log.info("DEBUG:  " + args);
				while (e.hasMoreElements()) {
					k = (String)e.nextElement();
					val = (String)props.getProperty(k);					
					args = args + " " + k + " " + val + " ";
				}
				cmd = cmd + " " + args;
				log.info(cmd);
				p = rt.exec(cmd, envs, null);
			}
			cmdError = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			cmdOutput = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			int exitVal = 0;
			exitVal = p.waitFor();
			log.info("Exit Value : " + exitVal);
			if (exitVal != 0) {
				String line = null;
				String error = null;
				if (null != props.getProperty("-host"))
					error = "Error in requesting host certificate";
				else
					error = "Error in requesting user certificate";
				while ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = error + "\n" + line;
				}
				while ((line = cmdOutput.readLine()) != null) {
					log.info(line);
					error = error + "\n" + line;
				}
				throw new GT4SecurityException(error);
			}
			String out = null;
			while ((out = cmdOutput.readLine()) != null) {
				log.info(out);			
			}
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		}
		
	}
	
	/*public String getSignedCert(File certReq) 
			throws GT4SecurityException {
		CAServiceImpl ca = new CAServiceImpl();
		String certReqStr = null;
		try {
			if ((certReqStr = FileUtils.file2String(certReq)) == null) {
				log.error("Error in accessing file : " +
						certReq.getAbsolutePath());
				throw new GT4SecurityException("Error in accessing file : " +
						certReq.getAbsolutePath());
			}
		} catch (FileNotFoundException fnfe) {
			log.error(fnfe);
			throw new GT4SecurityException(fnfe);
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		}
		
		String signedCertStr = null;
		try {
			signedCertStr = ca.signCert(certReqStr, "sandya");
			if (null == signedCertStr) {
				log.error("Error in signing certificate...");
				throw new GT4SecurityException(
						"Error in signing certificate...");
			}
		} catch (CAException cae) {
			log.error(cae);
			throw new GT4SecurityException(cae);
		}
		return signedCertStr;
	}*/
}
