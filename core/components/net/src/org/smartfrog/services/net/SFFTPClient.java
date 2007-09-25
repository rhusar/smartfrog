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

package org.smartfrog.services.net;

import org.smartfrog.sfcore.prim.Prim;

/**
 * SmartFrog FTP client Interface. 
 * @author Ashish Awasthi
 */ 
public interface SFFTPClient extends Prim {
    
    //SmartFrog attributes for the FTPClient component
    public static String FTP_HOST = "ftpHost";
    public static String FTP_PORT = "port";
    public static String USER = "username";
    public static String PASSWORD_FILE = "passwordFile";
    public static String TRANSFER_MODE = "transferMode";
    public static String TRANSFER_TYPE = "transferType";
    public static String LOCAL_FILES = "localFiles";
    public static String REMOTE_FILES = "remoteFiles";
    public static String TERMINATE = "shouldTerminate";

    // messages
    public static String CONNECTION_FAILED = "CONNECTION_FAILED";
}
