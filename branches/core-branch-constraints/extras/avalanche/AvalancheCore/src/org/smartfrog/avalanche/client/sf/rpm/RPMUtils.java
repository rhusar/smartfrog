/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.rpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author sandya
 * 
 * Contains RPM utilities to Install, Un-install and Upgrade RPM packages
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RPMUtils {
	private static Log log = LogFactory.getLog(RPMUtils.class);
	private Runtime rt = null;

	/**
	 * Constructor which initialises a Runtime object 
	 */
	public RPMUtils() {
		rt = Runtime.getRuntime();
	}

	/*
	 * Installs the RPM package without any install-options
	 * @param rpmPackage - full path of RPM Package to be installed
	 * @return boolean - returns true if Package is installed successfully
	 * 						else return false
	 * @throws IOException if an i/o error occurs 
	 */
	public boolean InstallPackage(String rpmPackage) throws IOException {
		String installOptions = "";
		return (InstallPackage(rpmPackage, installOptions));
	}
	
	/*
	 * Installs the RPM package in the install path provided.
	 * Note: Only relocatable RPM packages can use this method.
	 * @param rpmPackage - full path of RPM Package to be installed
	 * @param installPath - location where the package need to be installed
	 * @return boolean - returns true if Package is installed successfully
	 * 						else return false
	 * @throws IOException if an i/o error occurs 
	 */
	public boolean InstallPackage(String rpmPackage, File installPath)
			throws IOException {
		String installOptions = null;
		
		if (installPath.exists() && installPath.isDirectory()
				&& installPath.canWrite()) {
			String path = installPath.getCanonicalPath();
			installOptions = "--prefix " + path;
		}
		
		return InstallPackage(rpmPackage, installOptions);				
	}
	
	/*
	 * Installs RPM Package with the given install-options.
	 * @param rpmPackage - RPM package to be installed
	 * @param installOptions - RPM install options
	 * @return boolean - returns true if Package is installed successfully
	 * 						else return false
	 * @throws IOException if i/o error occurs 
	 */
	public boolean InstallPackage(String rpmPackage, String installOptions)
			throws IOException {
		Process p = null;
		String line;
		boolean error = false;

		File rpmFile = new File(rpmPackage);
		if (!rpmFile.exists()) {
			log.error("The file " + rpmPackage + " does not exist.");
			return false;
		}
		if (!rpmFile.isFile()) {
			log.error(rpmPackage + " is not a file");
			return false;
		}
		
		/*
		// Get the file extension to check if it is .rpm file
		String fileName = rpmFile.getName();
        String extn = null;
        int whereExtn = fileName.lastIndexOf('.');
        if ( 0 < whereExtn && whereExtn <= fileName.length()-2)
                extn = fileName.substring(whereExtn+1);
        
        if (!extn.equals("rpm")) {
        	log.error("The file " + rpmPackage + " is not RPM file.");
        	return false;
        }
        */

        // TODO: optimize this useless if-block  SFOS-222 
        String command = "rpm -i ";
		//if (installOptions.length() != 0) 
		if (installOptions != null) 
			command = "rpm -i " + installOptions;
		
		String cmd = command + " " + rpmPackage;		
		
		p = rt.exec(cmd);
		BufferedReader cmdError = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
		while ((line = cmdError.readLine()) != null) {
			error = true;
			log.error(line);			
		}
		cmdError.close();
		if (error)
			return false;
		
		BufferedReader cmdOutput = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		while ((line = cmdOutput.readLine()) != null) {
			error = true;
			log.error(line);
		}
		cmdOutput.close();
				
		if (!error) {
			log.info("The RPM package " + rpmPackage + " is successfully installed.");
			return true;
		}
		else
			return false;		
	}
	
	/*
	 * Un-installs the RPM package with out any erase-options
	 * @param packageName - RPM package name to be un-installed
	 * @return boolean - returns true if package is un-installed 
	 * 						successfully else returns false
	 * @throws IOException if i/o error occurs
	 */
	public boolean UninstallPackage(String packageName) throws IOException {
		String eraseOptions = "";
		
		return UninstallPackage(packageName, eraseOptions);
	}
	
	/*
	 * Un-installs the RPM Package with erase-options
	 * @param packageName - RPM package name to be un-installed
	 * @param eraseOptions - erase options to be used while un-installing
	 * @return boolean - returns true if package is un-installed 
	 * 						successfully else returns false
	 * @throws IOException if i/o error occurs
	 */
	public boolean UninstallPackage(String packageName, String eraseOptions)
			throws IOException {
		Process p = null;
		String line;

		String command = null;
		if (eraseOptions.length() != 0)
			command = "rpm -e " + eraseOptions + packageName;
		else
			command = "rpm -e " + packageName;
		
		
		p = rt.exec(command);
		BufferedReader cmdError = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
		while ((line = cmdError.readLine()) != null) {
			log.error(line);
			return false;
		}
		cmdError.close();			
				
		log.info("The RPM package " + packageName + " is successfully un-installed");
		return true;		
	}
	
	/*
	 * Upgrades the RPM package with out any upgrade-options. If a 
	 * previous version of the package is not found, it installs the 
	 * given package otherwise it removes all the older versions of 
	 * the package before upgrading.
	 * @param rpmPackage - Upgrades using the RPM package
	 * @return boolean - returns true if package is successfully upgraded
	 * 						else returns false
	 * @throws IOException if i/o exception occurs 
	 */
	public boolean UpgradePackage(String rpmPackage) throws IOException {
		String upgradeOptions = "";
		
		return UpgradePackage(rpmPackage, upgradeOptions);
	}
	
	/* Upgrades the RPM package with the given upgrade-options. If a 
	 * previous version of the package is not found, it installs the 
	 * given package otherwise it removes all the older versions of 
	 * the package before upgrading.
	 * @param rpmPackage - Upgrades using the RPM package
	 * @return boolean - returns true if package is successfully upgraded
	 * 						else returns false
	 * @throws IOException if i/o exception occurs
	 */
	public boolean UpgradePackage(String rpmPackage, String upgradeOptions)
			throws IOException {
		Process p = null;
		String line;
		boolean error = false;
				
		File rpmFile = new File(rpmPackage);
		if (!rpmFile.exists()) {
			log.error("The file " + rpmPackage + " does not exist.");
			return false;
		}
		if (!rpmFile.isFile()) {
			log.error(rpmPackage + " is not a file");
			return false;
		}
		
		/*
		// Get the file extension to check if it is .rpm file
		String fileName = rpmFile.getName();
        String extn = null;
        int whereExtn = fileName.lastIndexOf('.');
        if ( 0 < whereExtn && whereExtn <= fileName.length()-2)
                extn = fileName.substring(whereExtn+1);
        
        if (!extn.equals("rpm")) {
        	log.error("The file " + rpmPackage + " is not RPM file.");
        	return false;
        }
        */
        
        String command = "rpm -U ";
		if (upgradeOptions.length() != 0) 
			command = "rpm -U " + upgradeOptions;
		
		String cmd = command + rpmPackage;		
		
		p = rt.exec(cmd);
		BufferedReader cmdError = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
		while ((line = cmdError.readLine()) != null) {
			error = true;
			log.error(line);			
		}
		cmdError.close();
		
		if (error)
			return false;
		
		BufferedReader cmdOutput = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		while ((line = cmdOutput.readLine()) != null) {
			error = true;
			log.error(line);
		}
			
		cmdOutput.close();		
		
		if (!error) {
			log.info("The RPM package " + rpmPackage + " is successfully upgraded");
			return true;
		}
		else
			return false;				
	}
		
}
