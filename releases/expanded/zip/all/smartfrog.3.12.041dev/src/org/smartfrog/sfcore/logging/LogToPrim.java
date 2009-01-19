/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.logging;

import java.rmi.RemoteException;


/**
 *
 *  Logs log info into a prim that implements Log interface
 *
 */

public interface LogToPrim extends LogToStreams {
   //Configuration parameters
   /** String name for optional attribute "{@value}". */
    final static String ATR_LOG_TO = "logTo";
    /** String name for optional attribute "{@value}". */
    final static String ATR_TAG_MESSAGE = "tagMessage";
    /** String name for optional attribute "{@value}". */
    final static String ATR_DEBUG = "debug";

    /**
     * Get the destination log
     * @return the destination for logging messages.
     * @throws RemoteException in case of remote/network error
     */
    public LogRemote getLogTo() throws RemoteException;
}
