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
package org.smartfrog.avalanche.util;

import java.io.File;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BuildHelper {
	String sysPath;
	String javaHome;

	/**
	 * 
	 */
	public BuildHelper() {
		super();
		// TODO Auto-generated constructor stub
		sysPath = new String("/usr/bin:/bin:/usr/local/bin:/sbin");
		String jHome = System.getProperty("java.home");
		char separator = File.separatorChar;
		int idx = jHome.lastIndexOf("/");
		javaHome = jHome.substring(0,idx-1);
		String javaBin = javaHome+separator+"bin";
		sysPath = sysPath + javaBin;		
	}
	
	public void addEnvVariable(String envVar, String value) {
		value = value.replace('\\', File.separatorChar);
		value = value.replace('/', File.separatorChar);
		
		System.setProperty(envVar, value);	
	}
	
	public void add2Path(String binPath) {
		sysPath = sysPath + binPath;		
	}

}
