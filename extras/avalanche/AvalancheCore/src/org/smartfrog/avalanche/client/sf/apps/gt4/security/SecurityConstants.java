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

import java.io.File;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SecurityConstants {
	public static String gridSecurityDir = "/tmp";
	public static String certificateDir = "/tmp";
	public static String openssl = "openssl";
	public static String subjectStart = "subject= ";
	
	public static String signingPolicyTmpl = 
		"/org/smartfrog/avalanche/client/sf/apps/gt4/security/" +
			"ca-signing-policy.conf.tmpl";
	public static String hostSSLConfTmpl = 
		"/org/smartfrog/avalanche/client/sf/apps/gt4/security/" +
			"globus-host-ssl.conf.tmpl";
	public static String userSSLConfTmpl = 
		"/org/smartfrog/avalanche/client/sf/apps/gt4/security/" +
			"globus-user-ssl.conf.tmpl";
	public static String gridSecConf = certificateDir + "/grid-security.conf." +
				"__HASH__";
	public static String globusHostConf = certificateDir + 
			"/globus-host-ssl.conf." + "__HASH__";
	public static String globusUserConf = certificateDir + 
			"/globus-user-ssl.conf." + "__HASH__";
	
	public static String hostCertReq = "/etc/grid-security/hostcert_request.pem";
	public static String hostCert = "/etc/grid-security/hostcert.pem";
	public static String hostKey = "/etc/grid-security/hostkey.pem";
	
	public static String gridMapFile = "/etc/grid-security/grid-mapfile";
	
	public static String containerKey = "/etc/grid-security/containerkey.pem";
	public static String containercert = "/etc/grid-security/containercert.pem";
	
	public static String userCertReq = System.getProperty("user.home") +
				File.separatorChar + ".globus" + File.separatorChar + 
				"usercert_request.pem";
	public static String userCert = System.getProperty("user.home") +
				File.separatorChar + ".globus" + File.separatorChar + 
				"usercert.pem";
	
	
	/**
	 * 
	 */
	public SecurityConstants() {
		super();
		// TODO Auto-generated constructor stub
		gridSecurityDir = gridSecurityDir.replace('\\', File.separatorChar);
		gridSecurityDir = gridSecurityDir.replace('/', File.separatorChar);
		
		signingPolicyTmpl = signingPolicyTmpl.replace('\\', File.separatorChar);
		signingPolicyTmpl = signingPolicyTmpl.replace('/', File.separatorChar);
		
		hostSSLConfTmpl = hostSSLConfTmpl.replace('\\', File.separatorChar);
		hostSSLConfTmpl = hostSSLConfTmpl.replace('/', File.separatorChar);
		
		userSSLConfTmpl = userSSLConfTmpl.replace('\\', File.separatorChar);
		userSSLConfTmpl = userSSLConfTmpl.replace('/', File.separatorChar);
		
		gridSecConf = gridSecConf.replace('\\', File.separatorChar);
		gridSecConf = gridSecConf.replace('/', File.separatorChar);
		
		globusHostConf = globusHostConf.replace('\\', File.separatorChar);
		globusHostConf = globusHostConf.replace('/', File.separatorChar);	
		
		globusUserConf = globusUserConf.replace('\\', File.separatorChar);
		globusUserConf = globusUserConf.replace('/', File.separatorChar);		
	}

}
