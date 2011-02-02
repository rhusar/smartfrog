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
package org.smartfrog.avalanche.client.sf.apps.gt4.build;

import java.util.ArrayList;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GT4Constants {
	private static String pathSep = System.getProperty("path.separator");
	public static String pathEnv = "/usr/bin" + pathSep + "/bin" + pathSep +
			"/usr/local/bin" + pathSep + "/usr/sbin" + pathSep + "/sbin";
	public static int defaultPathLen = pathEnv.length();
	public static ArrayList envp = new ArrayList();
	
	public GT4Constants() {		
	}
	
	public static void setPath(String path) {
		pathEnv += pathSep  + path;		
	}
	
	public static String getPath() {		
		return pathEnv;
	}
	
	public static void setEnvp(String env) {
		envp.add(env);
	}
}
