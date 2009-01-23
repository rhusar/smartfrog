/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Dec 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.apps.ca;

import java.io.File;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CAConstants {
	//public static String caDir = "/usr/local/grit/ca";
	//public static String opensslDir = "/usr/local/grit/openssl";
	public static String caDir = null;
	public static String opensslDir = null;
	public static String confFileTmpl = "/org/smartfrog/avalanche/client/" +
										"sf/apps/ca/openssl.cnf.tmpl";
	public static String gridConfTmpl = "/org/smartfrog/avalanche/client/" +
										"sf/apps/ca/grid-security.conf.tmpl";
	public static String confFile = null;
	public static String gridConfFile = null; 
	public static String caCertFile = "cacert.pem";
	public static String opensslCmd = "openssl";
		
	public static String dir = "dir";
	public static String newCertsDir = "new_certs_dir";
	public static String PublicCert = "certificate";
	public static String comment = "#";
	public static String separator = "=";
	public static String variableDecl = "\\$";
	
	public static String signedCert = "signed.pem";
	
	public CAConstants(String caDirectory, String opensslDirectory) {
		caDirectory = caDirectory.replace('\\', File.separatorChar);
		caDirectory = caDirectory.replace('/', File.separatorChar);
		caDir = new String(caDirectory);
		
		opensslDirectory = opensslDirectory.replace('\\', File.separatorChar);
		opensslDirectory = opensslDirectory.replace('/', File.separatorChar);
		opensslDir = new String(opensslDirectory);
		
		confFile = new String(opensslDir + "/openssl.cnf");
		confFile = confFile.replace('\\', File.separatorChar);
		confFile = confFile.replace('/', File.separatorChar);
		
		confFileTmpl = confFileTmpl.replace('\\', File.separatorChar);
		confFileTmpl = confFileTmpl.replace('/', File.separatorChar);
		
		gridConfTmpl = gridConfTmpl.replace('\\', File.separatorChar);
		gridConfTmpl = gridConfTmpl.replace('/', File.separatorChar);
		
		gridConfFile = new String(caDir + "/grid-security.conf.__HASH__");
		gridConfFile = gridConfFile.replace('\\', File.separatorChar);
		gridConfFile = gridConfFile.replace('/', File.separatorChar);
	}
}
