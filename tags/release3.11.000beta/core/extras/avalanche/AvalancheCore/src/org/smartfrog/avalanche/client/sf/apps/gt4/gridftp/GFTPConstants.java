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
package org.smartfrog.avalanche.client.sf.apps.gt4.gridftp;

import java.util.Hashtable;


/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GFTPConstants {
	public Hashtable osNames = null;
	public static String gftpServiceName = new String("gsiftp");
	public static String gftpServerName = new String("globus-gridftp-server");
	public static String xinetdGsiFTPStr = new String(
            "service " + gftpServiceName + "\n" +
            "{\n" +
            "instances               = 100\n" + 
            "socket_type             = stream\n" +
            "wait                    = no\n" +
            "user                    = root\n" +
            "env                     += GLOBUS_LOCATION=(globus_location)\n" +
            "env                     += LD_LIBRARY_PATH=(globus_location)/lib\n" +
            "server                  = (globus_location)/sbin/" + gftpServerName + "\n" +
            "server_args             = -i\n" +
            "log_on_success          += DURATION\n" +
            "nice                    = 10\n" +
            "disable                 = no\n" +
            "}");
	
	public static String inetdGsiFtpStr = new String(
			gftpServiceName + "\tstream\ttcp\tnowait\troot\t/usr/bin/env env "  +
			"GLOBUS_LOCATION=(globus_location) " +
			"LD_LIBRARY_PATH=(globus_location)/lib\t" +
			"(globus_location)/sbin/" + gftpServerName +  " -i");
	
	public static String xinetdGsiFtpFile = new String("/etc/xinetd.d/gridftp");
	public static String inetdConfFile = new String("/etc/inetd.conf");
	public static String servicesFile = new String("/etc/services");
	public static String xinetd = new String("/etc/init.d/xinetd");
	
	public static final int LINUX = 1;
	public static final int HPUX = 2;
	
	public GFTPConstants() {
		osNames = new Hashtable();
		osNames.put("Linux", new Integer(LINUX));
		osNames.put("HP-UX", new Integer(HPUX));		
	}
}
