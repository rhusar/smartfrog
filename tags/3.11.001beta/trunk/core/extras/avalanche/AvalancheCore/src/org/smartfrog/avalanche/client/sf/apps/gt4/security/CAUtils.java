/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.gt4.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.client.sf.apps.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CAUtils extends SecurityConstants {
	private String caCert = null;
	private Runtime rt = null;
	
	private static Log log = LogFactory.getLog(CAUtils.class);
	/**
	 * 
	 */
	public CAUtils(String caCert) {
		super();
		// TODO Auto-generated constructor stub
		this.caCert = caCert;
		rt = Runtime.getRuntime();
	}
	
	public String getCASubject() throws GT4SecurityException {
		File tmpFile;
		try {
			tmpFile = File.createTempFile("CACert", ".0");
			FileUtils.writeString2File(caCert, tmpFile);			
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		}
		
		String cmd = openssl + " x509 -subject -noout -in " + 
				tmpFile.getAbsolutePath();
		
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		
		String subject = null;
		int exitVal = 0;
		try {
			Process p = rt.exec(cmd);
			cmdError = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			cmdOutput = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			exitVal = p.waitFor();
			if (exitVal != 0) {
				log.error("Error in getting CA subject...");
				String line = null;
				String error = null;
				if ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = line;
					while ((line = cmdError.readLine()) != null) {
						log.error(line);
						error = error + "\n" + line;
					}
					throw new GT4SecurityException(error);
				}
			}
			
			String line = null;
			if (null == (line = cmdOutput.readLine())) {
				log.error("Error in reading output of the command " + cmd);
				throw new GT4SecurityException("Error in reading output " +
						"of the command " + cmd);		
			}
			subject = line.substring(subjectStart.length());
			
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		} finally {
			try {
			cmdError.close();
			cmdOutput.close();
			tmpFile.delete();
			} catch (IOException e) {
				log.error(e);
				throw new GT4SecurityException(e);
			}
		}		
			
		//log.info("Subject: " + subject);
		return subject.trim();
		
	}
	
	public String getCAHash() throws GT4SecurityException {
		String hash = null;
		BufferedReader cmdError = null;
		BufferedReader cmdOutput = null;
		try {
			File tmpFile = File.createTempFile("CACert", ".0");
			tmpFile.deleteOnExit();
			FileUtils.writeString2File(caCert, tmpFile);
			
			String cmd = openssl + " x509 -hash -noout -in " +
					tmpFile.getAbsolutePath();
			
			Process p = rt.exec(cmd);
			cmdError = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			cmdOutput = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			
			int exitVal = p.waitFor();
			if (exitVal != 0) {
				log.error("Error in getting CA hash...");
				String line = null;
				String error = null;
				if ((line = cmdError.readLine()) != null) {
					log.error(line);
					error = line;
					while ((line = cmdError.readLine()) != null) {
						log.error(line);
						error = error + "\n" + line;
					}
					throw new GT4SecurityException(error);
				}
			}
			
			if (null == (hash = cmdOutput.readLine())) {
				log.error("Error in reading output of the command " + cmd);
				throw new GT4SecurityException("Error in reading " +
						"output of the command " + cmd);
			}			
		} catch (IOException ioe) {
			log.error(ioe);
			throw new GT4SecurityException(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new GT4SecurityException(ie);
		}finally {
			try {
				cmdError.close();
				cmdOutput.close();
			} catch (IOException e) {
				log.error(e);
				throw new GT4SecurityException(e);
			}
		}		
		log.info("Hash: " + hash);
		return hash.trim();		
	}
}
