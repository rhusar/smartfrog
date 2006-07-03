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

package org.smartfrog.sfcore.logging;


import java.rmi.Remote;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.SmartFrogLogException;


public interface LogRegistration extends Remote {

    /**
     * Log Registration interface
     *
     * @param name log name
     * @param log logger to register
     * @throws SmartFrogLogException  if failed to register
     * @throws RemoteException in case of remote/network error
     */
   public void register(String name,Log log)  throws RemoteException, SmartFrogLogException;

    /**
     *  Log Registration interface
     * @param name log name
     * @param log logger to register
     * @param logLevel  log level
     * @throws RemoteException in case of remote/network error
     * @throws SmartFrogLogException if failed to register
     */
    public void register(String name,Log log, int logLevel)  throws RemoteException, SmartFrogLogException;

    /**
     *  Log Deregistration interface
     * @param name log name
     * @return  boolean success/failure
     * @throws SmartFrogLogException if failed to deregister
     * @throws RemoteException in case of remote/network error
     */
   public boolean deregister(String name)  throws RemoteException, SmartFrogLogException;

}
