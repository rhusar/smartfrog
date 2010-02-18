/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Mar 16, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
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
public class SystemUtils {
	private static Log log = LogFactory.getLog(SystemUtils.class);

	/**
	 * 
	 */
	public SystemUtils() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static void addUser(String user, Properties props) 
			throws UtilsException {
		
		if ((null == user) || (user.length() == 0)) {
			log.error("User name cannot be null");
			throw new UtilsException("User name cannot be null");
		}
		
		String cmd = "adduser";
		
		if ((null == props) || (props.isEmpty())) {
			log.error("Please provide user information....");
			throw new UtilsException("Please provide user information....");
		}
		
		String passwd = (String)props.getProperty("-p");
		if ((null == passwd) || (passwd.length() == 0)) {
			log.error("Password is not provided....Cannot create user");
			throw new UtilsException("Password is not provided...." +
					"Cannot create user");			
		}
		String epasswd = jcrypt.crypt("Tb", passwd);
		if (epasswd.length() == 0) {
			log.error("Error in encrypting passwd...");
			throw new UtilsException("Error in encrypting passwd...");
		}
		props.setProperty("-p", epasswd);		
		props.setProperty("-c", "User-"+user);
		
		String key = null;
		String value = null;
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			key = (String)e.nextElement();
			value = (String)props.getProperty(key);
			if ((null != value) || (value.length() != 0))
				cmd += " " + key + " " + value;				
			else
				cmd += " " + key;					
		}
		
		cmd += " " + user;
				
		Process p = null;
		Runtime rt = Runtime.getRuntime();
		BufferedReader cmdError = null;
		int exitVal = 0;
		try {
			p = rt.exec(cmd);
			exitVal = p.waitFor();
			cmdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			if (exitVal != 0) {
				String err = "Error in adding user " + user + "... returned with exit value 0 : ";
				log.error(err);
				String line = null;
				while ((line = cmdError.readLine()) != null) {
					log.error(line);
					err += line;
				}				
				throw new UtilsException(err);
			}
		} catch (IOException ioe) {
			log.error(ioe);
			throw new UtilsException(ioe);
		} catch (InterruptedException ie) {
			log.error(ie);
			throw new UtilsException(ie);
		}
		
		log.info("User " + user + " added successfully");
	}
	
	public static String os() {
		return System.getProperty("os.name");
	}
	
	public static String arch() {
		return System.getProperty("os.arch");
	}
	
	public static void main(String args[]) {
		Properties props = new Properties();		
		props.setProperty("-d", "/home/test");
		props.setProperty("-m", "");
		props.setProperty("-s", "/bin/bash");
		props.setProperty("-p", "testUser");
		
		try {
			SystemUtils.addUser("test", props);
		} catch (UtilsException ue) {
			log.error("Error in creating user", ue);
		}
	}
}


