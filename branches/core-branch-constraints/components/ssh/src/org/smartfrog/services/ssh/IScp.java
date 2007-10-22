/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.ssh;

import org.smartfrog.sfcore.prim.Prim;

/**
 * SmartFrog Interface for SCP component. Used to upload/download files 
 * to/from a remote machine securely over SSH.
 * @author Ashish Awasthi
 */
public interface IScp extends Prim {
    //SmartFrog attributes names
    public static String HOST = "host";
    public static String PORT = "port";
    public static String USER = "username";
    public static String KEYFILE = "keyfile";
    public static String PASSWORD = "password";
    public static String TIMEOUT = "timeout";
    public static String TERMINATE = "shouldTerminate";
    public static String TRUST_ALL_CERTIFICATES = "trustAllCertificates";
    public static String KNOWN_HOSTS = "knownHosts";
    public static String LOG_FILE = "logFile";
    public static String LOCAL_FILES = "localFiles";
    public static String REMOTE_FILES = "remoteFiles";
    public static String TRANSFER_TYPE = "transferType";
}

