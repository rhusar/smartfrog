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
package org.smartfrog.services.filesystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * created 27-May-2004 10:43:10
 */

public interface FileIntf extends UriIntf,Remote {

    /*
    filename extends OptionalString;
    dir extends OptionalString;
    mustExist extends OptionalBoolean;

    exists: Boolean
    absolutePath: String
    URI: String
    */

    String varFilename="filename";
    String varDir="dir";
    String varExists="exists";
    String varAbsolutePath="absolutePath";
    String varIsDirectory="isDirectory";
    String varIsFile = "isFile";
    String varIsHidden = "isHidden";
    String varTimestamp = "timestamp";
    String varLength = "length";
    String varIsEmpty = "isEmpty";
    String varShortname="shortname";
    String varMustExist = "mustExist";
    String varMustRead = "mustRead";
    String varMustWrite = "mustWrite";
    String varMustBeFile = "mustBeFile";
    String varMustBeDir = "mustBeDir";
    String varTestOnStartup = "testOnStartup";
    String varTestOnLiveness = "testOnLiveness";

    /**
     * get the absolute path of this file
     * @return
     * @throws RemoteException
     */
    public String getAbsolutePath() throws RemoteException;

}
